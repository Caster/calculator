package nl.ordina.elwa.fullstack.lexer;

import java.util.ArrayList;
import java.util.List;
import lombok.val;
import nl.ordina.elwa.fullstack.exception.CalculatorException;
import nl.ordina.elwa.fullstack.lexer.Token.NumberToken;
import nl.ordina.elwa.fullstack.lexer.Token.OperatorToken;
import nl.ordina.elwa.fullstack.lexer.Token.Type;

public final class Lexer {

  /**
   * Transform the given input into a list of tokens that can be parsed further.
   */
  public List<Token> lex(final String input) {
    val tokens = new ArrayList<Token>();
    val builder = new StringBuilder();
    Type lastParsedType = null;
    for (val character : input.toCharArray()) {
      if (Character.isWhitespace(character)) {
        continue;
      }

      val characterType = typeOf(character);
      if (characterType != lastParsedType && lastParsedType != null) {
        tokens.add(Token.of(lastParsedType, builder.toString()));
        builder.delete(0, builder.length());
        checkForNegativeNumber(tokens);
      }
      builder.append(character);
      lastParsedType = characterType;
    }
    if (lastParsedType != null && builder.length() > 0) {
      tokens.add(Token.of(lastParsedType, builder.toString()));
      checkForNegativeNumber(tokens);
    }
    return tokens;
  }

  private Type typeOf(final char character) {
    if (Character.isDigit(character) || character == '.') {
      return Type.NUMBER;
    }
    if (Operator.isOperator(character)) {
      return Type.OPERATOR;
    }
    throw new CalculatorException("Cannot parse [%s] into a valid token".formatted(character));
  }

  private void checkForNegativeNumber(final List<Token> tokens) {
    if (tokens.size() < 2) {
      return;
    }

    val lastToken = tokens.get(tokens.size() - 1);
    if (lastToken.getType() != Type.NUMBER) {
      return;
    }

    val previousToken = tokens.get(tokens.size() - 2);
    if (previousToken.getType() != Type.OPERATOR) {
      return;
    }
    val lastOperator = (OperatorToken) previousToken;
    val operatorValue = lastOperator.getOperator().getValue();
    if (!operatorValue.endsWith("-")
        || (operatorValue.length() == 1 && tokens.size() > 2)) {
      return;
    }

    tokens.remove(tokens.size() - 1); // remove lastToken
    tokens.remove(tokens.size() - 1); // remove previousToken
    if (operatorValue.length() > 1) {
      tokens.add(Token.of(
          Type.OPERATOR,
          operatorValue.substring(0, operatorValue.length() - 1)
      ));
    }
    tokens.add(Token.of(Type.NUMBER, "-" + ((NumberToken) lastToken).getValue()));
  }

}
