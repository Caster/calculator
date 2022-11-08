package nl.ordina.elwa.fullstack.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token;
import nl.ordina.elwa.fullstack.lexer.Token.BracketToken;
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
    while (!tokensLeft.isEmpty() && tokensLeft.peek().getType() != Type.BRACKET) {
      tree = parseExpression(tree);
    }
    return tree;
  }

  private AbstractSyntaxTree parseValue() {
    val token = nextToken();
    if (token.getType() == Type.NUMBER) {
      return AbstractSyntaxTree.leaf(token);
    }
    if (token.getType() == Type.BRACKET && ((BracketToken) token).isOpen()) {
      val expression = parse().bracketed();
      val closeToken = nextToken();
      if (closeToken.getType() != Type.BRACKET || ((BracketToken) closeToken).isOpen()) {
        throw new CalculatorException(
            "Expected a closing bracket, got %s%s".formatted(
                (closeToken.getType() == Type.BRACKET ? "opening " : ""),
                closeToken.getType().toString().toLowerCase()
            ),
            closeToken.getIndex()
        );
      }
      return expression;
    }
    throw new CalculatorException("Expected a number or opening bracket", token.getIndex());
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
    if (leftHandSide.hasLowerPriorityThan((OperatorToken) token) && !leftHandSide.isBracketed()) {
      return leftHandSide.withReplacedRightChild(token, parseValue());
    }
    return AbstractSyntaxTree.node(leftHandSide, token, parseValue(), false);
  }

}
