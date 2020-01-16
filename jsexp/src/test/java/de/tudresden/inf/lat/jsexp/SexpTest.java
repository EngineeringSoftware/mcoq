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
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Sexp}.
 * 
 * @author Julian Mendez
 *
 */
public class SexpTest {

	public SexpTest() {
	}

	@Test
	public void testDepth() throws SexpParserException, IOException {
		String testStr = " (( defun test () \"hi there\"))";
		Sexp parsedExpression = SexpFactory.parse(new StringReader(testStr));
		Sexp sexp1 = SexpFactory.newNonAtomicSexp();
		sexp1.add(SexpFactory.newAtomicSexp("defun"));
		sexp1.add(SexpFactory.newAtomicSexp("test"));
		sexp1.add(SexpFactory.newNonAtomicSexp());
		sexp1.add(SexpFactory.newAtomicSexp("\"hi there\""));
		Sexp expectedExpression = SexpFactory.newNonAtomicSexp();
		expectedExpression.add(sexp1);
		Assert.assertEquals(expectedExpression, parsedExpression);
	}

	@Test
	public void testEmptyString() throws SexpParserException, IOException {
		String testStr = "";
		try {
			SexpFactory.parse(new StringReader(testStr));
			Assert.assertTrue(false);
		} catch (SexpParserException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testGet() throws SexpParserException, IOException {
		String testStr = "((elem-01-01 elem-01-02) elem-02 (elem-03-01 elem-03-02 elem-03-03))";
		Sexp parsedExpr = SexpFactory.parse(new StringReader(testStr));
		Sexp expectedElem03 = SexpFactory.newNonAtomicSexp();
		expectedElem03.add(SexpFactory.newAtomicSexp("elem-03-01"));
		expectedElem03.add(SexpFactory.newAtomicSexp("elem-03-02"));
		expectedElem03.add(SexpFactory.newAtomicSexp("elem-03-03"));
		Assert.assertEquals(SexpFactory.newAtomicSexp("elem-01-01"), parsedExpr.get(0).get(0));
		Assert.assertEquals(SexpFactory.newAtomicSexp("elem-01-02"), parsedExpr.get(0).get(1));
		Assert.assertEquals(SexpFactory.newAtomicSexp("elem-02"), parsedExpr.get(1));
		Assert.assertEquals(expectedElem03, parsedExpr.get(2));
		try {
			parsedExpr.get(3);
			Assert.assertTrue(false);
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue(true);
		}
		try {
			(SexpFactory.newAtomicSexp("example")).get(0);
			Assert.assertTrue(false);
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testShuffle() throws SexpParserException, IOException {
		String testStr = "((elem-01-01 elem-01-02) (elem-02-01 elem-02-02 elem-02-03))";
		SexpList sexpList = (SexpList) SexpFactory.parse(new StringReader(testStr));
		SexpList.shuffle(new Random(42), sexpList);
        Assert.assertEquals("((elem-02-01 elem-02-02 elem-02-03) (elem-01-01 elem-01-02))", sexpList.toString());
	}

	@Test
	public void testIndentedString() {
		Sexp aux = SexpFactory.newNonAtomicSexp();
		aux.add(SexpFactory.newAtomicSexp("defun"));
		aux.add(SexpFactory.newAtomicSexp("mark-told-subsumers-and-ancestors"));
		Sexp aux1 = SexpFactory.newNonAtomicSexp();
		aux1.add(SexpFactory.newAtomicSexp("c"));
		aux1.add(SexpFactory.newAtomicSexp("subsumer"));
		aux.add(aux1);
		aux.add(SexpFactory
				.newAtomicSexp("\"Mark as subsumer of c all told subsumers and their ancestors in the hierarchy\""));
		Sexp aux2 = SexpFactory.newNonAtomicSexp();
		aux2.add(SexpFactory.newAtomicSexp("dolist"));
		Sexp aux2_1 = SexpFactory.newNonAtomicSexp();
		aux2_1.add(SexpFactory.newAtomicSexp("x"));
		Sexp aux2_1_1 = SexpFactory.newNonAtomicSexp();
		aux2_1_1.add(SexpFactory.newAtomicSexp("c-told-subsumers"));
		aux2_1_1.add(SexpFactory.newAtomicSexp("c"));
		aux2_1.add(aux2_1_1);
		aux2.add(aux2_1);
		Sexp aux2_2 = SexpFactory.newNonAtomicSexp();
		aux2_2.add(SexpFactory.newAtomicSexp("unless"));
		Sexp aux2_2_1 = SexpFactory.newNonAtomicSexp();
		aux2_2_1.add(SexpFactory.newAtomicSexp("eq"));
		Sexp aux2_2_1_1 = SexpFactory.newNonAtomicSexp();
		aux2_2_1_1.add(SexpFactory.newAtomicSexp("c-marked"));
		aux2_2_1_1.add(SexpFactory.newAtomicSexp("x"));
		aux2_2_1.add(aux2_2_1_1);
		aux2_2_1.add(SexpFactory.newAtomicSexp("subsumer"));
		aux2_2.add(aux2_2_1);
		Sexp aux2_2_2 = SexpFactory.newNonAtomicSexp();
		aux2_2_2.add(SexpFactory.newAtomicSexp("when"));
		Sexp aux2_2_2_1 = SexpFactory.newNonAtomicSexp();
		aux2_2_2_1.add(SexpFactory.newAtomicSexp("c-classified"));
		aux2_2_2_1.add(SexpFactory.newAtomicSexp("x"));
		aux2_2_2.add(aux2_2_2_1);
		Sexp aux2_2_2_2 = SexpFactory.newNonAtomicSexp();
		aux2_2_2_2.add(SexpFactory.newAtomicSexp("mark-ancestors"));
		aux2_2_2_2.add(SexpFactory.newAtomicSexp("x"));
		aux2_2_2_2.add(SexpFactory.newAtomicSexp("subsumer"));
		aux2_2_2.add(aux2_2_2_2);
		aux2_2.add(aux2_2_2);
		Sexp aux2_2_3 = SexpFactory.newNonAtomicSexp();
		aux2_2_3.add(SexpFactory.newAtomicSexp("mark-told-subsumers-and-ancestors"));
		aux2_2_3.add(SexpFactory.newAtomicSexp("x"));
		aux2_2_3.add(SexpFactory.newAtomicSexp("subsumer"));
		aux2_2.add(aux2_2_3);
		aux2.add(aux2_2);
		aux.add(aux2);
		String str = "(defun mark-told-subsumers-and-ancestors "
				+ "\n  (c subsumer) \"Mark as subsumer of c all told subsumers and their ancestors in the hierarchy\" "
				+ "\n  (dolist " + "\n    (x " + "\n      (c-told-subsumers c)) " + "\n    (unless " + "\n      (eq "
				+ "\n        (c-marked x) subsumer) " + "\n      (when " + "\n        (c-classified x) "
				+ "\n        (mark-ancestors x subsumer)) "
				+ "\n      (mark-told-subsumers-and-ancestors x subsumer))))";
		Assert.assertEquals(str, aux.toIndentedString());
	}

	@Test
	public void testMissingParenthesis() throws SexpParserException, IOException {
		String testStr = "(( defun test () \"hi there\")( a s ";
		try {
			SexpFactory.parse(new StringReader(testStr));
			Assert.assertTrue(false);
		} catch (SexpParserException e) {
			Assert.assertTrue(true);
		}
		testStr = "(( defun test () \"hi there\")";
		try {
			SexpFactory.parse(testStr);
			Assert.assertTrue(false);
		} catch (SexpParserException e) {
			Assert.assertTrue(true);
		}
		testStr = ") defun test () \"hi there\")";
		try {
			SexpFactory.parse(new StringReader(testStr));
			Assert.assertTrue(false);
		} catch (SexpParserException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testParsing() throws SexpParserException, IOException {
		String testStr = "( defun test () \"hi there\")";
		Sexp parsedExpression = SexpFactory.parse(testStr);
		Sexp expectedExpression = SexpFactory.newNonAtomicSexp();
		expectedExpression.add(SexpFactory.newAtomicSexp("defun"));
		expectedExpression.add(SexpFactory.newAtomicSexp("test"));
		expectedExpression.add(SexpFactory.newNonAtomicSexp());
		expectedExpression.add(SexpFactory.newAtomicSexp("\"hi there\""));
		Assert.assertEquals(expectedExpression, parsedExpression);
	}

	@Test
	public void testPlainString() throws SexpParserException, IOException {
		String testStr = "test";
		Sexp expectedExpr = SexpFactory.newAtomicSexp("test");
		Sexp parsedExpr = SexpFactory.parse(new StringReader(testStr));
		Assert.assertEquals(expectedExpr, parsedExpr);
	}

	@Test
	public void testWeirdSexpFromTLCWithVerticalBar() throws SexpParserException, IOException {
		String longStr= "((v(VernacExpr()(VernacExtend(VernacTacticNotation 0)((GenArg raw(OptArg(ExtraArg ltac_tactic_level))())(GenArg raw(ListArg(ExtraArg ltac_production_item))((TacTerm set_eq)(TacNonTerm((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2030)(bol_pos_last 74314)(bp 74339)(ep 74347)))((ident())((Id X)))))(TacNonTerm((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2030)(bol_pos_last 74314)(bp 74348)(ep 74357)))((ident())((Id HX)))))(TacTerm :)(TacNonTerm((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2030)(bol_pos_last 74314)(bp 74362)(ep 74371)))((constr())((Id E)))))(TacTerm in)(TacTerm |-)))(GenArg raw(ExtraArg tactic)(TacThen(TacAtom((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74387)(ep 74405)))(TacLetTac false(Name(Id X))((v(CRef((v(Ser_Qualid(DirPath())(Id E)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74397)(ep 74398)))))()))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74397)(ep 74398)))))((onhyps(()))(concl_occs NoOccurrences))true())))(TacArg((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74407)(ep 74423)))(TacCall((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74407)(ep 74423)))(((v(Ser_Qualid(DirPath())(Id def_to_eq)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74407)(ep 74416)))))((Reference((v(Ser_Qualid(DirPath())(Id X)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74417)(ep 74418))))))(Reference((v(Ser_Qualid(DirPath())(Id HX)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74419)(ep 74421))))))(Reference((v(Ser_Qualid(DirPath())(Id E)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74422)(ep 74423))))))))))))))))))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2031)(bol_pos_last 74385)(bp 74314)(ep 74424)))))";
		Sexp sexp = SexpFactory.parse(longStr);
		Assert.assertEquals(2, sexp.getLength());
	}

	@Test
	public void testClone() throws SexpParserException {
		String longStr = "((v(VernacExpr()(VernacExtend(VernacTacticNotation 0)((GenArg raw(OptArg(ExtraArg ltac_tactic_level))())(GenArg raw(ListArg(ExtraArg ltac_production_item))((TacTerm set_eq)(TacNonTerm((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2030)(bol_pos_last 74314)(bp 74339)(ep 74347)))((ident())((Id X)))))(TacNonTerm((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2030)(bol_pos_last 74314)(bp 74348)(ep 74357)))((ident())((Id HX)))))(TacTerm :)(TacNonTerm((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2030)(bol_pos_last 74314)(bp 74362)(ep 74371)))((constr())((Id E)))))(TacTerm in)(TacTerm |-)))(GenArg raw(ExtraArg tactic)(TacThen(TacAtom((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74387)(ep 74405)))(TacLetTac false(Name(Id X))((v(CRef((v(Ser_Qualid(DirPath())(Id E)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74397)(ep 74398)))))()))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74397)(ep 74398)))))((onhyps(()))(concl_occs NoOccurrences))true())))(TacArg((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74407)(ep 74423)))(TacCall((((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74407)(ep 74423)))(((v(Ser_Qualid(DirPath())(Id def_to_eq)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74407)(ep 74416)))))((Reference((v(Ser_Qualid(DirPath())(Id X)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74417)(ep 74418))))))(Reference((v(Ser_Qualid(DirPath())(Id HX)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74419)(ep 74421))))))(Reference((v(Ser_Qualid(DirPath())(Id E)))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2031)(bol_pos 74385)(line_nb_last 2031)(bol_pos_last 74385)(bp 74422)(ep 74423))))))))))))))))))(loc(((fname(InFile /home/celik/projects/coq-mutation/mutator/_downloads/tlc/src/LibTactics.v))(line_nb 2030)(bol_pos 74314)(line_nb_last 2031)(bol_pos_last 74385)(bp 74314)(ep 74424)))))";
		Sexp sexp = SexpFactory.parse(longStr);
		Sexp cloneSexp = SexpFactory.clone(sexp);
		Assert.assertFalse(sexp == cloneSexp);
		Assert.assertEquals(sexp.toString(), cloneSexp.toString());
	}
}
