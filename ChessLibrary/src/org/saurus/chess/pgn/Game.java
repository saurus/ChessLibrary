package org.saurus.chess.pgn;

import java.util.HashMap;
import java.util.Map;

public class Game {
	private String tagEvent;
	private String tagSite;
	private String tagDate;
	private String tagRound;
	private String tagWhite;
	private String tagBlack;
	private String tagResult;
	private Map<String, String> otherTags;
	private String comment;
	private MoveList moves;
	
	public Game() {
		otherTags = new HashMap<String, String>();
		moves = new MoveList();
	}
	
	public String getTagEvent() {
		return tagEvent;
	}

	public void setTagEvent(String tagEvent) {
		this.tagEvent = tagEvent;
	}

	public String getTagSite() {
		return tagSite;
	}

	public void setTagSite(String tagSite) {
		this.tagSite = tagSite;
	}

	public String getTagDate() {
		return tagDate;
	}

	public void setTagDate(String tagDate) {
		this.tagDate = tagDate;
	}

	public String getTagRound() {
		return tagRound;
	}

	public void setTagRound(String tagRound) {
		this.tagRound = tagRound;
	}

	public String getTagWhite() {
		return tagWhite;
	}

	public void setTagWhite(String tagWhite) {
		this.tagWhite = tagWhite;
	}

	public String getTagBlack() {
		return tagBlack;
	}

	public void setTagBlack(String tagBlack) {
		this.tagBlack = tagBlack;
	}

	public String getTagResult() {
		return tagResult;
	}

	public void setTagResult(String tagResult) {
		this.tagResult = tagResult;
	}

	public Map<String, String> getOtherTags() {
		return otherTags;
	}

	public void setOtherTags(Map<String, String> otherTags) {
		this.otherTags = otherTags;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public MoveList getMoves() {
		return moves;
	}

	public void setMoves(MoveList moves) {
		this.moves = moves;
	}

	public String getOtherTag(String name) {
		return otherTags.get(name);
	}
	
	public void setOtherTag(String name, String value) {
		otherTags.put(name, value);
	}

	public String getStartFEN() {
		// TODO get fen from game
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		
		return fen;
	}
}