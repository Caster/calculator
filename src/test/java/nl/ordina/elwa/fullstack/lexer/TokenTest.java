package nl.ordina.elwa.fullstack.lexer;

import static nl.ordina.elwa.fullstack.lexer.Token.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import lombok.val;
import nl.ordina.elwa.fullstack.lexer.Token.BracketToken;
import nl.ordina.elwa.fullstack.lexer.Token.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class TokenTest {

  @Test
  void numberTokenHasToString() {
    val token = Token.of(Type.NUMBER, "42", 0);

    assertEquals("42", token.toString());
  }

  @Test
  void operatorTokenHasToString() {
    val token = Token.of(Type.OPERATOR, "input", 0);

    assertEquals("input", token.toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"(", ")"})
  void bracketTokenHasToString(final String input) {
    val token = (BracketToken) Token.of(Type.BRACKET, input, 0);

    assertEquals(input, token.toString());
    assertEquals("(".equals(input), token.isOpen());
  }

  @ParameterizedTest
  @MethodSource("ofNullArgumentsProvider")
  void throwsOnNullOf(
      final Type type, final String input, final String expectedMessage
  ) {
    val exception = assertThrows(
        NullPointerException.class,
        () -> of(type, input, 0)
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  static Stream<Arguments> ofNullArgumentsProvider() {
    return Stream.of(
        Arguments.of(null, "input", "type is marked non-null but is null"),
        Arguments.of(Type.NUMBER, null, "input is marked non-null but is null")
    );
  }

}
