package nl.ordina.elwa.fullstack.parser;

import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token;
import nl.ordina.elwa.fullstack.lexer.Token.Type;

public final class AbstractSyntaxTree {

  public final AbstractSyntaxTree leftChild;
  public final AbstractSyntaxTree rightChild;
  public final Token value;

  public static AbstractSyntaxTree leaf(final Token value) throws CalculatorException {
    if (value.getType() != Type.NUMBER) {
      throw new CalculatorException("Can only have numbers as leaves of an AST");
    }
    return new AbstractSyntaxTree(null, null, value);
  }

  public static AbstractSyntaxTree node(
      final AbstractSyntaxTree leftChild, final Token value, final AbstractSyntaxTree rightChild
  ) throws CalculatorException {
    if (value.getType() != Type.OPERATOR) {
      throw new CalculatorException("Can only have operators as nodes of an AST");
    }
    return new AbstractSyntaxTree(leftChild, rightChild, value);
  }

  private AbstractSyntaxTree(
      final AbstractSyntaxTree leftChild, final AbstractSyntaxTree rightChild, final Token value
  ) {
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.value = value;
  }

  public double compute() throws CalculatorException {
    if (isLeaf()) {
      return Double.parseDouble(value.getValue());
    }
    return switch (value.getValue()) {
      case "+" -> leftChild.compute() + rightChild.compute();
      default -> throw new CalculatorException(
          "Operator [%s] is not yet implemented fully".formatted(value.getValue())
      );
    };
  }

  private boolean isLeaf() {
    return leftChild == null;
  }

}
