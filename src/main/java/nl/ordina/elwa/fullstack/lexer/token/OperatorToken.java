package nl.ordina.elwa.fullstack.lexer.token;

import nl.ordina.elwa.fullstack.lexer.Operator;

public final class OperatorToken extends Token {

  private final Operator operator;

  OperatorToken(final String input, final int index) {
    super(Type.OPERATOR, index);
    this.operator = Operator.of(input, index);
  }

  public Operator getOperator() {
    return operator;
  }

  @Override
  public String toString() {
    return String.valueOf(operator.getValue());
  }

}
