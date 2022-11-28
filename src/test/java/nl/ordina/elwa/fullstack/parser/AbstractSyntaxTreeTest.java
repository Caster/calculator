package nl.ordina.elwa.fullstack.parser;

import static nl.ordina.elwa.fullstack.parser.AbstractSyntaxTree.leaf;
import static nl.ordina.elwa.fullstack.parser.AbstractSyntaxTree.node;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.token.Token;
import nl.ordina.elwa.fullstack.lexer.token.Token.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.MethodSource;

class AbstractSyntaxTreeTest {

  @Test
  void canGetProperties() {
    val leftChild = AbstractSyntaxTree.leaf(Token.of(Type.NUMBER, "21", 0));
    val token = Token.of(Type.OPERATOR, "*", 2);
    val rightChild = AbstractSyntaxTree.leaf(Token.of(Type.NUMBER, "2", 3));
    val node = AbstractSyntaxTree.node(leftChild, token, rightChild, true);

    assertEquals("21", leftChild.toString());

    assertSame(leftChild, node.getLeftChild());
    assertSame(token, node.getToken());
    assertSame(rightChild, node.getRightChild());
    assertTrue(node.isBracketed());
    assertEquals("21 * 2", node.toString());
  }

  @ParameterizedTest
  @EnumSource(value = Type.class, names = {"NUMBER"}, mode = Mode.EXCLUDE)
  void throwsOnInvalidLeaf(final Type invalidType) {
    val token = Token.of(invalidType, "", 42);

    val exception = assertThrows(CalculatorException.class, () -> leaf(token));

    assertEquals("Can only have numbers as leaves of an AST", exception.getMessage());
    assertEquals(42, exception.getProblemIndex());
  }

  @Test
  void throwsOnNullLeaf() {
    @SuppressWarnings("ConstantConditions")
    val exception = assertThrows(NullPointerException.class, () -> leaf(null));
    assertEquals("token is marked non-null but is null", exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(value = Type.class, names = {"OPERATOR"}, mode = Mode.EXCLUDE)
  void throwsOnInvalidSingleChildNode(final Type invalidType) {
    val token = Token.of(invalidType, "1", 42);
    val tree = leaf(Token.of(Type.NUMBER, "1", 0));

    val exception = assertThrows(CalculatorException.class, () -> node(token, tree, false));

    assertEquals("Can only have operators as nodes of an AST", exception.getMessage());
    assertEquals(42, exception.getProblemIndex());
  }

  @ParameterizedTest
  @EnumSource(value = Type.class, names = {"OPERATOR"}, mode = Mode.EXCLUDE)
  void throwsOnInvalidNode(final Type invalidType) {
    val token = Token.of(invalidType, "1", 42);
    val tree = leaf(Token.of(Type.NUMBER, "1", 0));

    val exception = assertThrows(CalculatorException.class, () -> node(tree, token, tree, false));

    assertEquals("Can only have operators as nodes of an AST", exception.getMessage());
    assertEquals(42, exception.getProblemIndex());
  }

  @ParameterizedTest
  @MethodSource("singleChildNodeNullArgumentsProvider")
  void throwsOnNullSingleChildNode(
      final Token token, final AbstractSyntaxTree child, final String expectedMessage
  ) {
    val exception = assertThrows(
        NullPointerException.class,
        () -> node(token, child, false)
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  static Stream<Arguments> singleChildNodeNullArgumentsProvider() {
    val token = Token.of(Type.NUMBER, "1", 42);
    val tree = leaf(token);
    return Stream.of(
        Arguments.of(null, tree, "token is marked non-null but is null"),
        Arguments.of(token, null, "child is marked non-null but is null")
    );
  }

  @ParameterizedTest
  @MethodSource("nodeNullArgumentsProvider")
  void throwsOnNullNode(
      final AbstractSyntaxTree leftChild, final Token token, final AbstractSyntaxTree rightChild,
      final String expectedMessage
  ) {
    val exception = assertThrows(
        NullPointerException.class,
        () -> node(leftChild, token, rightChild, false)
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  static Stream<Arguments> nodeNullArgumentsProvider() {
    val token = Token.of(Type.NUMBER, "1", 42);
    val tree = leaf(token);
    return Stream.of(
        Arguments.of(null, token, tree, "leftChild is marked non-null but is null"),
        Arguments.of(tree, null, tree, "token is marked non-null but is null"),
        Arguments.of(tree, token, null, "rightChild is marked non-null but is null")
    );
  }

}
