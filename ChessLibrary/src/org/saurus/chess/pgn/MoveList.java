package org.saurus.chess.pgn;

import java.util.ArrayList;
import java.util.List;

public class MoveList extends ArrayList<MovePair> implements List<MovePair> {
	private static final long serialVersionUID = 8331491796262170777L;

	private int moveNumberStart;
	private boolean isBlackTurn;

	public void addMove(Move move, int moveNumber, boolean isBlackMove) throws PGNException {
		MovePair mp = null;
		
		if (this.size() == 0) {
			// this is the first move in this MoveList
			this.moveNumberStart = moveNumber;
			this.isBlackTurn = isBlackMove;
			mp = new MovePair();
		} else { 
			// add a move: check parameters validity
			if (this.getMoveNumber() != moveNumber)
				throw new PGNException("parsing moves: expecting a NUMBER " + this.getMoveNumber() + " for move number, got " + moveNumber);
			if (this.isBlackTurn != isBlackMove)
				throw new PGNException("parsing moves: expecting a " + (isBlackTurn ? "black" : "white") + " move, found a "
						+ (isBlackMove ? "black" : "white") + " move.");
			
			if (!isBlackTurn)
				mp = new MovePair();
		}
		if (mp != null)
			this.add(mp);
		else
			mp = this.get(this.size()-1);
		
		if (isBlackTurn)
			mp.setBlack(move);
		else
			mp.setWhite(move);
			
		isBlackTurn = !isBlackTurn;
	}
	
	public int getMoveNumber() {
		return moveNumberStart + this.size() - (isBlackTurn?1:0);
	}

	public int getMoveNumberStart() {
		return moveNumberStart;
	}

	public void setMoveNumberStart(int moveNumberStart) {
		this.moveNumberStart = moveNumberStart;
	}

	public boolean isBlackTurn() {
		return isBlackTurn;
	}

	public void setBlackTurn(boolean isBlackTurn) {
		this.isBlackTurn = isBlackTurn;
	}
}