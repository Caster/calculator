package nl.ordina.elwa.fullstack.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token;
import nl.ordina.elwa.fullstack.lexer.Token.OperatorToken;
import nl.ordina.elwa.fullstack.lexer.Token.Type;

public final class Parser {

  private final Deque<Token> tokensLeft;

  public Parser(final List<Token> tokens) {
    tokensLeft = new ArrayDeque<>(tokens);
  }

  /**
   * Parse the list of tokens given at construction time into an {@link AbstractSyntaxTree}.
   */
  public AbstractSyntaxTree parse() {
    var tree = parseValue();
    while (!tokensLeft.isEmpty()) {
      tree = parseExpression(tree);
    }
    return tree;
  }

  private AbstractSyntaxTree parseValue() {
    return AbstractSyntaxTree.leaf(nextToken());
  }

  private Token nextToken() {
    try {
      return tokensLeft.pop();
    } catch (final NoSuchElementException e) {
      throw new CalculatorException("Unexpected end of input", e);
    }
  }

  private AbstractSyntaxTree parseExpression(final AbstractSyntaxTree leftHandSide) {
    val token = nextToken();
    if (token.getType() != Type.OPERATOR) {
      throw new CalculatorException(
          "Expected an operator, got [%s]".formatted(token), token.getIndex()
      );
    }
    if (leftHandSide.hasLowerPriorityThan((OperatorToken) token)) {
      return leftHandSide.withReplacedRightChild(token, parseValue());
    }
    return AbstractSyntaxTree.node(leftHandSide, token, parseValue());
  }

}
