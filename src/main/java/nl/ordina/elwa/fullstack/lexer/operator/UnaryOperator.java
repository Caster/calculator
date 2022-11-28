package nl.ordina.elwa.fullstack.lexer.operator;

import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleFunction;
import nl.ordina.elwa.fullstack.parser.AbstractSyntaxTree;

public final class UnaryOperator extends Operator
    implements ToDoubleFunction<AbstractSyntaxTree> {

  private final DoubleUnaryOperator operation;

  UnaryOperator(final String value, final int priority, final DoubleUnaryOperator operation) {
    super(value, priority);
    this.operation = operation;
  }

  @Override
  public double applyAsDouble(final AbstractSyntaxTree node) {
    return operation.applyAsDouble(node.compute());
  }
}
