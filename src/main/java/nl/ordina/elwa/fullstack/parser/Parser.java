package nl.ordina.elwa.fullstack.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.token.BracketToken;
import nl.ordina.elwa.fullstack.lexer.token.OperatorToken;
import nl.ordina.elwa.fullstack.lexer.token.Token;
import nl.ordina.elwa.fullstack.lexer.token.Token.Type;

public final class Parser {

  private final Deque<Token> tokensLeft;

  public Parser(final List<Token> tokens) {
    tokensLeft = new ArrayDeque<>(tokens);
  }

  /**
   * Parse the list of tokens given at construction time into an {@link AbstractSyntaxTree}.
   */
  @SuppressWarnings("ConstantConditions")
  public AbstractSyntaxTree parse() {
    var tree = parseValue();
    while (!tokensLeft.isEmpty()) {
      if (tokensLeft.peek().getType() != Type.BRACKET) {
        tree = parseExpression(tree);
      } else if (((BracketToken) tokensLeft.peek()).isOpen()) {
        tree = buildNode(
            tree,
            (OperatorToken) Token.of(Type.OPERATOR, "*", tokensLeft.peek().getIndex()),
            parseValue()
        );
      } else {
        break;
      }
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
      nextToken(); // Remove closing bracket; needs no check, otherwise parse() would've failed.
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
      if (leftHandSide.isBracketed() || (
            leftHandSide.getToken().getType() == Type.OPERATOR
            && leftHandSide.getRightChild().isBracketed()
          )) {
        tokensLeft.push(token);
        return buildNode(
            leftHandSide,
            (OperatorToken) Token.of(Type.OPERATOR, "*", token.getIndex()),
            parseValue()
        );
      }

      throw new CalculatorException(
          "Expected an operator, got [%s]".formatted(token), token.getIndex()
      );
    }
    return buildNode(leftHandSide, (OperatorToken) token, parseValue());
  }

  private AbstractSyntaxTree buildNode(
      final AbstractSyntaxTree leftHandSide,
      final OperatorToken operatorToken,
      final AbstractSyntaxTree rightHandSide
  ) {
    if (leftHandSide.hasLowerPriorityThan(operatorToken) && !leftHandSide.isBracketed()) {
      return leftHandSide.withReplacedRightChild(operatorToken, rightHandSide);
    }
    return AbstractSyntaxTree.node(leftHandSide, operatorToken, rightHandSide, false);
  }

}
