package nl.ordina.elwa.fullstack;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;
import lombok.val;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CalculatorTest {

  private static final LogCaptor CALCULATOR_LOGS = LogCaptor.forClass(Calculator.class);

  @AfterEach
  void reset() {
    CALCULATOR_LOGS.clearLogs();
  }

  @AfterAll
  static void cleanup() {
    CALCULATOR_LOGS.close();
  }

  @ParameterizedTest
  @MethodSource("problemProvider")
  void canSolveProblems(final String problem, final String expectedSolution) {
    val reader = new StringReader(problem);
    val writer = new StringWriter();
    val calculator = new Calculator(reader, writer);

    calculator.compute();

    assertEquals(2, CALCULATOR_LOGS.getLogs().size());
    assertThat(CALCULATOR_LOGS.getInfoLogs(), contains(
        "Solving [%s]...".formatted(problem),
        "Computed [%s] = [%s]".formatted(problem, expectedSolution)
    ));
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
        Arguments.of("1 + -2", "-1"),
        Arguments.of("3 * 2", "6"),
        Arguments.of("1 + 3 * 2", "7"),
        Arguments.of("1 + 3 * 2 - 1", "6")
    );
  }

}
