package nl.ordina.elwa.fullstack;

import static java.lang.Double.parseDouble;
import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;

@Slf4j
public final class Calculator {

  private static final DecimalFormat FORMAT = new DecimalFormat("#.#####");

  private static String format(final double number) {
    return FORMAT.format(number);
  }

  private final BufferedReader input;
  private final BufferedWriter output;

  /**
   * Construct a calculator that will read its input from the given {@link Reader} and will write
   * prompts and solutions to the given {@link Writer}.
   */
  public Calculator(final Reader input, final Writer output) {
    this.input = new BufferedReader(input);
    this.output = new BufferedWriter(output);
  }

  /**
   * Write a prompt and read two numbers, add them together and print the result.
   */
  public void compute() {
    write("> ");
    val problem = read();
    log.info("Solving [%s]...".formatted(problem));

    val hasError = new AtomicBoolean();
    val solution = Arrays.stream(problem.split("\\s*\\+\\s*"))
        .map(this::readDouble)
        .filter(optionalDouble -> {
          if (optionalDouble.isPresent()) {
            return true;
          }
          hasError.set(true);
          return false;
        })
        .map(OptionalDouble::getAsDouble)
        .reduce(Double::sum)
        .get();
    if (hasError.get()) {
      return;
    }
    write(format(solution) + "\n");
    log.info("Computed [%s] = [%s]".formatted(
        problem, format(solution)
    ));
  }

  private String read() {
    try {
      return ofNullable(input.readLine()).orElse("");
    } catch (final IOException ioe) {
      throw new CalculatorException("Could not read input", ioe);
    }
  }

  private OptionalDouble readDouble(final String input) {
    try {
      return OptionalDouble.of(parseDouble(input));
    } catch (final NumberFormatException nfe) {
      log.error("Invalid input");
      write("Cannot parse [%s] as a number.%n".formatted(input));
      return OptionalDouble.empty();
    }
  }

  private void write(final String message) {
    try {
      output.write(message);
      output.flush();
    } catch (final IOException ioe) {
      throw new CalculatorException(
          "Could not write message '%s' to output".formatted(message),
          ioe
      );
    }
  }

  /**
   * Run a calculator, letting it {@link #compute()} using standard input and output.
   */
  public static void main(String... args) {
    val console = ConsoleProvider.getConsole();
    val calculator = new Calculator(console.reader(), console.writer());
    calculator.compute();
  }

}
