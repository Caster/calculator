package nl.ordina.elwa.fullstack.lexer;

import static lombok.AccessLevel.PROTECTED;

import java.text.DecimalFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;

@RequiredArgsConstructor(access = PROTECTED)
public sealed class Token {

  public enum Type {
    NUMBER,
    OPERATOR,
    BRACKET
  }

  @Getter
  private final Type type;
  @Getter
  private final int index;

  /**
   * Returns a token for the given type and input.
   *
   * @see NumberToken
   * @see OperatorToken
   */
  public static Token of(@NonNull final Type type, @NonNull final String input, final int index) {
    return switch (type) {
      case NUMBER -> new NumberToken(input, index);
      case OPERATOR -> new OperatorToken(input, index);
      case BRACKET -> new BracketToken(input, index);
    };
  }

  public static final class NumberToken extends Token {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.#####");

    public static String format(final double value) {
      return FORMAT.format(value);
    }

    private final double value;

    private NumberToken(final String input, final int index) {
      super(Type.NUMBER, index);
      try {
        this.value = Double.parseDouble(input);
      } catch (final NumberFormatException nfe) {
        if ("multiple points".equals(nfe.getMessage())) {
          val firstPoint = input.indexOf('.');
          val secondPoint = input.indexOf('.', firstPoint + 1);
          throw new CalculatorException(nfe.getMessage(), index + secondPoint, nfe);
        }
        throw new CalculatorException(nfe.getMessage(), index, nfe);
      }
    }

    public double getValue() {
      return value;
    }

    @Override
    public String toString() {
      return format(value);
    }

  }

  public static final class OperatorToken extends Token {

    private final Operator operator;

    private OperatorToken(final String input, final int index) {
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

  public static final class BracketToken extends Token {

    private final boolean isOpen;

    private BracketToken(final String input, final int index) {
      super(Type.BRACKET, index);
      this.isOpen = "(".equals(input);
    }

    public boolean isOpen() {
      return isOpen;
    }

    @Override
    public String toString() {
      return (isOpen ? "(" : ")");
    }
  }

}
