package nl.ordina.elwa.fullstack;

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

  public Calculator(final Reader input, final Writer output) {
    this.input = new BufferedReader(input);
    this.output = new BufferedWriter(output);
    this.lexer = new Lexer();
  }

  public void compute() throws CalculatorException {
    write("> ");
    val problem = read();
    if ("quit".equals(problem)) {
      throw new CalculatorException("quit");
    }
    log.info("Solving [%s]...".formatted(problem));
    val tokens = lexer.lex(problem);
    val syntaxTree = new Parser(tokens).parse();
    final double solution = syntaxTree.compute();
    write("%.0f%n".formatted(solution));
    log.info("Computed [%s] = [%.0f]".formatted(problem, solution));
  }

  private String read() throws CalculatorException {
    try {
      return input.readLine();
    } catch (final IOException ioe) {
      throw new CalculatorException("Could not read input", ioe);
    }
  }

  private void write(final String message) throws CalculatorException {
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
