package nl.ordina.elwa.fullstack.lexer;

import static lombok.AccessLevel.PRIVATE;

import java.util.Collections;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.parser.AbstractSyntaxTree;

@RequiredArgsConstructor(access = PRIVATE)
public final class Operator implements ToDoubleBiFunction<AbstractSyntaxTree, AbstractSyntaxTree> {

  @Getter
  private final String value;
  private final int priority;
  private final DoubleBinaryOperator operation;

  private static final DoubleBinaryOperator ROOT_EXTRACTION = (a, b) -> Math.pow(b, 1.0 / a);

  /**
   * List of known/implemented operators.
   */
  public static final List<Operator> OPERATORS = List.of(
      new Operator("+", 1, Double::sum),
      new Operator("-", 1, (a, b) -> a - b),
      new Operator("*", 2, (a, b) -> a * b),
      new Operator("/", 2, (a, b) -> a / b),
      new Operator("**", 3, Math::pow),
      new Operator("root", 3, ROOT_EXTRACTION),
      new Operator("√", 3, ROOT_EXTRACTION)
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

  /**
   * Return the operator from {@link #OPERATORS} that has the given {@code value}, or return a
   * freshly constructed operator that will throw an exception when it is
   * {@link #applyAsDouble(AbstractSyntaxTree, AbstractSyntaxTree) applied}.
   */
  public static Operator of(final String value, final int index) {
    return OPERATORS.stream()
        .filter(operator -> operator.value.equals(value))
        .findFirst()
        .orElse(new Operator(value, 0, (a, b) -> {
          throw new CalculatorException("Unknown operator [%s]".formatted(value), index);
        }));
  }

  public int comparePriorityTo(final Operator that) {
    return that.priority - this.priority;
  }

  @Override
  @SneakyThrows(CalculatorException.class)
  public double applyAsDouble(final AbstractSyntaxTree nodeA, final AbstractSyntaxTree nodeB) {
    return operation.applyAsDouble(nodeA.compute(), nodeB.compute());
  }
}
