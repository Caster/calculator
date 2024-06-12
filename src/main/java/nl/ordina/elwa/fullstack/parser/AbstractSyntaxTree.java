package nl.ordina.elwa.fullstack.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token;
import nl.ordina.elwa.fullstack.lexer.Token.Type;

@Getter(AccessLevel.PACKAGE)
public final class AbstractSyntaxTree {

  /*
     Input: 3 + 5 * 4
            +      <- root node (operator, or number in single-node tree)
           / \
          /   *    <- internal node (operators!)
         /   / \
        3   5   4  <- leaves (leaf nodes, numbers!)

     Input: (3 + 5) * 4
            *
           / \
          +   \
         / \   \
        3   5   4
   */

  public final AbstractSyntaxTree leftChild;
  public final AbstractSyntaxTree rightChild;
  public final Token token;

  public static AbstractSyntaxTree leaf(@NonNull final Token token) throws CalculatorException {
    if (token.type() != Type.NUMBER) {
      throw new CalculatorException("Can only have numbers as leaves of an AST");
    }
    return new AbstractSyntaxTree(null, null, token);
  }

  public static AbstractSyntaxTree node(
      @NonNull final AbstractSyntaxTree leftChild,
      @NonNull final Token token,
      @NonNull final AbstractSyntaxTree rightChild
  ) throws CalculatorException {
    if (token.type() != Type.OPERATOR) {
      throw new CalculatorException("Can only have operators as nodes of an AST");
    }
    return new AbstractSyntaxTree(leftChild, rightChild, token);
  }

  private AbstractSyntaxTree(
      final AbstractSyntaxTree leftChild, final AbstractSyntaxTree rightChild, final Token token
  ) {
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.token = token;
  }

  public double compute() throws CalculatorException {
    if (isLeaf()) {
      return Double.parseDouble(token.value());
    }
    return switch (token.value()) {
      case "+" -> leftChild.compute() + rightChild.compute();
      case "-" -> leftChild.compute() - rightChild.compute();
      default -> throw new CalculatorException(
          "Unknown operator [%s]".formatted(token.value())
      );
    };
  }

  private boolean isLeaf() {
    return leftChild == null;
  }

}
