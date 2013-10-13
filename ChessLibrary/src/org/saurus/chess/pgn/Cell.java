package org.saurus.chess.pgn;

/**
 * this is a class describing a chess board's cell. 
 * this contains a 'file' (a-h in standard chess) and a 'rank' (1-8).
 *  
 * @author sauro
 *
 */
public class Cell {
	public static final char UNDEFINED_FILE = ' ';
	public static final int UNDEFINED_RANK = 0;
	
	private char file;
	private int rank;
	
	public Cell(char file, int rank) {
		this.file = file;
		this.rank = rank;
	}
	
	public Cell(int file, int rank) {
		this.file = (char) ('a' + (file - 1));
		this.rank = rank;
	}
	
	public char getFile() {
		return file;
	}
	
	public int getFileInt() {
		return file - 'a' + 1;
	}
	
	public int getRank() {
		return rank;
	}
	
	@Override
	public String toString() {
		return  "" + (file == UNDEFINED_FILE?"":"" + file) + (rank == UNDEFINED_RANK?"":""+rank);
	}

	public Cell add(int deltaFile, int deltaRank) {
		Cell target = new Cell(getFileInt() + deltaFile, getRank() + deltaRank);
		
		if (target.getFileInt() < 1 || target.getFileInt() > 8 || target.getRank() < 1 || target.getRank() > 8)
			return null;
		return target;
	}
}