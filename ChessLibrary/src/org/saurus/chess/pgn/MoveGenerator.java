package org.saurus.chess.pgn;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
	private Board board;
	private boolean isBlack;

	public MoveGenerator(Board board, boolean isBlack) {
		this.board = board;
		this.isBlack = isBlack;
	}

	public List<Move> AllMoves(Piece piece) {
		List<Move> moves = new ArrayList<Move>();
		List<Cell> cellsForPiece = board.getCells(piece);

		for (Cell startCell : cellsForPiece) {
			for (Cell endCell : Moves(startCell, piece)) {
				Move move = new Move();
				
				move.setPiece(piece.getPieceType());
				move.setStartCell(startCell);
				move.setEndCell(endCell);
				// FIXME other data...
				moves.add(move);
			}
		}

		return moves;
	}

	public List<Cell> Moves(Cell startCell, Piece piece) {
		switch (piece.getPieceType()) {
		case KING:
			return KingMoves(startCell);
		case QUEEN:
			return QueenMoves(startCell);
		case ROOK:
			return RookMoves(startCell);
		case BISHOP:
			return BishopMoves(startCell);
		case KNIGHT:
			return KnightMoves(startCell);
		case PAWN:
			return PawnMoves(startCell);
		default:
			return null;
		}
	}

	public List<Cell> RookMoves(Cell startCell) {
		List<Cell> cells = new ArrayList<Cell>();

		cells.addAll(SimpleMoves(startCell, 1, 0));
		cells.addAll(SimpleMoves(startCell, -1, 0));
		cells.addAll(SimpleMoves(startCell, 0, 1));
		cells.addAll(SimpleMoves(startCell, 0, -1));

		return cells;
	}

	public List<Cell> BishopMoves(Cell startCell) {
		List<Cell> cells = new ArrayList<Cell>();

		cells.addAll(SimpleMoves(startCell, 1, 1));
		cells.addAll(SimpleMoves(startCell, -1, -1));
		cells.addAll(SimpleMoves(startCell, -1, 1));
		cells.addAll(SimpleMoves(startCell, 1, -1));

		return cells;
	}

	public List<Cell> QueenMoves(Cell startCell) {
		List<Cell> cells = new ArrayList<Cell>();

		cells.addAll(RookMoves(startCell));
		cells.addAll(BishopMoves(startCell));

		return cells;
	}

	public List<Cell> KingMoves(Cell startCell) {
		List<Cell> cells = new ArrayList<Cell>();

		addMove(cells, startCell.add(1, 1));
		addMove(cells, startCell.add(1, 0));
		addMove(cells, startCell.add(1, -1));
		addMove(cells, startCell.add(0, -1));
		addMove(cells, startCell.add(-1, -1));
		addMove(cells, startCell.add(-1, 0));
		addMove(cells, startCell.add(-1, 1));
		addMove(cells, startCell.add(0, 1));
		
		// castling
		if (startCell.getFile() == 'e' && startCell.getRank() == (isBlack?8:1)) {
			// FIXME: check verification missing
			Cell targetCell = startCell.add(2,  0);
			if (board.get(startCell.add(1,  0)) == null && board.get(targetCell) == null) {
				Piece shouldBeRook = board.get(startCell.add(3,  0));
				if (shouldBeRook != null && shouldBeRook.getPieceType() == PieceType.ROOK)
					cells.add(targetCell);
			}
			targetCell = startCell.add(-2,  0);
			if (board.get(startCell.add(-1,  0)) == null && board.get(targetCell) == null && board.get(startCell.add(-3,  0)) == null ) {
				Piece shouldBeRook = board.get(startCell.add(-4,  0));
				if (shouldBeRook != null && shouldBeRook.getPieceType() == PieceType.ROOK)
					cells.add(targetCell);
			}
		}

		return cells;
	}

	public List<Cell> PawnMoves(Cell startCell) {
		List<Cell> cells = new ArrayList<Cell>();
		int direction = 1;
		int baseRank = 2;

		if (isBlack) {
			direction = -1;
			baseRank = 7;
		}
		Cell targetCell = startCell.add(0, direction);
		if (targetCell != null && board.get(targetCell) == null) {
			cells.add(targetCell);
			if (startCell.getRank() == baseRank) {
				// try an advance by two.
				targetCell = targetCell.add(0, direction);
				if (targetCell != null && board.get(targetCell) == null)
					cells.add(targetCell);
			}
		}
		// captures:
		targetCell = startCell.add(-1, direction);
		if (targetCell != null) {
			Piece piece = board.get(targetCell);
			if (piece != null && piece.isBlack() != isBlack)
				cells.add(targetCell);
		}

		targetCell = startCell.add(1, direction);
		if (targetCell != null) {
			Piece piece = board.get(targetCell);
			if (piece != null && piece.isBlack() != isBlack)
				cells.add(targetCell);
		}

		return cells;
	}

	public List<Cell> KnightMoves(Cell startCell) {
		List<Cell> cells = new ArrayList<Cell>();

		addMove(cells, startCell.add(-1, 2));
		addMove(cells, startCell.add(1, 2));
		addMove(cells, startCell.add(-2, 1));
		addMove(cells, startCell.add(2, 1));
		addMove(cells, startCell.add(-2, -1));
		addMove(cells, startCell.add(2, -1));
		addMove(cells, startCell.add(-1, -2));
		addMove(cells, startCell.add(1, -2));

		return cells;
	}

	private List<Cell> SimpleMoves(Cell startCell, int deltaFile, int deltaRank) {
		List<Cell> cells = new ArrayList<Cell>();

		for (Cell targetCell = startCell.add(deltaFile, deltaRank); targetCell != null; targetCell = targetCell.add(deltaFile, deltaRank)) {
			if (!addMove(cells, targetCell))
				break; // cannot go on
		}

		return cells;
	}

	private boolean addMove(List<Cell> cells, Cell targetCell) {
		if (targetCell != null) {
			Piece piece = board.get(targetCell);

			if (piece == null) {
				cells.add(targetCell);
				return true;
			}
			if (piece.isBlack() != isBlack) {
				cells.add(targetCell);
				return false; // cannot go on
			}
		}
		return false;
	}

}
