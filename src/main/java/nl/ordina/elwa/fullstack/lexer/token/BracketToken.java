package nl.ordina.elwa.fullstack.lexer.token;

public final class BracketToken extends Token {

  private final boolean isOpen;

  BracketToken(final String input, final int index) {
    super(Type.BRACKET, index);
    this.isOpen = "(".equals(input);
  }

  public boolean isOpen() {
    return isOpen;
  }

  @Override
  public String toString() {
    return (isOpen ? "(" : ")");
  }

}
