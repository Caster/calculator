package nl.ordina.elwa.fullstack.lexer.operator;

import static lombok.AccessLevel.PRIVATE;
import static nl.ordina.elwa.fullstack.lexer.operator.BinaryOperator.ROOT_EXTRACTION;

import java.util.Collections;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Operators {

  /**
   * List of known/implemented operators.
   */
  @SuppressWarnings("java:S1700")
  public static final List<Operator> OPERATORS = List.of(
      new BinaryOperator("+", 1, Double::sum),
      new BinaryOperator("-", 1, (a, b) -> a - b),
      new BinaryOperator("*", 2, (a, b) -> a * b),
      new BinaryOperator("/", 2, (a, b) -> a / b),
      new BinaryOperator("**", 3, Math::pow),
      new BinaryOperator("root", 3, ROOT_EXTRACTION),
      new BinaryOperator("√", 3, ROOT_EXTRACTION)
  );

  private static final List<Character> OPERATOR_CHARACTERS = OPERATORS.stream()
      .flatMapToInt(operator -> operator.getValue().chars())
      .sorted()
      .distinct()
      .mapToObj(c -> (char) c)
      .toList();

  public static boolean isOperatorCharacter(final char character) {
    return Collections.binarySearch(OPERATOR_CHARACTERS, character) >= 0;
  }

}
