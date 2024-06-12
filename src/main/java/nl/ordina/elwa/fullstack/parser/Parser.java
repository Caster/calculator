package nl.ordina.elwa.fullstack.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token;

public final class Parser {

  private final Deque<Token> tokensLeft;

  private AbstractSyntaxTree rootNode;

  public static AbstractSyntaxTree parse(final List<Token> tokens) {
    return new Parser(tokens).rootNode;
  }

  private Parser(final List<Token> tokens) {
    tokensLeft = new ArrayDeque<>(tokens);
    rootNode = parseValue();
    while (!tokensLeft.isEmpty()) {
      rootNode = parseExpression();
    }
  }

  private AbstractSyntaxTree parseValue() throws CalculatorException {
    return AbstractSyntaxTree.leaf(nextToken());
  }

  private Token nextToken() throws CalculatorException {
    try {
      return tokensLeft.pop();
    } catch (final NoSuchElementException e) {
      throw new CalculatorException("Unexpected end of input", e);
    }
  }

  private AbstractSyntaxTree parseExpression()
      throws CalculatorException {
    return AbstractSyntaxTree.node(rootNode, nextToken(), parseValue());
  }

}
