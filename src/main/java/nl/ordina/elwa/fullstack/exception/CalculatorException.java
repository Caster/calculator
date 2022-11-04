package nl.ordina.elwa.fullstack.exception;

import lombok.Getter;

public final class CalculatorException extends RuntimeException {

  @Getter
  private final int problemIndex;

  public CalculatorException(final String message) {
    this(message, -1, null);
  }

  public CalculatorException(final String message, final Throwable cause) {
    this(message, -1, cause);
  }

  public CalculatorException(final String message, final int problemIndex) {
    this(message, problemIndex, null);
  }

  public CalculatorException(final String message, final int problemIndex, final Throwable cause) {
    super(message, cause);
    this.problemIndex = problemIndex;
  }

}
