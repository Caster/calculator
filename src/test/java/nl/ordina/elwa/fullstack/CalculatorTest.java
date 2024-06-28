package nl.ordina.elwa.fullstack;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
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
  @ValueSource(strings = {"", " ", "\t"})
  void canIgnoreBlankInput(final String problem) {
    val calculator = getCalculatorThatSolves(problem);

    calculator.compute();

    assertEquals(1, CALCULATOR_LOGS.getLogs().size());
    assertThat(CALCULATOR_LOGS.getInfoLogs(), contains("Solving [%s]...".formatted(problem)));
    assertEquals("> ", WRITER.toString());
  }

  private Calculator getCalculatorThatSolves(final String problem) {
    return new Calculator(new StringReader(problem), WRITER);
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
    assertEquals("> %s%n".formatted(expectedSolution), WRITER.toString());
  }

  static Stream<Arguments> problemProvider() {
    return Stream.of(
        Arguments.of("1 + 1", "2"),
        Arguments.of("31+11", "42"),
        Arguments.of("1.5+ 3.25", "4.75"),
        Arguments.of("1 + 2 + 3 + 4", "10"),
        Arguments.of("1 + 2 - 3 + 4", "4"),
        Arguments.of("-1", "-1"),
        Arguments.of("1 + -2", "-1"),
        Arguments.of("1 - -2", "3"),
        Arguments.of("1--2", "3"),
        Arguments.of("-4 -1", "-5")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidProblemProvider")
  void printsCustomErrorOnInvalidInput(final String problem, final String expectedOutput) {
    val calculator = getCalculatorThatSolves(problem);

    calculator.compute();

    assertEquals(1, CALCULATOR_LOGS.getLogs().size());
    assertEquals("> %s%n".formatted(expectedOutput), WRITER.toString());
  }

  static Stream<Arguments> invalidProblemProvider() {
    return Stream.of(
        Arguments.of("a + 1", "Cannot parse [a] into a valid token"),
        Arguments.of("1 + b", "Cannot parse [b] into a valid token")
    );
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
    doThrow(expectedException).when(writerMock).flush();
    val calculator = new Calculator(new StringReader("1"), writerMock);

    val exception = assertThrows(CalculatorException.class, calculator::compute);

    val expectedMessage = "Could not write message '> ' to output";
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(expectedException, exception.getCause());
    assertEquals(0, CALCULATOR_LOGS.getLogs().size());
  }

  @Test
  void canRunAsMain() {
    try (val consoleProviderMock = mockStatic(ConsoleProvider.class)) {
      val consoleMock = mock(Console.class);
      doReturn(new StringReader("40 + 2\n")).when(consoleMock).reader();
      val outputWriter = new StringWriter();
      doReturn(new PrintWriter(outputWriter)).when(consoleMock).writer();
      consoleProviderMock.when(ConsoleProvider::getConsole).thenReturn(consoleMock);

      Calculator.main();

      assertEquals("> 42\n", outputWriter.toString());
    }
  }

}
