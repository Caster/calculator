package nl.ordina.elwa.fullstack.lexer.token;

import java.text.DecimalFormat;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;

public final class NumberToken extends Token {

  private static final DecimalFormat FORMAT = new DecimalFormat("#.#####");

  public static String format(final double value) {
    return FORMAT.format(value);
  }

  private final double value;

  NumberToken(final String input, final int index) {
    super(Type.NUMBER, index);
    try {
      this.value = Double.parseDouble(input);
    } catch (final NumberFormatException nfe) {
      // The only possibility that a number we lexed is not a valid double is if it has
      // multiple points (which coincidentally is the message of the NumberFormatException).
      val firstPoint = input.indexOf('.');
      val secondPoint = input.indexOf('.', firstPoint + 1);
      throw new CalculatorException(
          "Invalid number: %s".formatted(nfe.getMessage()),
          index + secondPoint,
          nfe
      );
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
