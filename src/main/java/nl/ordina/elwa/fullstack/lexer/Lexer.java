package nl.ordina.elwa.fullstack.lexer;

import java.util.ArrayList;
import java.util.List;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token.Type;

public final class Lexer {

  public List<Token> lex(final String input) throws CalculatorException {
    if (input.isBlank()) {
      return List.of();
    }

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
        checkForNegativeNumber(tokens);
      }
      builder.append(character);
      lastParsedType = characterType;
    }
    tokens.add(new Token(lastParsedType, builder.toString()));
    checkForNegativeNumber(tokens);
    return tokens;
  }

  private Type typeOf(final char character) throws CalculatorException {
    if (Character.isDigit(character) || character == '.') {
      return Type.NUMBER;
    }
    if (character == '+' || character == '-') {
      return Type.OPERATOR;
    }
    throw new CalculatorException("Cannot parse [%s] into a valid token".formatted(character));
  }

  private void checkForNegativeNumber(final List<Token> tokens) {
    if (tokens.size() < 2) {
      return;
    }

    val lastToken = tokens.get(tokens.size() - 1);
    if (lastToken.type() != Type.NUMBER) {
      return;
    }

    val previousToken = tokens.get(tokens.size() - 2);
    /* assert previousToken.type() == Type.OPERATOR;  because there are only 2 types of operators
        and this method is called whenever the other type of operator is encountered */
    val previousValue = previousToken.value();
    if (!previousValue.endsWith("-")
        || (previousValue.length() == 1 && tokens.size() > 2)) {
      return;
    }

    tokens.remove(tokens.size() - 1); // remove lastToken
    tokens.remove(tokens.size() - 1); // remove previousToken
    if (previousValue.length() > 1) {
      tokens.add(new Token(
          previousToken.type(),
          previousValue.substring(0, previousValue.length() - 1)
      ));
    }
    tokens.add(new Token(lastToken.type(), "-" + lastToken.value()));
  }

}
