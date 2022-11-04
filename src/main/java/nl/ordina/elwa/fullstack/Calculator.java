package nl.ordina.elwa.fullstack;

import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Lexer;
import nl.ordina.elwa.fullstack.parser.Parser;

@Slf4j
public final class Calculator {

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
   * Write a prompt, read a problem, compute and write the solution.
   */
  public void compute() {
    write("> ");
    val problem = read();
    if ("quit".equals(problem)) {
      throw new CalculatorException("quit");
    }
    log.info("Solving [%s]...".formatted(problem));

    val tokens = lexer.lex(problem);
    if (tokens.size() == 0) {
      log.info("Computed [%s] = []".formatted(problem));
      return;
    }

    val syntaxTree = new Parser(tokens).parse();
    final double solution = syntaxTree.compute();
    write("%.0f%n".formatted(solution));
    log.info("Computed [%s] = [%.0f]".formatted(problem, solution));
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
   * Run a calculator, letting it {@link #compute()} until it encounters an error or quits.
   */
  @SuppressWarnings({"java:S106", "java:S2189"})
  public static void main(String... args) {
    val calculator = new Calculator(
        new InputStreamReader(System.in), new OutputStreamWriter(System.out)
    );
    while (true) {
      try {
        calculator.compute();
      } catch (final CalculatorException ce) {
        System.out.println(ce.getMessage());
        break;
      }
    }
  }

}
