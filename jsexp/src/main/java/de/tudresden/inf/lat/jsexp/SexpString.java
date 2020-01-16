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

import java.util.Iterator;

/**
 * Represents an atomic S-expression.
 * 
 * @author Julian Mendez
 */
public class SexpString implements Sexp {

	private final String rep;

	/**
	 * Constructs a new S-expression using the given string.
	 * 
	 * @param text
	 *            string for the S-expression
	 */
	protected SexpString(String text) {
		if (text == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.rep = text;
	}

	@Override
	public void add(Sexp item) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public boolean equals(Object o) {
		boolean ret = (this == o);
		if (!ret && o instanceof SexpString) {
			SexpString other = (SexpString) o;
			ret = getText().equals(other.getText());
		}
		return ret;
	}

	@Override
	public Sexp get(int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void set(int index, Sexp sexp) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int getLength() {
		return 0;
	}

	private String getText() {
		return this.rep;
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

	@Override
	public boolean isAtomic() {
		return true;
	}

	@Override
	public Iterator<Sexp> iterator() {
		return null;
	}

	@Override
	public String toIndentedString() {
		return toString();
	}

	@Override
	public String toString() {
		return getText();
	}

	@Override
	public String objectHash() { return Long.toString(super.hashCode(), 16); }
}
