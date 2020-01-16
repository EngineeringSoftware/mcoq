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

/**
 * This is a categorized string used by the S-expression parser. A text between
 * quotes is considered as a single token.
 * 
 * @author Julian Mendez
 */
class Token {

	public static final char COMMENT_CHAR = ';';
	public static final char ESCAPE_CHAR = '\\';
	public static final char LEFT_PARENTHESIS_CHAR = '(';
	public static final char QUOTATION_MARK_CHAR = '\"';
	public static final char RIGHT_PARENTHESIS_CHAR = ')';

	private int location = -1;
	private final StringBuffer sbuf;

	/**
	 * Constructs a new empty token.
	 */
	public Token() {
		this.sbuf = new StringBuffer();
	}

	/**
	 * Constructs a new token.
	 * 
	 * @param str
	 *            string value of the token
	 * @param loc
	 *            location of the token
	 */
	public Token(String str, int loc) {
		if (str == null) {
			throw new IllegalArgumentException("Cannot create a token using a null string.");
		}

		this.sbuf = new StringBuffer(str);
		this.location = loc;
	}

	/**
	 * Adds a character to this token.
	 * 
	 * @param ch
	 *            character
	 */
	public void append(char ch) {
		this.sbuf.append(ch);
	}

	/**
	 * Adds a string to this token
	 * 
	 * @param str
	 *            string
	 */
	public void append(String str) {
		if (str != null) {
			this.sbuf.append(str);
		}
	}

	@Override
	public boolean equals(Object o) {
		boolean ret = (this == o);
		if (!ret && o instanceof Token) {
			Token other = (Token) o;
			ret = getText().equals(other.getText()) && getLocation() == other.getLocation();
		}
		return ret;
	}

	/**
	 * Returns the location of this token.
	 * 
	 * @return the location of this token
	 */
	public int getLocation() {
		return this.location;
	}

	/**
	 * Returns the value of this token.
	 * 
	 * @return the value of this token
	 */
	public String getText() {
		return this.sbuf.toString();
	}

	@Override
	public int hashCode() {
		return this.getText().hashCode();
	}

	/**
	 * Tells whether this token is a comment.
	 * 
	 * @return <code>true</code> if and only if this token is a comment
	 */
	public boolean isComment() {
		return getText().startsWith("" + COMMENT_CHAR);
	}

	/**
	 * Tells whether this token is a left parenthesis.
	 * 
	 * @return <code>true</code> if and only if this token is a left parenthesis
	 */
	public boolean isLeftParenthesis() {
		return getText().equals("" + LEFT_PARENTHESIS_CHAR);
	}

	/**
	 * Tells whether this token is a quotation mark.
	 * 
	 * @return <code>true</code> if and only if this token is a quotation mark
	 */
	public boolean isQuotationMarkToken() {
		return getText().startsWith("" + QUOTATION_MARK_CHAR) && getText().endsWith("" + QUOTATION_MARK_CHAR);
	}

	/**
	 * Tells whether this token is a right parenthesis.
	 * 
	 * @return <code>true</code> if and only if this token is a right
	 *         parenthesis
	 */
	public boolean isRightParenthesis() {
		return getText().equals("" + RIGHT_PARENTHESIS_CHAR);
	}

	/**
	 * Returns the length of this token.
	 * 
	 * @return the length of this token
	 */
	public int length() {
		return this.sbuf.length();
	}

	/**
	 * Changes the location of this token.
	 * 
	 * @param loc
	 *            the location of this token
	 */
	public void setLocation(int loc) {
		this.location = loc;
	}

	/**
	 * Changes the text of this token.
	 * 
	 * @param str
	 *            the text of this token
	 */
	public void setText(String str) {
		if (str == null) {
			throw new IllegalArgumentException("Cannot create a token using a null string.");
		}
		this.sbuf.setLength(0);
		this.sbuf.append(str);
	}

	@Override
	public String toString() {
		return getText() + ":" + getLocation();
	}

}
