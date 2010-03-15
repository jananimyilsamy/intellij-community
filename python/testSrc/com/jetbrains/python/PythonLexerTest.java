package com.jetbrains.python;

import com.intellij.lexer.Lexer;
import com.jetbrains.python.lexer.PythonIndentingLexer;
import junit.framework.TestCase;

/**
 * @author yole
 */
public class PythonLexerTest extends TestCase {
  public void testSimpleExpression() {
    doTest("a=1", "Py:IDENTIFIER", "Py:EQ", "Py:INTEGER_LITERAL");
  }

  public void testMergeSpaces() {
    doTest("a  =    1", "Py:IDENTIFIER", "Py:SPACE", "Py:EQ", "Py:SPACE", "Py:INTEGER_LITERAL");
  }

  public void testLineBreakInBraces() {
    doTest("[a,\n b]", "Py:LBRACKET", "Py:IDENTIFIER", "Py:COMMA", "Py:LINE_BREAK", "Py:IDENTIFIER", "Py:RBRACKET");
  }

  public void testLineBreakInBraces2() {
    doTest("x=[a,\n b]", "Py:IDENTIFIER", "Py:EQ", "Py:LBRACKET", "Py:IDENTIFIER", "Py:COMMA", "Py:LINE_BREAK",
           "Py:IDENTIFIER", "Py:RBRACKET");
  }

  public void testLineBreakInBracesAfterComment() {
    doTest("x=[a, #c\n b]",
           "Py:IDENTIFIER", "Py:EQ", "Py:LBRACKET", "Py:IDENTIFIER", "Py:COMMA", "Py:SPACE", "Py:END_OF_LINE_COMMENT",
           "Py:LINE_BREAK", "Py:IDENTIFIER", "Py:RBRACKET");
  }

  public void testBraceAfterIndent() {
    doTest("x=\n [a,\n  b]",
           "Py:IDENTIFIER", "Py:EQ", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:INDENT", "Py:LBRACKET", "Py:IDENTIFIER", "Py:COMMA", "Py:LINE_BREAK", "Py:IDENTIFIER", "Py:RBRACKET");
  }

  public void testBackslash() {
    doTest("a=\\\nb",
           "Py:IDENTIFIER", "Py:EQ", "Py:SPACE", "Py:LINE_BREAK", "Py:IDENTIFIER");
  }

  public void testBackslashAfterSpace() {
    doTest("a = \\\n  b",
           "Py:IDENTIFIER", "Py:SPACE", "Py:EQ", "Py:SPACE", "Py:LINE_BREAK", "Py:IDENTIFIER");
  }

  public void testMultipleBackslashes() {
    doTest("[\\\n\\\n]",
           "Py:LBRACKET", "Py:SPACE", "Py:LINE_BREAK", "Py:SPACE", "Py:LINE_BREAK", "Py:RBRACKET");
  }

  public void testIndent() {
    doTest("if a:\n b",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:INDENT", "Py:IDENTIFIER");
  }

  public void testMultiLineIndent() {
    doTest("if a:\n b\n c",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:INDENT", "Py:IDENTIFIER", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:IDENTIFIER");
  }

  public void testDedent() {
    doTest("if a:\n b\nc",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:INDENT", "Py:IDENTIFIER", "Py:STATEMENT_BREAK", "Py:DEDENT", "Py:LINE_BREAK",
           "Py:IDENTIFIER");
  }

  public void testMultiDedent() {
    doTest("if a:\n b\n  c\nd",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:INDENT",
           "Py:IDENTIFIER", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:INDENT", "Py:IDENTIFIER", "Py:STATEMENT_BREAK", "Py:DEDENT",
           "Py:DEDENT", "Py:LINE_BREAK", "Py:IDENTIFIER");
  }

  public void testEmptyLine() {
    doTest("if a:\n b\n  \n c",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:INDENT",
           "Py:IDENTIFIER", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:IDENTIFIER");
  }

  public void testEndOfLineSpace() {
    doTest("if a:\n b\n  c   \n  \n  d",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:INDENT", "Py:IDENTIFIER", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:INDENT", "Py:IDENTIFIER", "Py:STATEMENT_BREAK",
           "Py:LINE_BREAK", "Py:IDENTIFIER");
  }

  public void testComment() {
    doTest("if a:\n b\n #comment\nc",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:INDENT", "Py:IDENTIFIER", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:END_OF_LINE_COMMENT", "Py:DEDENT", "Py:LINE_BREAK",
           "Py:IDENTIFIER");
  }

  public void testIndentedComment() {
    doTest("#a\n #b\n#c",
           "Py:END_OF_LINE_COMMENT", "Py:LINE_BREAK", "Py:END_OF_LINE_COMMENT", "Py:LINE_BREAK", "Py:END_OF_LINE_COMMENT");
  }

  public void testIndentedCommentAndCode() {
    doTest("if a:\n #b\n c",
           "Py:IF_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:COLON", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:END_OF_LINE_COMMENT", "Py:LINE_BREAK", "Py:INDENT", "Py:IDENTIFIER");
  }

  public void testWithNotKeyword() {
    doTest("with=as", "Py:IDENTIFIER", "Py:EQ", "Py:IDENTIFIER");
  }

  public void testBytesLiteral() {
    doTest("b'ABC'", "Py:STRING_LITERAL");
  }

  public void testOctalLiteral() {
    doTest("0o123", "Py:INTEGER_LITERAL");
  }

  public void testBinaryLiteral() {
    doTest("0b0101", "Py:INTEGER_LITERAL");
  }

  private static final String THREE_QUOTES = "\"\"\"";

  public void testLongString() {
    doTest(THREE_QUOTES + THREE_QUOTES + "\nb=" + THREE_QUOTES + THREE_QUOTES,
           "Py:STRING_LITERAL", "Py:STATEMENT_BREAK", "Py:LINE_BREAK", "Py:IDENTIFIER", "Py:EQ", "Py:STRING_LITERAL");
  }

  public void testLongStringEscaped() {
    doTest("''' \\'''foo\\''' ''';", "Py:STRING_LITERAL", "Py:SEMICOLON");
  }

  public void testLongStringWithLineBreak() {
    doTest(THREE_QUOTES + "\\\na\n\n" + THREE_QUOTES + ";", "Py:STRING_LITERAL", "Py:SEMICOLON");
  }


  public void _testWithKeyword() throws Exception {
    // processing of 'from __future__ import' is now done on parser level, so a pure lexer test won't handle
    // this correctly
    doTest("from __future__ import with_statement\nwith x as y", new String[] { "Py:FROM_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:SPACE", "Py:IMPORT_KEYWORD", "Py:SPACE", "Py:IDENTIFIER",
           "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:WITH_KEYWORD", "Py:SPACE", "Py:IDENTIFIER", "Py:SPACE", "Py:AS_KEYWORD", "Py:SPACE", "Py:IDENTIFIER" });
  }

  public void testBackslashBeforeEmptyLine() {
    doTest("x=\"a\" + \\\n     \"b\" \\\n\nprint x", "Py:IDENTIFIER", "Py:EQ", "Py:STRING_LITERAL", "Py:SPACE", "Py:PLUS", "Py:SPACE", "Py:LINE_BREAK",
           "Py:STRING_LITERAL", "Py:SPACE", "Py:STATEMENT_BREAK", "Py:LINE_BREAK",
           "Py:IDENTIFIER" , "Py:SPACE", "Py:IDENTIFIER");
  }

  private static void doTest(String text, String... expectedTokens) {
    Lexer lexer = new PythonIndentingLexer();
    lexer.start(text);
    int idx = 0;
    int tokenPos = 0;
    while (lexer.getTokenType() != null) {
      if (idx >= expectedTokens.length) {
        StringBuilder remainingTokens = new StringBuilder(lexer.getTokenType().toString());
        lexer.advance();
        while (lexer.getTokenType() != null) {
          remainingTokens.append(" ").append(lexer.getTokenType().toString());
          lexer.advance();
        }
        fail("Too many tokens. Following tokens: " + remainingTokens.toString());
      }
      assertEquals("Token offset mismatch at position " + idx, tokenPos, lexer.getTokenStart());
      String tokenName = lexer.getTokenType().toString();
      assertEquals("Token mismatch at position " + idx, expectedTokens[idx], tokenName);
      idx++;
      tokenPos = lexer.getTokenEnd();
      lexer.advance();
    }

    if (idx < expectedTokens.length) fail("Not enough tokens");
  }
}
