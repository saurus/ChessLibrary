package org.saurus.chess.pgn;

import java.util.List;

public class Move {
	public enum CastleType {
		NONE, SHORT, LONG
	}
	private PieceType piece;
	private Cell startCell;
	private Cell endCell;
	private boolean isCapture;
	private boolean isCheck;
	private boolean isCheckMate;
	private PieceType promotedTo;
	private String comment;
	private List<MoveList> variations; 
	
	public PieceType getPiece() {
		return piece;
	}
	public void setPiece(PieceType piece) {
		this.piece = piece;
	}
	public Cell getStartCell() {
		return startCell;
	}
	public void setStartCell(Cell startCell) {
		this.startCell = startCell;
	}
	public Cell getEndCell() {
		return endCell;
	}
	public void setEndCell(Cell endCell) {
		this.endCell = endCell;
	}
	public boolean isCapture() {
		return isCapture;
	}
	public void setCapture(boolean isCapture) {
		this.isCapture = isCapture;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public boolean isCheckMate() {
		return isCheckMate;
	}
	public void setCheckMate(boolean isCheckMate) {
		this.isCheckMate = isCheckMate;
	}
	public PieceType getPromotedTo() {
		return promotedTo;
	}
	public void setPromotedTo(PieceType promotedTo) {
		this.promotedTo = promotedTo;
	}
	public CastleType getCastling() {
		if (piece == PieceType.KING && startCell != null && endCell != null && startCell.getFile() == 'e') {
			if (endCell.getFile() == 'g')
				return CastleType.SHORT;
			if (endCell.getFile() == 'c')
				return CastleType.LONG;
		}
		return CastleType.NONE;
	}
	public void setCastling(CastleType castling, boolean isBlack) {
		int rank = isBlack?8:1;
		this.setStartCell(new Cell('e', rank));
		
		switch (castling) {
		case SHORT:
			this.setEndCell(new Cell('g', rank));
			break;
		case LONG:
			this.setEndCell(new Cell('c', rank));
		default:
			break;
		}
			
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public List<MoveList> getVariations() {
		return variations;
	}
	public void setVariations(List<MoveList> variations) {
		this.variations = variations;
	}
	
	@Override
	public String toString() {
		CastleType castling = getCastling();
				
		if (castling == CastleType.SHORT)
			return "O-O";
		if (castling == CastleType.LONG)
			return "O-O-O";
		
		return (piece == PieceType.PAWN?"":piece.toString()) + (startCell!=null?startCell.toString():"") + (isCapture?"x":"") + endCell + (promotedTo!=null?"="+promotedTo.toString():"") + (isCheck?"+":"") + (isCheckMate?"#":"");
	}
}