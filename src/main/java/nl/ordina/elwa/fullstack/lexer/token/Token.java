package nl.ordina.elwa.fullstack.lexer.token;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PROTECTED)
public sealed class Token permits BracketToken, NumberToken, OperatorToken {

  public enum Type {
    NUMBER,
    OPERATOR,
    BRACKET
  }

  @Getter
  private final Type type;
  @Getter
  private final int index;

  /**
   * Returns a token for the given type and input.
   *
   * @see NumberToken
   * @see OperatorToken
   */
  public static Token of(@NonNull final Type type, @NonNull final String input, final int index) {
    return switch (type) {
      case NUMBER -> new NumberToken(input, index);
      case OPERATOR -> new OperatorToken(input, index);
      default -> new BracketToken(input, index);
    };
  }

}
