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
 * Exception thrown when an error occurs while trying to parse an S-expression.
 * 
 * @author Julian Mendez
 */
public class SexpParserException extends Exception {

	private static final long serialVersionUID = -8566185429209182391L;

	public SexpParserException() {
		super();
	}

	public SexpParserException(String message) {
		super(message);
	}

	public SexpParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public SexpParserException(Throwable cause) {
		super(cause);
	}

}
