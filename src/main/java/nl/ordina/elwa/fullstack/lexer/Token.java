package nl.ordina.elwa.fullstack.lexer;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PROTECTED)
public sealed class Token {

  public enum Type {
    NUMBER,
    OPERATOR
  }

  @Getter
  private final Type type;

  /**
   * Returns a token for the given type and input.
   *
   * @see NumberToken
   * @see OperatorToken
   */
  public static Token of(@NonNull final Type type, @NonNull final String input) {
    return switch (type) {
      case NUMBER -> new NumberToken(input);
      case OPERATOR -> new OperatorToken(input);
    };
  }

  public static final class NumberToken extends Token {

    private final double value;

    private NumberToken(final String input) {
      super(Type.NUMBER);
      this.value = Double.parseDouble(input);
    }

    public double getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
  }

  public static final class OperatorToken extends Token {

    private final Operator operator;

    private OperatorToken(final String input) {
      super(Type.OPERATOR);
      this.operator = Operator.of(input);
    }

    public Operator getOperator() {
      return operator;
    }

    @Override
    public String toString() {
      return String.valueOf(operator.getValue());
    }

  }

}
