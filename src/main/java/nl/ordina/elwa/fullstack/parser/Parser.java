package nl.ordina.elwa.fullstack.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token;

public final class Parser {

  private final Deque<Token> tokensLeft;

  public Parser(final List<Token> tokens) {
    tokensLeft = new ArrayDeque<>(tokens);
  }

  public AbstractSyntaxTree parse() throws CalculatorException {
    var tree = parseValue();
    while (!tokensLeft.isEmpty()) {
      tree = parseExpression(tree);
    }
    return tree;
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

  private AbstractSyntaxTree parseExpression(final AbstractSyntaxTree leftHandSide)
      throws CalculatorException {
    return AbstractSyntaxTree.node(leftHandSide, nextToken(), parseValue());
  }

}
