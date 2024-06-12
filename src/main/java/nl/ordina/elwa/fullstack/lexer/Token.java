package nl.ordina.elwa.fullstack.lexer;

import lombok.NonNull;

public record Token(
    @NonNull Type type,
    @NonNull String value
) {

  public enum Type {
    NUMBER,
    OPERATOR
  }

}
