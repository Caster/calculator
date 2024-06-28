package nl.ordina.elwa.fullstack;

import static java.util.Optional.ofNullable;
import static nl.ordina.elwa.fullstack.parser.Parser.parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Lexer;

@Slf4j
public final class Calculator {

  private static final DecimalFormat FORMAT = new DecimalFormat("#.#####");

  private static String format(final double number) {
    return FORMAT.format(number);
  }

  private final BufferedReader input;
  private final BufferedWriter output;
  private final Lexer lexer;

  /**
   * Construct a calculator that will read its input from the given {@link Reader} and will write
   * prompts and solutions to the given {@link Writer}.
   */
  public Calculator(final Reader input, final Writer output) {
    this.input = new BufferedReader(input);
    this.output = new BufferedWriter(output);
    this.lexer = new Lexer();
  }

  /**
   * Write a prompt, read a problem, solve it and print the result.
   */
  public void compute() {
    write("> ");
    val problem = read();
    log.info("Solving [%s]...".formatted(problem));
    try {
      val tokens = lexer.lex(problem);
      if (tokens.isEmpty()) {
        return;
      }
      val syntaxTree = parse(tokens);
      val solution = syntaxTree.compute();
      write(format(solution) + "\n");
      log.info("Computed [%s] = [%s]".formatted(
          problem, format(solution)
      ));
    } catch (final CalculatorException ce) {
      write("%s%n".formatted(ce.getMessage()));
    }
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
    val console = ConsoleProvider.getConsole();
    val calculator = new Calculator(console.reader(), console.writer());
    calculator.compute();
  }

}
