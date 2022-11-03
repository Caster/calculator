package nl.ordina.elwa.fullstack.lexer;

import java.util.ArrayList;
import java.util.List;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token.Type;

public final class Lexer {

  public List<Token> lex(final String input) throws CalculatorException {
    val tokens = new ArrayList<Token>();
    val builder = new StringBuilder();
    Type lastParsedType = null;
    for (val character : input.toCharArray()) {
      if (Character.isWhitespace(character)) {
        continue;
      }

      val characterType = typeOf(character);
      if (characterType != lastParsedType && lastParsedType != null) {
        tokens.add(new Token(lastParsedType, builder.toString()));
        builder.delete(0, builder.length());
      }
      builder.append(character);
      lastParsedType = characterType;
    }
    tokens.add(new Token(lastParsedType, builder.toString()));
    return tokens;
  }

  private Type typeOf(final char character) throws CalculatorException {
    if (Character.isDigit(character)) {
      return Type.NUMBER;
    }
    if (character == '+') {
      return Type.OPERATOR;
    }
    throw new CalculatorException("Cannot parse [%s] into a valid token".formatted(character));
  }

}
