package org.saurus.chess.pgn;

public enum PieceType {
	KING,
	QUEEN,
	ROOK,
	BISHOP,
	KNIGHT,
	PAWN;

	@Override
	public String toString() {
		switch (this) {
		case KING: return "K";
		case QUEEN: return "Q";
		case ROOK: return "R";
		case BISHOP: return "B";
		case KNIGHT: return "N";
		case PAWN: return "P";
		default:
			return "!";
		}
	}
	
	public static PieceType fromChar(char c) {
		switch (c) {
		case 'K': return KING;
		case 'Q': return QUEEN;
		case 'R': return ROOK;
		case 'B': return BISHOP;
		case 'N': return KNIGHT;
		case 'P': return PAWN;
		default:
			return null;
		}
	}
}