package org.saurus.chess.pgn;

public class Piece {
	PieceType pieceType;
	boolean isBlack;

	public Piece(PieceType pieceType, boolean isBlack) {
		this.pieceType = pieceType;
		this.isBlack = isBlack;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public boolean isBlack() {
		return isBlack;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Piece other = (Piece) obj;
		if (this.pieceType != other.pieceType)
			return false;

		if (this.isBlack != other.isBlack)
			return false;

		return true;
	}
}
