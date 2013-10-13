package org.saurus.tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

public class Tokenizer {
	private Reader reader;
	private boolean isEOF;
	private char lookaheadChar = Character.MIN_VALUE;
	private boolean debug;
	private Token currentToken;

	public Tokenizer(Reader reader) {
		this(reader, false);
	}

	public Tokenizer(Reader reader, boolean debug) {
		this.reader = reader;
		this.debug = debug;
	}

	public Token getCurrent() {
		return this.currentToken;
	}

	public Token next() {
		currentToken = internalNext();
		if (debug)
			System.out.println(currentToken == null ? "(null)" : currentToken.toString());

		return currentToken;
	}

	private Token internalNext() {
		char c;

		while ((c = readStream()) > Character.MIN_VALUE) {
			switch (c) {
			case '"':
				return readToken(Token.TokenType.STRING, '\\', "\"");
			case ';':
				return readToken(Token.TokenType.COMMENT, Character.MIN_VALUE, "\r\n");
			case '{':
				return readToken(Token.TokenType.COMMENT, Character.MIN_VALUE, "}");
			default:
				if (isTokenSelfDelimiting(c))
					return new Token(Token.TokenType.TOKEN, "" + c);
				else if (Character.isLetter(c))
					return readSymbol(c);
				else if (Character.isDigit(c))
					return readNumber(c);
				else if (Character.isWhitespace(c))
					; // ignore
				else
					return new Token(Token.TokenType.ERROR, "INVALID char: " + c);
			}
		}

		return null;
	}

	private Token readSymbol(char c) {
		String s = readIdentifier(c);
		
		if (s != null)
			return new Token(Token.TokenType.SYMBOL, s);
		else
			return new Token(Token.TokenType.ERROR, "EOF found on symbol");
	}

	private Token readNumber(char c) {
		String s = readIdentifier(c);
		
		if (s != null) {
			if (isNumber(s))
				return new Token(Token.TokenType.NUMBER, s);
			else 
				return new Token(Token.TokenType.SYMBOL, s);
		} else
			return new Token(Token.TokenType.ERROR, "EOF found on symbol");
	}

	private String readIdentifier(char c) {

		StringBuilder sb = new StringBuilder();

		sb.append(c);
		while ((c = readStream()) > 0) {
			if (Character.isLetter(c) || Character.isDigit(c) || "_+#=:-/".indexOf(c) >= 0)
				sb.append(c);
			else {
				lookaheadChar = c;
				return sb.toString();
			}
		}

		return null;
	}

	private Token readToken(Token.TokenType type, char quote, String endMarkers) {
		char c;
		StringBuilder sb = new StringBuilder();

		while ((c = readStream()) > 0) {
			if (c == quote)
				// get quoted char
				c = readStream();
			else if (endMarkers.indexOf(c) >= 0) {
				return new Token(type, sb.toString());
			}
			sb.append(c);
		}

		return new Token(Token.TokenType.ERROR, "EOF found on token: " + sb.toString());
	}

	private boolean isTokenSelfDelimiting(char c) {
		// taken from http://www.thechessdrum.net/PGN_Reference.txt, section 7.
		return "[].*()<>".indexOf(c) >= 0;
	}

	private char readStream() {
		if (isEOF)
			return Character.MIN_VALUE;

		if (lookaheadChar != Character.MIN_VALUE) {
			char c = lookaheadChar;
			lookaheadChar = Character.MIN_VALUE;
			return c;
		}

		int ic;

		try {
			ic = reader.read();
		} catch (IOException e) {
			ic = -1;
		}
		if (ic == -1) {
			isEOF = true;
			return Character.MIN_VALUE;
		}

		return (char) ic;
	}
	
	private static final Pattern intPattern = Pattern.compile("-?[0-9]+");
	
	public boolean isNumber(String s) {
		return intPattern.matcher(s).matches();
	}
}
