package nl.ordina.elwa.fullstack.lexer.token;

import lombok.Getter;
import nl.ordina.elwa.fullstack.lexer.operator.BinaryOperator;
import nl.ordina.elwa.fullstack.lexer.operator.Operator;
import nl.ordina.elwa.fullstack.lexer.operator.UnaryOperator;
import nl.ordina.elwa.fullstack.parser.AbstractSyntaxTree;

public final class OperatorToken extends Token {

  @Getter
  private final Operator operator;

  OperatorToken(final String input, final int index) {
    super(Type.OPERATOR, index);
    this.operator = Operator.of(input, index);
  }

  public double applyOperator(final AbstractSyntaxTree a) {
    return ((UnaryOperator) operator).applyAsDouble(a);
  }

  public double applyOperator(final AbstractSyntaxTree a, final AbstractSyntaxTree b) {
    return ((BinaryOperator) operator).applyAsDouble(a, b);
  }

  public boolean isBinary() {
    return operator instanceof BinaryOperator;
  }

  @Override
  public String toString() {
    return String.valueOf(operator.getValue());
  }

}
