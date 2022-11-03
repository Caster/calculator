package nl.ordina.elwa.fullstack.lexer;

import lombok.Getter;

public final class Token {

  public enum Type {
    NUMBER,
    OPERATOR
  }

  @Getter
  private final Type type;
  @Getter
  private final String value;

  public Token(final Type type, final String value) {
    this.type = type;
    this.value = value;
  }

}
