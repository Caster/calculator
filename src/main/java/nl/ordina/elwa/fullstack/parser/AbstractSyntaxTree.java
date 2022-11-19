package nl.ordina.elwa.fullstack.parser;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.token.NumberToken;
import nl.ordina.elwa.fullstack.lexer.token.OperatorToken;
import nl.ordina.elwa.fullstack.lexer.token.Token;
import nl.ordina.elwa.fullstack.lexer.token.Token.Type;

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

  @Getter private final AbstractSyntaxTree leftChild;
  @Getter private final AbstractSyntaxTree rightChild;
  @Getter private final Token token;
  @Getter private final boolean bracketed;

  /**
   * Construct a leaf node that holds a {@link NumberToken number token}.
   */
  public static AbstractSyntaxTree leaf(@NonNull final Token token) {
    if (token.getType() != Type.NUMBER) {
      throw new CalculatorException("Can only have numbers as leaves of an AST", token.getIndex());
    }
    return new AbstractSyntaxTree(null, null, token, false);
  }

  /**
   * Construct an internal or root node that holds an {@link OperatorToken operator token}.
   */
  public static AbstractSyntaxTree node(
      @NonNull final AbstractSyntaxTree leftChild,
      @NonNull final Token token,
      @NonNull final AbstractSyntaxTree rightChild,
      final boolean bracketed
  ) {
    if (token.getType() != Type.OPERATOR) {
      throw new CalculatorException("Can only have operators as nodes of an AST", token.getIndex());
    }
    return new AbstractSyntaxTree(leftChild, rightChild, token, bracketed);
  }

  private AbstractSyntaxTree(
      final AbstractSyntaxTree leftChild, final AbstractSyntaxTree rightChild,
      final Token token, final boolean bracketed
  ) {
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.token = token;
    this.bracketed = bracketed;
  }

  /**
   * Return a shallow copy of this {@link AbstractSyntaxTree} where the {@link #isBracketed()} is
   * marked {@code true}. Will still return a shallow copy if the node is already bracketed.
   */
  public AbstractSyntaxTree bracketed() {
    return new AbstractSyntaxTree(leftChild, rightChild, token, true);
  }

  /**
   * Return either the {@link NumberToken#getValue() value} if this is a leaf node, or the result
   * of {@link nl.ordina.elwa.fullstack.lexer.Operator#applyAsDouble(AbstractSyntaxTree,
   * AbstractSyntaxTree) applying} the operator to the two child nodes (recursively) if this is an
   * internal or root node.
   */
  public double compute() {
    if (isLeaf()) {
      return ((NumberToken) token).getValue();
    }
    return ((OperatorToken) token).getOperator().applyAsDouble(leftChild, rightChild);
  }

  /**
   * Return whether the operator represented by the token of this node has a lower priority than
   * the operator represented by the given token. Always return {@code false} when this node has
   * a non-operator token.
   */
  public boolean hasLowerPriorityThan(final OperatorToken thatToken) {
    if (token.getType() != Type.OPERATOR) {
      return false;
    }
    val thisOperator = ((OperatorToken) token).getOperator();
    val thatOperator = thatToken.getOperator();
    return (thisOperator.comparePriorityTo(thatOperator) > 0);
  }

  /**
   * Return a new node that has the same left child and token as this node, but a new right child.
   * The newly constructed right child will have:
   * <ul>
   *   <li>this node's right child as its left child;</li>
   *   <li>the given token ({@code rightToken}) as its token;</li>
   *   <li>the given node ({@code rightGrandChild}) as its right child.</li>
   * </ul>
   * Neither the returned node nor its right child will be marked as {@link #isBracketed()
   * bracketed}.
   */
  public AbstractSyntaxTree withReplacedRightChild(
      final Token rightToken, final AbstractSyntaxTree rightGrandChild
  ) {
    return node(
        leftChild,
        token,
        node(
            rightChild,
            rightToken,
            rightGrandChild,
            false
        ),
        false
    );
  }

  private boolean isLeaf() {
    return leftChild == null;
  }

  @Override
  public String toString() {
    if (isLeaf()) {
      return token.toString();
    }
    return "%s %s %s".formatted(leftChild.toString(), token.toString(), rightChild.toString());
  }
}
