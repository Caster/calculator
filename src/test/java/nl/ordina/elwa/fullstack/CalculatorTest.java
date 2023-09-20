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
  void canSolveProblems(final String input1, final String input2, final String expectedSolution) {
    val input = input1 + "\n" + input2;
    val calculator = getCalculatorThatSolves(input);

    calculator.compute();

    assertEquals(2, CALCULATOR_LOGS.getLogs().size());
    val problem = input1 + " + " + input2;
    assertThat(CALCULATOR_LOGS.getInfoLogs(), contains(
        "Solving [%s]...".formatted(problem),
        "Computed [%s] = [%s]".formatted(problem, expectedSolution)
    ));
    assertEquals("Input 1? Input 2? %s%n".formatted(expectedSolution), WRITER.toString());
  }

  private Calculator getCalculatorThatSolves(final String problem) {
    return new Calculator(new StringReader(problem), WRITER);
  }

  static Stream<Arguments> problemProvider() {
    return Stream.of(
        Arguments.of("1", "1", "2"),
        Arguments.of("31", "11", "42"),
        Arguments.of("1.5", "3.25", "4.75")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidProblemProvider")
  void printsCustomErrorOnInvalidInput(
      final String input1, final String input2, final String expectedOutput
  ) {
    val input = input1 + "\n" + input2;
    val calculator = getCalculatorThatSolves(input);

    calculator.compute();

    assertEquals(1, CALCULATOR_LOGS.getLogs().size());
    assertThat(CALCULATOR_LOGS.getErrorLogs(), contains("Invalid input"));
    assertEquals(expectedOutput + "\n", WRITER.toString());
  }

  static Stream<Arguments> invalidProblemProvider() {
    return Stream.of(
        Arguments.of("a", "1", "Input 1? Cannot parse [a] as a number."),
        Arguments.of("1", "b", "Input 1? Input 2? Cannot parse [b] as a number.")
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
    assertEquals("Input 1? ", WRITER.toString());
    assertEquals(0, CALCULATOR_LOGS.getLogs().size());
  }

  @Test
  void throwsCustomExceptionOnWriteIoException() throws IOException {
    val writerMock = mock(Writer.class);
    val expectedException = new IOException("test");
    doNothing().doThrow(expectedException).when(writerMock).flush();
    val calculator = new Calculator(new StringReader("1"), writerMock);

    val exception = assertThrows(CalculatorException.class, calculator::compute);

    val expectedMessage = "Could not write message 'Input 2? ' to output";
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(expectedException, exception.getCause());
    assertEquals(0, CALCULATOR_LOGS.getLogs().size());
  }

  @Test
  void canRunAsMain() throws IOException {
    final InputStream defaultSystemIn = System.in;
    final PrintStream defaultSystemOut = System.out;
    try (
        val byteArrayInputStream = new ByteArrayInputStream("40\n2\n".getBytes(UTF_8));
        val byteArrayOutputStream = new ByteArrayOutputStream()
    ) {
      System.setIn(byteArrayInputStream);
      System.setOut(new PrintStream(byteArrayOutputStream));

      Calculator.main();

      assertEquals("Input 1? Input 2? 42\n", byteArrayOutputStream.toString());
    } finally {
      System.setOut(defaultSystemOut);
      System.setIn(defaultSystemIn);
    }
  }

}
