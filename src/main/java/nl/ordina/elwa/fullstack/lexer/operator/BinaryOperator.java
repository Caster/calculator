package nl.ordina.elwa.fullstack.lexer.operator;

import java.util.function.DoubleBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import nl.ordina.elwa.fullstack.parser.AbstractSyntaxTree;

public final class BinaryOperator extends Operator
    implements ToDoubleBiFunction<AbstractSyntaxTree, AbstractSyntaxTree> {

  public static final DoubleBinaryOperator ROOT_EXTRACTION = (a, b) -> Math.pow(b, 1.0 / a);

  private final DoubleBinaryOperator operation;

  BinaryOperator(final String value, final int priority, final DoubleBinaryOperator operation) {
    super(value, priority);
    this.operation = operation;
  }

  @Override
  public double applyAsDouble(final AbstractSyntaxTree nodeA, final AbstractSyntaxTree nodeB) {
    return operation.applyAsDouble(nodeA.compute(), nodeB.compute());
  }
}
