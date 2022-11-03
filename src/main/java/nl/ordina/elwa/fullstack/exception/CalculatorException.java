package nl.ordina.elwa.fullstack.exception;

public final class CalculatorException extends Exception {

  public CalculatorException(final String message) {
    super(message);
  }

  public CalculatorException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
