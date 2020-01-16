/*
 * Copyright (C) 2009, 2012, 2015 Julian Mendez
 *
 *
 * This file is part of jsexp.
 *
 * jsexp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jsexp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jsexp.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.tudresden.inf.lat.jsexp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * An object of this class can create a list of tokens by processing a stream.
 * Blank spaces are omitted and texts between quotation marks are considered as
 * one token. It is implemented as a finite state machine.
 * 
 * @author Julian Mendez
 */
class SexpTokenizer {

	private enum State {
		COMMENT, QMARK, QMARK_BSLASH, SYMBOL
	}

	public static final char COMMENT_CHAR = Token.COMMENT_CHAR;
	public static final char ESCAPE_CHAR = Token.ESCAPE_CHAR;
	public static final char LEFT_PARENTHESIS_CHAR = Token.LEFT_PARENTHESIS_CHAR;
	public static final char NEW_LINE_CHAR = '\n';
	public static final char QUOTATION_MARK_CHAR = Token.QUOTATION_MARK_CHAR;
	public static final char RETURN_CHAR = '\r';
	public static final char RIGHT_PARENTHESIS_CHAR = Token.RIGHT_PARENTHESIS_CHAR;
	public static final char SPACE_CHAR = ' ';
	public static final char TAB_CHAR = '\t';

	/**
	 * Returns a list of tokens after processing an input stream.
	 * 
	 * @param in
	 *            input
	 * @return a list of tokens after processing an input stream
	 * @throws SexpParserException
	 *             if the input stream does not provide a valid S-expression
	 * @throws IOException
	 *             if the input stream cannot be properly read
	 */
	public static List<Token> tokenize(InputStream in) throws SexpParserException, IOException {
		return SexpTokenizer.tokenize(in);
	}

	/**
	 * Returns a list of tokens after processing a stream.
	 * 
	 * @param in
	 *            input
	 * @return a list of tokens after processing a stream
	 * @throws SexpParserException
	 *             if the stream does not provide a valid S-expression
	 * @throws IOException
	 *             if the stream cannot be properly read
	 */
	public static List<Token> tokenize(Reader in) throws SexpParserException, IOException {
		if (in == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		SexpTokenizer tokenizer = new SexpTokenizer();
		boolean finished = false;
		int ch = in.read();
		while (!finished && ch != -1) {
			finished = tokenizer.readChar((char) ch);
			if (!finished) {
				ch = in.read();
			}
		}
		tokenizer.close();
		return tokenizer.getParsedTokens();
	}

	private Token currentToken = new Token();
	private int depth = 0;
	private int lineNumber = 1;
	private State state = State.SYMBOL;
	private List<Token> tokenList = new ArrayList<>();

	/**
	 * Constructs a new S-expression tokenizer.
	 */
	private SexpTokenizer() {
	}

	private void close() throws SexpParserException {
		flush();
		if (this.state.equals(State.QMARK) || this.state.equals(State.QMARK_BSLASH)) {
			throw new SexpParserException("Missing quotation mark at line " + this.lineNumber);
		}
		if (this.depth != 0) {
			throw new SexpParserException("Unbalanced parenthesis at line " + this.lineNumber + ".");
		}
	}

	private void flush() {
		if (this.currentToken.length() > 0) {
			this.currentToken.setLocation(lineNumber);
			this.tokenList.add(this.currentToken);
			this.currentToken = new Token();
		}
	}

	private List<Token> getParsedTokens() {
		return this.tokenList;
	}

	private boolean readChar(char ch) {
		boolean ret = false;
		switch (this.state) {
		case SYMBOL:
			readSymbol(ch);
			break;
		case QMARK:
			readQuotationMark(ch);
			break;
		case QMARK_BSLASH:
			readQuotationMarkBackslash(ch);
			break;
		case COMMENT:
			readComment(ch);
			break;
		}
		ret = (this.depth <= 0 && this.tokenList.size() > 0);
		return ret;
	}

	private void readComment(char ch) {
		if (ch == NEW_LINE_CHAR) {
			flush();
			this.lineNumber++;
			this.state = State.SYMBOL;
		} else {
			this.currentToken.append(ch);
		}
	}

	private void readQuotationMark(char ch) {
		this.currentToken.append(ch);
		if (ch == NEW_LINE_CHAR) {
			this.lineNumber++;
		} else if (ch == ESCAPE_CHAR) {
			this.state = State.QMARK_BSLASH;
		} else if (ch == QUOTATION_MARK_CHAR) {
			flush();
			this.state = State.SYMBOL;
		}
	}

	private void readQuotationMarkBackslash(char ch) {
		this.currentToken.append(ch);
		if (ch == NEW_LINE_CHAR) {
			this.lineNumber++;
		}
		this.state = State.QMARK;
	}

	private void readSymbol(char ch) {
		if (ch == NEW_LINE_CHAR) {
			flush();
			this.lineNumber++;
		} else if (ch == SPACE_CHAR || ch == TAB_CHAR || ch == RETURN_CHAR) {
			flush();
		} else if (ch == COMMENT_CHAR) {
			flush();
			this.currentToken.append(ch);
			this.state = State.COMMENT;
		} else if (ch == LEFT_PARENTHESIS_CHAR) {
			this.depth++;
			flush();
			this.currentToken.append(ch);
			flush();
		} else if (ch == RIGHT_PARENTHESIS_CHAR) {
			this.depth--;
			flush();
			this.currentToken.append(ch);
			flush();
		} else if (ch == QUOTATION_MARK_CHAR) {
			flush();
			this.currentToken.append(ch);
			this.state = State.QMARK;
		} else {
			this.currentToken.append(ch);
		}
	}
}
