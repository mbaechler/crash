/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.cmdline.matcher.impl2;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TokenizerTestCase extends TestCase {

  public void testEmpty() throws Exception {
    Tokenizer tokenizer = new Tokenizer("");
    assertFalse(tokenizer.hasNext());
  }

  public void testSpace() throws Exception {
    Tokenizer tokenizer = new Tokenizer(" ");
    assertEquals(new Token.Whitespace(0, " "), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }

  public void testWord1() throws Exception {
    Tokenizer tokenizer = new Tokenizer("a");
    assertEquals(new Token.Literal(0, TokenType.WORD, "a"), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }

  public void testWord2() throws Exception {
    Tokenizer tokenizer = new Tokenizer(" a");
    assertEquals(new Token.Whitespace(0, " "), tokenizer.next());
    assertEquals(new Token.Literal(1, TokenType.WORD, "a"), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }

  public void testQuotedWord1() throws Exception {
    Tokenizer tokenizer = new Tokenizer("\"a");
    assertEquals(new Token.Literal(0, TokenType.WORD, "\"a", "a", Termination.DOUBLE_QUOTE), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }

  public void testQuotedWord2() throws Exception {
    Tokenizer tokenizer = new Tokenizer("\"a \"");
    assertEquals(new Token.Literal(0, TokenType.WORD, "\"a \"", "a ", Termination.DETERMINED), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }

  public void testQuotedWord3() throws Exception {
    Tokenizer tokenizer = new Tokenizer("a\" \"b");
    assertEquals(new Token.Literal(0, TokenType.WORD, "a\" \"b", "a b", Termination.DETERMINED), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }

  public void testShortOption1() throws Exception {
    Tokenizer tokenizer = new Tokenizer("-a");
    assertEquals(new Token.Literal(0, TokenType.SHORT_OPTION, "-a", "-a", Termination.DETERMINED), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }
  public void testShortOption2() throws Exception {
    Tokenizer tokenizer = new Tokenizer(" -a");
    assertEquals(new Token.Whitespace(0, " "), tokenizer.next());
    assertEquals(new Token.Literal(1, TokenType.SHORT_OPTION, "-a", "-a", Termination.DETERMINED), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }

  public void testLongOption1() throws Exception {
    Tokenizer tokenizer = new Tokenizer("--a");
    assertEquals(new Token.Literal(0, TokenType.LONG_OPTION, "--a", "--a", Termination.DETERMINED), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }
  public void testLongOption2() throws Exception {
    Tokenizer tokenizer = new Tokenizer(" --a");
    assertEquals(new Token.Whitespace(0, " "), tokenizer.next());
    assertEquals(new Token.Literal(1, TokenType.LONG_OPTION, "--a", "--a", Termination.DETERMINED), tokenizer.next());
    assertFalse(tokenizer.hasNext());
  }
}