package nl.ordina.elwa.fullstack;

import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
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
    write("Input 1? ");
    val input1 = Double.parseDouble(read());
    write("Input 2? ");
    val input2 = Double.parseDouble(read());
    log.info("Solving [%s + %s]...".formatted(format(input1), format(input2)));

    val solution = input1 + input2;
    write(format(solution));
    log.info("Computed [%s + %s] = [%s]".formatted(
        format(input1), format(input2), format(solution)
    ));
  }

  private String read() {
    try {
      return ofNullable(input.readLine()).orElse("");
    } catch (final IOException ioe) {
      throw new CalculatorException("Could not read input", ioe);
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
    @SuppressWarnings("java:S106")
    val calculator = new Calculator(
        new InputStreamReader(System.in), new OutputStreamWriter(System.out)
    );
    calculator.compute();
  }

}
