package org.saurus.tokenizer;

public class Token implements Comparable<Token> {
	public enum TokenType {
		ERROR,
		TOKEN,
		STRING,
		COMMENT,
		SYMBOL,
		NUMBER,
	}
	
	private TokenType type;
	private String text;
	
	public Token(TokenType type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public TokenType getType() {
		return type;
	}
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return type.toString() + ":<" + text + ">"; 
	}

	@Override
	public int compareTo(Token o) {
		if (o == null)
			return 1;
		
		int delta = this.type.compareTo(o.type);
		if (delta != 0)
			return delta;
		
		if (this.text == null) {
			if (o.text == null)
				return 0;
			return -1;
		} 
			
		if (o.text == null) {
			return 1;
		} 
			
		return this.text.compareTo(o.text);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		return this.compareTo(other) == 0;
	}

	public int getNumber() {
		return Integer.parseInt(this.text);
	}
}
