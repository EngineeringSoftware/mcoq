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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SexpTokenizerTest {

	public SexpTokenizerTest() {
	}

	@Test
	public void testBasic() throws SexpParserException, IOException {
		String testStr = "( defun test () \"hi there\")";
		StringReader input = new StringReader(testStr);
		List<Token> expectedList = new ArrayList<>();
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token("defun", 1));
		expectedList.add(new Token("test", 1));
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token(")", 1));
		expectedList.add(new Token("\"hi there\"", 1));
		expectedList.add(new Token(")", 1));
		List<Token> parsedList = SexpTokenizer.tokenize(input);
		Assert.assertEquals(expectedList, parsedList);
	}

	@Test
	public void testBlanks() throws SexpParserException, IOException {
		String testStr = "  \t   (  defun     test ( \t ) \n \"hi there\"   )   \t ";
		StringReader input = new StringReader(testStr);
		List<Token> expectedList = new ArrayList<>();
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token("defun", 1));
		expectedList.add(new Token("test", 1));
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token(")", 1));
		expectedList.add(new Token("\"hi there\"", 2));
		expectedList.add(new Token(")", 2));
		List<Token> parsedList = SexpTokenizer.tokenize(input);
		Assert.assertEquals(expectedList, parsedList);
	}

	@Test
	public void testComments() throws SexpParserException, IOException {
		String testStr = "( defun \n test;no comments\n () \"hi ;no comment here \n there\") ;this is a comment";
		StringReader input = new StringReader(testStr);
		List<Token> expectedList = new ArrayList<>();
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token("defun", 1));
		expectedList.add(new Token("test", 2));
		expectedList.add(new Token(";no comments", 2));
		expectedList.add(new Token("(", 3));
		expectedList.add(new Token(")", 3));
		expectedList.add(new Token("\"hi ;no comment here \n there\"", 4));
		expectedList.add(new Token(")", 4));
		List<Token> parsedList = SexpTokenizer.tokenize(input);
		Assert.assertEquals(expectedList, parsedList);
	}

	@Test
	public void testLineNumbers() throws SexpParserException, IOException {
		String testStr = "( \ndefun \ntest \n(\n) \"hi there\")";
		StringReader input = new StringReader(testStr);
		List<Token> expectedList = new ArrayList<>();
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token("defun", 2));
		expectedList.add(new Token("test", 3));
		expectedList.add(new Token("(", 4));
		expectedList.add(new Token(")", 5));
		expectedList.add(new Token("\"hi there\"", 5));
		expectedList.add(new Token(")", 5));
		List<Token> parsedList = SexpTokenizer.tokenize(input);
		Assert.assertEquals(expectedList, parsedList);
	}

	@Test
	public void testNewLineInText() throws SexpParserException, IOException {
		String testStr = "( defun test () \"hi \n\nthere\")";
		StringReader input = new StringReader(testStr);
		List<Token> expectedList = new ArrayList<>();
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token("defun", 1));
		expectedList.add(new Token("test", 1));
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token(")", 1));
		expectedList.add(new Token("\"hi \n\nthere\"", 3));
		expectedList.add(new Token(")", 3));
		List<Token> parsedList = SexpTokenizer.tokenize(input);
		Assert.assertEquals(expectedList, parsedList);
	}

	@Test
	public void testNoSpaces() throws SexpParserException, IOException {
		String testStr = "\t(defun\ttest(\t)\"hi-there\"\n)\t";
		StringReader input = new StringReader(testStr);
		List<Token> expectedList = new ArrayList<>();
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token("defun", 1));
		expectedList.add(new Token("test", 1));
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token(")", 1));
		expectedList.add(new Token("\"hi-there\"", 1));
		expectedList.add(new Token(")", 2));
		List<Token> parsedList = SexpTokenizer.tokenize(input);
		Assert.assertEquals(expectedList, parsedList);
	}

	@Test
	public void testQuotationMarksInQuotedText() throws SexpParserException, IOException {
		String testStr = "( defun\ttest () \"hi \\\" there\"\n)";
		StringReader input = new StringReader(testStr);
		List<Token> expectedList = new ArrayList<>();
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token("defun", 1));
		expectedList.add(new Token("test", 1));
		expectedList.add(new Token("(", 1));
		expectedList.add(new Token(")", 1));
		expectedList.add(new Token("\"hi \\\" there\"", 1));
		expectedList.add(new Token(")", 2));
		List<Token> parsedList = SexpTokenizer.tokenize(input);
		Assert.assertEquals(expectedList, parsedList);
	}

	@Test
	public void testUnbalancedQuotes() throws SexpParserException, IOException {
		String testStr = "( defun test () \"hi there)";
		StringReader input = new StringReader(testStr);
		try {
			SexpTokenizer.tokenize(input);
			Assert.assertTrue(false);
		} catch (SexpParserException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testWrongParentheses() throws SexpParserException, IOException {
		String testStr = ") defun\ttest () \"hi \\\" there\"\n(";
		StringReader input = new StringReader(testStr);
		try {
			SexpTokenizer.tokenize(input);
			Assert.assertTrue(false);
		} catch (SexpParserException e) {
			Assert.assertTrue(true);
		}
	}

}
