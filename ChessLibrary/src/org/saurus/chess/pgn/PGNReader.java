package org.saurus.chess.pgn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.saurus.chess.pgn.Move.CastleType;
import org.saurus.tokenizer.Token;
import org.saurus.tokenizer.Token.TokenType;
import org.saurus.tokenizer.Tokenizer;

public class PGNReader {
	public static final Token START_TAG = new Token(TokenType.TOKEN, "[");
	public static final Token END_TAG = new Token(TokenType.TOKEN, "]");
	public static final Token DOT = new Token(TokenType.TOKEN, ".");
	public static final Token RESULT_UNDECIDED = new Token(TokenType.TOKEN, "*");
	public static final Token RESULT_WHITE_WINS = new Token(TokenType.SYMBOL, "1-0");
	public static final Token RESULT_BLACK_WINS = new Token(TokenType.SYMBOL, "0-1");
	public static final Token RESULT_DRAW = new Token(TokenType.SYMBOL, "1/2-1/2");

	private boolean debug;

	public static void setFEN(Board board, String fen) {
		String[] parts = fen.split("\\s+");
		String[] rows = parts[0].split("/");

		for (int i = 0; i < rows.length; i++) {
			String row = rows[i];
			int rank = 8 - i;
			int fileInt = 1;

			for (int j = 0; j < row.length(); j++) {
				char ch = row.charAt(j);
				if (Character.isDigit(ch))
					fileInt += Integer.parseInt("" + ch);
				else if (Character.isLetter(ch)) {
					Piece piece = new Piece(PieceType.fromChar(Character.toUpperCase(ch)), Character.isLowerCase(ch));
					Cell cell = new Cell(fileInt, rank);
					board.set(cell, piece);
					fileInt++;
				} else
					break;
			}

		}

		/*
		 * bool isBlack = false; if (parts[1] == "b") isBlack = true;
		 * 
		 * board.PlyCount = 0; int moves; if (parts.Length > 5 &&
		 * int.TryParse(parts[5], out moves)) board.PlyCount = 2*(moves - 1) +
		 * (isBlack?1:0);
		 * 
		 * return board;
		 */
	}

	// public List<Game> read(String content) {
	// Scanner scanner = new Scanner(content);
	// return read(scanner);
	// }

	public List<Game> read(String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.out.println("file not found " + path);
			return null;
		}
		try {
			return read(br);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}

	public List<Game> read(Reader stream) {
		try {
			return internalRead(stream);
		} catch (PGNException e) {
			System.out.println("ERROR: " + e.toString());
			return null;
		}
	}

	public Board fixupMoves(Game game) throws PGNException {
		Board board = new Board();

		String fen = game.getStartFEN();
		PGNReader.setFEN(board, fen);

		MoveList list = game.getMoves();

		for (MovePair mp : list) {
			Move move = mp.getWhite();

			if (move != null)
				fixupMove(board, move, false);

			move = mp.getBlack();

			if (move != null)
				fixupMove(board, move, true);
		}

		return board;
	}

	public Board getPosition(Game game, int plyMoves) throws PGNException {
		Board board = new Board();
		int plyCount = 0;

		String fen = game.getStartFEN();
		PGNReader.setFEN(board, fen);

		MoveList list = game.getMoves();

		for (MovePair mp : list) {
			Move move = mp.getWhite();

			if (move != null) {
				if (plyCount++ >= plyMoves)
					break;
				board.move(move);
			}

			move = mp.getBlack();

			if (move != null)
			 {
				if (plyCount++ >= plyMoves)
					break;
				board.move(move);
			}
		}

		return board;
	}

	private void fixupMove(Board board, Move move, boolean isBlack) throws PGNException {
		MoveGenerator mg = new MoveGenerator(board, isBlack);
		Piece piece = new Piece(move.getPiece(), isBlack);
		Cell startCell = move.getStartCell();
		Cell endCell = move.getEndCell();
		Move candidateMove = null;

		for (Move possibleMove : mg.AllMoves(piece)) {
			Cell possibleStartCell = possibleMove.getStartCell();
			Cell possibleEndCell = possibleMove.getEndCell();

			// check if this move is compatible with the move from game
			if (startCell != null && startCell.getFile() != Cell.UNDEFINED_FILE && startCell.getFile() != possibleStartCell.getFile())
				continue; // mismatch
			if (startCell != null && startCell.getRank() != Cell.UNDEFINED_RANK && startCell.getRank() != possibleStartCell.getRank())
				continue; // mismatch
			if (endCell != null && endCell.getFile() != Cell.UNDEFINED_FILE && endCell.getFile() != possibleEndCell.getFile())
				continue; // mismatch
			if (endCell != null && endCell.getRank() != Cell.UNDEFINED_RANK && endCell.getRank() != possibleEndCell.getRank())
				continue; // mismatch

			// FIXME other checks

			if (candidateMove != null)
				throw new PGNException("two possible moves match move: " + move + ". first " + candidateMove + ", second: " + possibleMove);

			candidateMove = possibleMove;
		}

		if (candidateMove == null)
			throw new PGNException("no possible moves match move: " + move + ".");

		// fill missing move data
		move.setStartCell(candidateMove.getStartCell());
		move.setEndCell(candidateMove.getEndCell());

		board.move(move);

		if (debug) {
			System.out.println("DEBUG: move " + board.getMoveNumber() + " " + (isBlack ? "Black" : "White") + " after move:" + move);
			String[] dump = board.dump(true);
			for (int i = 0; i < dump.length; i++) {
				System.out.println("BOARD: " + dump[i]);
			}
		}
	}

	private List<Game> internalRead(Reader stream) throws PGNException {
		List<Game> games = new ArrayList<Game>();
		Game game = null;
		Tokenizer tokenizer = new Tokenizer(stream);

		while (tokenizer.next() != null) {
			game = new Game();
			games.add(game);
			readTags(game, tokenizer);

			readMoves(game, tokenizer);
		}

		return games;
	}

	private void readMoves(Game game, Tokenizer tokenizer) throws PGNException {
		Token token = tokenizer.getCurrent();

		// comment before moves: add it to the game
		if (token.getType() == TokenType.COMMENT) {
			game.setComment(token.getText());
			token = tokenizer.next();
		}

		boolean isFirstMove = true;

		while (readOneMove(game, tokenizer, isFirstMove))
			;

	}

	private boolean readOneMove(Game game, Tokenizer tokenizer, boolean isFirstMove) throws PGNException {
		Token token = tokenizer.getCurrent();

		int moveNumber = game.getMoves().getMoveNumber();
		if (moveNumber <= 0)
			moveNumber = 1;

		// get (optional) first move number
		if (token.getType() == TokenType.NUMBER) {
			moveNumber = token.getNumber();
			token = tokenizer.next();
		}

		// get (optional) dot(s)
		int countDots = 0;
		while (token.equals(PGNReader.DOT)) {
			token = tokenizer.next();
			countDots++;
		}

		boolean isBlackMove;

		if (countDots == 1)
			isBlackMove = false;
		else if (countDots > 1)
			isBlackMove = true;
		else
			isBlackMove = game.getMoves().isBlackTurn();

		if (isFirstMove)
			isFirstMove = false;

		// get the move.
		if (token.equals(RESULT_UNDECIDED)) {
			// game still ongoing
			// game.setResult(...);
			if (debug)
				System.out.println("RESULT: undecided");
			return false;
		} else if (token.equals(RESULT_WHITE_WINS)) {
			// game won by white player
			// game.setResult(...);
			if (debug)
				System.out.println("RESULT: white wins");
			return false;
		} else if (token.equals(RESULT_BLACK_WINS)) {
			// game won by black player
			// game.setResult(...);
			if (debug)
				System.out.println("RESULT: black wins");
			return false;
		} else if (token.equals(RESULT_DRAW)) {
			// game draw
			// game.setResult(...);
			if (debug)
				System.out.println("RESULT: draw");
			return false;
		}

		if (token.getType() != TokenType.SYMBOL)
			throw new PGNException("parsing moves: expecting a SYMBOL (move), found " + token);

		// do the simple move parsing here
		Move move = readMove(token.getText(), isBlackMove);

		game.getMoves().addMove(move, moveNumber, isBlackMove);

		if (debug)
			System.out.println("MOVE: " + moveNumber + (isBlackMove ? " BLACK " : " WHITE ") + move.toString());

		token = tokenizer.next();
		if (token.getType() == TokenType.COMMENT) {
			move.setComment(token.getText());
			token = tokenizer.next();
		}

		// fixme
		return token != null;
	}

	private Token readTags(Game game, Tokenizer tokenizer) throws PGNException {
		Token token = tokenizer.getCurrent();

		while (token.equals(PGNReader.START_TAG)) {
			addTag(game, tokenizer);
			token = tokenizer.next();
		}
		return token;
	}

	private void addTag(Game game, Tokenizer tokenizer) throws PGNException {
		Token token = tokenizer.next();
		if (token.getType() != TokenType.SYMBOL)
			throw new PGNException("tag parsing: expected token of type SYMBOL after \"[\"");
		String name = token.getText();

		token = tokenizer.next();
		if (token.getType() != TokenType.STRING)
			throw new PGNException("tag parsing: expected token of type STRING after SYMBOL");
		String value = token.getText();

		token = tokenizer.next();
		if (!token.equals(PGNReader.END_TAG))
			throw new PGNException("tag parsing: expected token of \"]\" after STRING");

		if (debug)
			System.out.println("PARSER: TAG " + name + " = \"" + value + "\"");
		if (name.equals("Event"))
			game.setTagEvent(value);
		else if (name.equals("Site"))
			game.setTagSite(value);
		else if (name.equals("Date"))
			game.setTagDate(value);
		else if (name.equals("Round"))
			game.setTagRound(value);
		else if (name.equals("White"))
			game.setTagWhite(value);
		else if (name.equals("Black"))
			game.setTagBlack(value);
		else if (name.equals("Result"))
			game.setTagResult(value);
		else
			game.setOtherTag(name, value);
	}

	// private void parseTag(Game game, InputStream stream) {
	// // TODO Auto-generated method stub
	//
	// }

	// public List<Game> read(Scanner scanner) {
	// // read tags
	// while (scanner.hasNext())
	// System.out.println("TOK: " + scanner.next());
	// return null;
	// }
	//
	public Move readMove(String s, boolean isBlack) throws PGNException {
		// if contains x split, parse source and dest.
		// casi:
		// a1: mossa di pedone
		// Pa1: mossa di altro pezzo
		// Pab1: mossa con disambiguazione tramite fila
		// P1a2: mossa con disambiguazione tramite rank
		// axb1: cattura con pedone
		// Pxa1: cattura con pezzo.
		// Paxb1: cattura con disambiguazione tramite fila
		// P1xa2: cattura con disambiguazione tramite rank
		//
		// se la prima e' maiuscola, e' un pezzo: tolto quello rimane:
		// a1: mossa
		// ab1: mossa con disambiguazione tramite fila
		// 1a2: mossa con disambiguazione tramite rank
		// axb1: cattura con pedone o disambiguazione tramite fila
		// xa1: cattura con pezzo.
		// 1xa2: cattura con disambiguazione tramite rank

		// se tolgo la 'x' di cattura, rimane
		// a1: mossa o cattura target
		// ab1: mossa o cattura con disambiguazione tramite fila
		// 1a2: mossa o cattura con disambiguazione tramite rank

		// suffissi:
		// =P: promozione
		// +: scacco
		// #: scacco matto.

		String fullMove = "" + s;
		Move move = new Move();

		// get piece.
		char c = s.charAt(0);

		if (Character.isUpperCase(c)) {
			// handle castling
			if (c == 'O') {
				move.setPiece(PieceType.KING); // by convention
				if (s.equals("O-O"))
					move.setCastling(CastleType.SHORT, isBlack);
				else if (s.equals("O-O-O"))
					move.setCastling(CastleType.LONG, isBlack);
				else
					throw new PGNException("invalid castling: " + s);
				// done with this move
				return move;
			}

			move.setPiece(PieceType.fromChar(c));
			if (move.getPiece() == null)
				throw new PGNException("invalid piece in move: " + s);
			s = s.substring(1);
		} else
			move.setPiece(PieceType.PAWN);

		// get capture:
		int idxCapture = s.indexOf('x');

		if (idxCapture >= 0) {
			move.setCapture(true);
			s = s.substring(0, idxCapture) + s.substring(idxCapture + 1);
		}

		// get check/checkmate suffix
		c = s.charAt(s.length() - 1);
		if (c == '+') {
			move.setCheck(true);
			s = s.substring(0, s.length() - 1);
		} else if (c == '#') {
			move.setCheckMate(true);
			s = s.substring(0, s.length() - 1);
		}

		// get promotion suffix
		if (Character.isUpperCase(c)) {
			move.setPromotedTo(PieceType.fromChar(c));
			if (move.getPromotedTo() == null)
				throw new PGNException("invalid promoted piece: " + c + " in move " + fullMove);
			c = s.charAt(s.length() - 2);
			if (c != '=')
				throw new PGNException("invalid promoted piece without equal sign, piece: " + c + ", move " + fullMove);
			s = s.substring(0, s.length() - 2);
		}

		// FIXME: handle length() == 4 to cope with "dumb" moves: e.g. e2e4,
		// b1c3, ecc
		if (s.length() == 3) {
			char sourceFile = ' ';
			int sourceRank = 0;
			// first letter is a disambiguation position, can be a rank or a
			// file
			c = s.charAt(0);
			if (Character.isDigit(c))
				sourceRank = Integer.parseInt("" + c);
			else
				sourceFile = c;
			Cell startCell = new Cell(sourceFile, sourceRank);
			move.setStartCell(startCell);
			s = s.substring(1);
		}
		if (s.length() == 2) {
			char targetFile = s.charAt(0);
			int targetRank = Integer.parseInt("" + s.charAt(1));
			Cell endCell = new Cell(targetFile, targetRank);
			move.setEndCell(endCell);
		} else
			throw new PGNException("invalid move length, remaining data: " + s + ", full move: " + fullMove);

		return move;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
