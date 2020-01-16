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
 * Represents an S-expression. There are basically two kinds of S-expressions:
 * the atomic and the non-atomic. S-expressions are used in Lisp.
 * 
 * @author Julian Mendez
 */
public interface Sexp extends Iterable<Sexp> {

	/**
	 * Adds an item to a non atomic S-expression. If applied to an atomic one,
	 * it throws an exception.
	 * 
	 * @param item
	 *            element to be added
	 * @throws IndexOutOfBoundsException
	 *             if the Sexp object is atomic (a SexpString)
	 */
	public void add(Sexp item);

	/**
	 * Gets an element given by index.
	 * 
	 * @param index
	 *            index
	 * @throws IndexOutOfBoundsException
	 *             if the Sexp object is atomic (a SexpString) or it is a
	 *             SexpList but the index is out of its bounds.
	 * @return the index-th element for a non-atomic S-expression
	 */
	public Sexp get(int index);

    /**
     * Sets the given element by index
     * @param index
     *             index
     * @param sexp
     *             the Sexp object
     * @throws IndexOutOfBoundsException
     *             if the Sexp object is atomic (a SexpString) or it is a
     *             SexpList but the index is out of its bounds.
     */
	public void set(int index, Sexp sexp);

	/**
	 * Gives the depth of an S-expression. The depth is defined as 0 for atomic
	 * expressions, and one more than the maximum of the depth of the members
	 * for non-atomic expression.
	 * 
	 * @return 0 for an atomic S-expression or 1+max(depth(elem)) for a
	 *         non-atomic S-expression, where elem are its members.
	 */
	// public int getDepth();

	/**
	 * Returns the length of a Sexp object.
	 * 
	 * @return 0 if it is an atomic S-expression or the length of a non-atomic
	 *         S-expression.
	 */
	public int getLength();

	/**
	 * Says whether an S-expression is atomic or not.
	 * 
	 * @return true if this S-expression if atomic or false otherwise.
	 */
	public boolean isAtomic();

	/**
	 * Returns an iterator.
	 * 
	 * @return null if this S-expression is atomic or an iterator to the list of
	 *         members if it is non-atomic.
	 */
	public Iterator<Sexp> iterator();

	/**
	 * Presents an S-expression as a Lisp-like indented code.
	 * 
	 * @return a string with the indented S-expression.
	 */
	public String toIndentedString();

	/**
	 *  Object pointer
	 *
	 * @return Object pointer
	 */
	public String objectHash();

}
