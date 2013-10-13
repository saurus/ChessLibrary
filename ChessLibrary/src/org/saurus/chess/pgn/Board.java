package org.saurus.chess.pgn;

import java.util.ArrayList;
import java.util.List;

import org.saurus.chess.pgn.Move.CastleType;

public class Board {
	Piece[][] pieces;
	int plyCount;

	public Board() {
		pieces = new Piece[8][8];
	}

	public void set(Cell cell, Piece piece) {
		int fileInt = cell.getFileInt();
		int rank = cell.getRank();

		pieces[fileInt - 1][rank - 1] = piece;
	}

	public Piece get(Cell cell) {
		int fileInt = cell.getFileInt();
		int rank = cell.getRank();

		return pieces[fileInt - 1][rank - 1];
	}

	public int getMoveNumber() {
		return (plyCount - 1) / 2 + 1;
	}

	public void move(Move move) {
		move(move.getStartCell(), move.getEndCell());

		CastleType castling = move.getCastling();

		if (castling != CastleType.NONE) {
			int rookRank = move.getEndCell().getRank();
			if (castling == CastleType.SHORT)
				move(new Cell('h', rookRank), new Cell('f', rookRank));
			else if (castling == CastleType.LONG)
				move(new Cell('a', rookRank), new Cell('d', rookRank));
		}
		plyCount++;
	}

	private void move(Cell startCell, Cell endCell) {
		Piece piece = get(startCell);

		set(endCell, piece);
		set(startCell, null);
	}

	public List<Cell> getCells(Piece piece) {
		List<Cell> cells = new ArrayList<Cell>();

		for (int file = 0; file < 8; file++)
			for (int rank = 0; rank < 8; rank++) {
				Piece pieceOnBoard = pieces[file][rank];

				if (pieceOnBoard != null && pieceOnBoard.equals(piece))
					cells.add(new Cell(file + 1, rank + 1));
			}

		return cells;
	}

	public String[] dump(boolean addLabels) {
		int size = addLabels?10:8;
		String[] b = new String[size];

		for (int rank = 0; rank < 8; rank++) {
			if (addLabels)
				b[7 - rank] = (rank + 1) + ": ";
			else
				b[7 - rank] = "";
			for (int file = 0; file < 8; file++) {
				Piece piece = pieces[file][rank];
				String p = ".";

				if (piece != null) {
					p = piece.getPieceType().toString();
					if (piece.isBlack())
						p = p.toLowerCase();
				}
				b[7 - rank] += p + " ";
			}
		}
		if (addLabels) {
			b[8] = "   - - - - - - - -";
			b[9] = "   a b c d e f g h";
		}

		return b;
	}

}
