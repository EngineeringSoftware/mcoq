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

import java.util.*;

/**
 * Represents a non-atomic S-expression.
 * 
 * @author Julian Mendez
 */
public class SexpList implements Sexp {

	private static final String NEW_LINE = "\n";
	private static final String TABULATION = "  ";
	private List<Sexp> rep = new ArrayList<>();

	/**
	 * Creates an empty non-atomic S-expression.
	 */
	protected SexpList() {
	}

	/**
	 * Creates a non-atomic S-expression using a list of tokens.
	 * 
	 * @param tokenList
	 *            list of tokens to parse, this list must be not null and have
	 *            balanced parenthesis.
	 */
	protected SexpList(List<Token> tokenList) {
		if (tokenList == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		SexpList ret = null;
		Stack<SexpList> stack = new Stack<SexpList>();
		SexpList currentList = new SexpList();
		boolean firstTime = true;
		for (Token tok : tokenList) {
			if (tok.isLeftParenthesis()) {
				if (!firstTime) {
					stack.push(currentList);
					currentList = new SexpList();
				}
				firstTime = false;
			} else if (tok.isRightParenthesis()) {
				if (stack.empty()) {
					ret = currentList;
				} else {
					SexpList lastList = currentList;
					currentList = stack.pop();
					currentList.add(lastList);
				}
			} else if (!tok.isComment()) {
				currentList.add(new SexpString(tok.getText()));
			}
		}
		this.rep = ret.rep;
	}

	@Override
	public void add(Sexp item) {
		if (item == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.rep.add(item);
	}

	@Override
	public boolean equals(Object o) {
		boolean ret = (this == o);
		if (!ret && o instanceof SexpList) {
			SexpList other = (SexpList) o;
			ret = this.rep.equals(other.rep);
		}
		return ret;
	}

	@Override
	public Sexp get(int index) {
		return this.rep.get(index);
	}

	@Override
	public void set(int index, Sexp sexp) {
		this.rep.set(index, sexp);
	}

	@Override
	public int getLength() {
		return this.rep.size();
	}

	@Override
	public int hashCode() {
		return this.rep.hashCode();
	}

	@Override
	public boolean isAtomic() {
		return false;
	}

	@Override
	public Iterator<Sexp> iterator() {
		return this.rep.iterator();
	}

	@Override
	public String toIndentedString() {
		return toIndentedString(this, 0).trim();
	}

	/**
	 * Auxiliary function for showing an indented string representing an
	 * S-expression or subexpression at a certain depth. The depth determines
	 * the indentation.
	 * 
	 * @param expr
	 *            the S-expression or subexpression.
	 * @param depth
	 *            the depth where a S-subexpression is.
	 * @return an indented string of the passed S-(sub)expression at a certain
	 *         depth.
	 */
	private String toIndentedString(Sexp expr, int depth) {
		StringBuffer ret0 = new StringBuffer();
		if (expr.isAtomic()) {
			ret0.append(expr.toIndentedString());
		} else if (expr.getLength() == 0) {
			ret0.append(Token.LEFT_PARENTHESIS_CHAR);
			ret0.append(Token.RIGHT_PARENTHESIS_CHAR);
		} else {
			ret0.append(NEW_LINE);
			for (int i = 0; i < depth; i++) {
				ret0.append(TABULATION);
			}
			ret0.append(Token.LEFT_PARENTHESIS_CHAR);
			for (Iterator<Sexp> it = expr.iterator(); it.hasNext();) {
				Sexp current = it.next();
				ret0.append(toIndentedString(current, depth + 1));
				if (it.hasNext()) {
					ret0.append(' ');
				}
			}
			ret0.append(Token.RIGHT_PARENTHESIS_CHAR);
		}
		return ret0.toString();
	}

	@Override
	public String toString() {
		StringBuffer ret0 = new StringBuffer();
		ret0.append(Token.LEFT_PARENTHESIS_CHAR);
		for (Iterator<Sexp> it = this.rep.iterator(); it.hasNext();) {
			Sexp expr = it.next();
			ret0.append(expr.toString());
			if (it.hasNext()) {
				ret0.append(' ');
			}
		}
		ret0.append(Token.RIGHT_PARENTHESIS_CHAR);
		return ret0.toString();
	}

    public static void shuffle(Random random, SexpList sexpList) {
        final int size = sexpList.getLength();
        for (int i = 0; i < size - 1; ++i) {
            int j = i + 1 + random.nextInt(size - i - 1);
            // swap i and j
            Sexp aux = sexpList.get(j);
            sexpList.set(j, sexpList.get(i));
            sexpList.set(i, aux);
        }
    }

    public static void reverse(SexpList sexpList) {
		int i = 0, j = sexpList.getLength() - 1;
		while (i < j) {
			// swap i and j
			Sexp aux = sexpList.get(j);
			sexpList.set(j, sexpList.get(i));
			sexpList.set(i, aux);
			++i;
			--j;
		}
	}

    @Override
    public String objectHash() { return Long.toString(super.hashCode(), 16); }
}
