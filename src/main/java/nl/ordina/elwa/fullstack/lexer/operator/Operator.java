package nl.ordina.elwa.fullstack.lexer.operator;

import static lombok.AccessLevel.PROTECTED;
import static nl.ordina.elwa.fullstack.lexer.operator.Operators.OPERATORS;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.parser.AbstractSyntaxTree;

@RequiredArgsConstructor(access = PROTECTED)
public sealed class Operator permits BinaryOperator, UnaryOperator {

  @Getter
  private final String value;
  private final int priority;

  /**
   * Return the operator from {@link Operators#OPERATORS} that has the given {@code value}, or
   * return a freshly constructed binary operator that will throw an exception when it is
   * {@link BinaryOperator#applyAsDouble(AbstractSyntaxTree, AbstractSyntaxTree) applied}.
   */
  public static Operator of(final String value, final int index) {
    return OPERATORS.stream()
        .filter(operator -> operator.value.equals(value))
        .findFirst()
        .orElse(new BinaryOperator(value, 0, (a, b) -> {
          throw new CalculatorException("Unknown operator [%s]".formatted(value), index);
        }));
  }

  public int comparePriorityTo(final Operator that) {
    return that.priority - this.priority;
  }

}
