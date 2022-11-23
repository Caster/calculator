package nl.ordina.elwa.fullstack;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Stream;
import lombok.val;
import nl.altindag.log.LogCaptor;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorTest {

  private static final LogCaptor CALCULATOR_LOGS = LogCaptor.forClass(Calculator.class);
  private static final StringWriter WRITER = new StringWriter();

  @AfterEach
  void reset() {
    CALCULATOR_LOGS.clearLogs();
    WRITER.getBuffer().setLength(0);
  }

  @AfterAll
  static void cleanup() throws IOException {
    CALCULATOR_LOGS.close();
    WRITER.close();
  }

  @ParameterizedTest
  @MethodSource("problemProvider")
  void canSolveProblems(final String problem, final String expectedSolution) {
    val calculator = getCalculatorThatSolves(problem);

    calculator.compute();

    assertEquals(2, CALCULATOR_LOGS.getLogs().size());
    assertThat(CALCULATOR_LOGS.getInfoLogs(), contains(
        "Solving [%s]...".formatted(problem),
        "Computed [%s] = [%s]".formatted(problem, expectedSolution)
    ));
  }

  private Calculator getCalculatorThatSolves(String problem) {
    return new Calculator(new StringReader(problem), WRITER);
  }

  static Stream<Arguments> problemProvider() {
    return Stream.of(
        Arguments.of("", ""),
        Arguments.of(" ", ""),
        Arguments.of("1", "1"),
        Arguments.of("31 + 11", "42"),
        Arguments.of("12+20", "32"),
        Arguments.of("321+ 123", "444"),
        Arguments.of("1 + 2 + 3 + 4", "10"),
        Arguments.of("1 + 2 - 3 + 4", "4"),
        Arguments.of("-1", "-1"),
        Arguments.of("1 +-2", "-1"),
        Arguments.of("1 + -2", "-1"),
        Arguments.of("3 * 2", "6"),
        Arguments.of("1 + 3 * 2", "7"),
        Arguments.of("1 + 3 * 2 - 1", "6"),
        Arguments.of("8 / 4", "2"),
        Arguments.of("3 + 8 / 4", "5"),
        Arguments.of("3 + 8 / 3 - 5", "0.66667"),
        Arguments.of("0.5 * 4", "2"),
        Arguments.of("-3 * -4", "12"),
        Arguments.of("(42)", "42"),
        Arguments.of("(12 + 21)", "33"),
        Arguments.of("(1 + 3) * 2", "8"),
        Arguments.of("14 - ((1 + 3) * 3)", "2"),
        Arguments.of("(1 + 3) 2", "8"),
        Arguments.of("2 (1 + 3)", "8"),
        Arguments.of("(2(1+3)4)", "32"),
        Arguments.of("1 + 2(4 / 8)", "2"),
        Arguments.of("5040 - 2(3)(4(5)(6)7)", "0"),
        Arguments.of("1 + (2)3", "7"),
        Arguments.of("3 ** 4", "81"),
        Arguments.of("2 * 3 ** 4 / 2", "81"),
        Arguments.of("(1 + 3) ** (4 / 2)", "16"),
        Arguments.of("1 + (3)2 ** (4)2", "97"),
        Arguments.of("2 root 4", "2"),
        Arguments.of("3 root 8", "2"),
        Arguments.of("2 root 2", "1.41421"),
        Arguments.of("6√729", "3"),
        Arguments.of("6√(700 + 29)", "3"),
        Arguments.of("1 + (3)2√(4)2", "13"),
        Arguments.of("sqrt 16", "4"),
        Arguments.of("2√4 * sqrt 16 + 3", "11"),
        Arguments.of("1 + (3)sqrt(4)2", "13"),
        Arguments.of("∛27", "3"),
        Arguments.of("∜256", "4")
    );
  }

  @Test
  void canQuit() {
    val calculator = getCalculatorThatSolves("quit");

    val exception = assertThrows(CalculatorException.class, calculator::compute);

    assertEquals("quit", exception.getMessage());
  }

  @Test
  void throwsCustomExceptionOnReadIoException() throws IOException {
    val readerMock = mock(Reader.class);
    val expectedException = new IOException("test");
    doThrow(expectedException).when(readerMock).read(any(), anyInt(), anyInt());
    val calculator = new Calculator(readerMock, WRITER);

    val exception = assertThrows(CalculatorException.class, calculator::compute);

    assertEquals("Could not read input", exception.getMessage());
    assertEquals(expectedException, exception.getCause());
    assertEquals("> ", WRITER.toString());
    assertEquals(0, CALCULATOR_LOGS.getLogs().size());
  }

  @Test
  void throwsCustomExceptionOnWriteIoException() throws IOException {
    val writerMock = mock(Writer.class);
    val expectedException = new IOException("test");
    doNothing().doThrow(expectedException).when(writerMock).flush();
    val calculator = new Calculator(new StringReader("1"), writerMock);

    val exception = assertThrows(CalculatorException.class, calculator::compute);

    val expectedMessage = "Could not write message '1\n' to output";
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(expectedException, exception.getCause());
    assertEquals(2, CALCULATOR_LOGS.getLogs().size());
    assertThat(CALCULATOR_LOGS.getInfoLogs(), contains("Solving [1]..."));
    assertThat(CALCULATOR_LOGS.getErrorLogs(), contains(
        "Cannot solve [1]: %s".formatted(expectedMessage)
    ));
  }

  @ParameterizedTest
  @MethodSource("invalidProblemProvider")
  void printsErrorMessageOnInvalidProblems(
      final String problem, final String expectedMessage, final int problemIndex
  ) {
    val calculator = getCalculatorThatSolves(problem);

    val exception = assertThrows(CalculatorException.class, calculator::compute);

    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(
        "> %s┗ %s%n".formatted(" ".repeat(2 + problemIndex), expectedMessage),
        WRITER.toString()
    );
    assertEquals(2, CALCULATOR_LOGS.getLogs().size());
    assertThat(CALCULATOR_LOGS.getErrorLogs(), contains(
        "Cannot solve [%s]: %s".formatted(problem, expectedMessage)
    ));
  }

  static Stream<Arguments> invalidProblemProvider() {
    return Stream.of(
        Arguments.of("a", "Cannot parse [a] into a valid token", 0),
        Arguments.of("+", "Expected a number or opening bracket", 0),
        Arguments.of(")", "Expected a number or opening bracket", 0),
        Arguments.of("1.0.", "Invalid number: multiple points", 3),
        Arguments.of("1++1", "Unknown operator [++]", 1),
        Arguments.of("1 1", "Expected an operator, got [1]", 2),
        Arguments.of("(1)2 3", "Expected an operator, got [3]", 5)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"1+", "(1("})
  void printsErrorMessageWithoutIndexOnInvalidProblem(final String problem) {
    val calculator = getCalculatorThatSolves(problem);

    val exception = assertThrows(CalculatorException.class, calculator::compute);

    val expectedMessage = "Unexpected end of input";
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(
        "> %s%n".formatted(expectedMessage),
        WRITER.toString()
    );
    assertEquals(2, CALCULATOR_LOGS.getLogs().size());
    assertThat(CALCULATOR_LOGS.getErrorLogs(), contains(
        "Cannot solve [%s]: %s".formatted(problem, expectedMessage)
    ));
  }

  @Test
  void canRunAsMain() throws IOException {
    final InputStream defaultSystemIn = System.in;
    final PrintStream defaultSystemOut = System.out;
    try (
        val byteArrayInputStream = new ByteArrayInputStream("40 + 2\n1+\nquit\n".getBytes(UTF_8));
        val byteArrayOutputStream = new ByteArrayOutputStream()
    ) {
      System.setIn(byteArrayInputStream);
      System.setOut(new PrintStream(byteArrayOutputStream));

      Calculator.main();

      assertEquals("> 42\n> Unexpected end of input\n> ", byteArrayOutputStream.toString());
    } finally {
      System.setOut(defaultSystemOut);
      System.setIn(defaultSystemIn);
    }
  }

}
