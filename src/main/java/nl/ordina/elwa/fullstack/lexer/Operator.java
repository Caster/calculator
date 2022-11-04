package nl.ordina.elwa.fullstack.lexer;

import static lombok.AccessLevel.PRIVATE;

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

  /**
   * List of known/implemented operators.
   */
  public static final List<Operator> OPERATORS = List.of(
      new Operator("+", 1, Double::sum),
      new Operator("-", 1, (a, b) -> a - b),
      new Operator("*", 2, (a, b) -> a * b)
  );

  public static boolean isOperator(final char character) {
    return OPERATORS.stream()
        .anyMatch(operator -> operator.value.equals(String.valueOf(character)));
  }

  /**
   * Return the operator from {@link #OPERATORS} that has the given {@code value}, or return a
   * freshly constructed operator that will throw an exception when it is
   * {@link #applyAsDouble(AbstractSyntaxTree, AbstractSyntaxTree) applied}.
   */
  public static Operator of(final String value) {
    return OPERATORS.stream()
        .filter(operator -> operator.value.equals(value))
        .findFirst()
        .orElse(new Operator(value, 0, (a, b) -> {
          throw new CalculatorException("Unknown operator [%s]".formatted(value));
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
