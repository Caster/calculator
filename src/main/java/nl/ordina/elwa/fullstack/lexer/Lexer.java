package nl.ordina.elwa.fullstack.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ObjIntConsumer;
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
    final ObjIntConsumer<Type> addToken = (lastParsedType, i) -> {
      if (builder.length() > 0) {
        tokens.add(Token.of(lastParsedType, builder.toString(), i - builder.length()));
        builder.delete(0, builder.length());
        checkForNegativeNumber(tokens);
      }
    };

    Type lastParsedType = null;
    val characters = input.toCharArray();
    for (int i = 0; i < characters.length; i++) {
      val character = characters[i];
      if (Character.isWhitespace(character)) {
        addToken.accept(lastParsedType, i);
        continue;
      }

      val characterType = typeOf(character, i);
      if (characterType == Type.BRACKET
          || (characterType != lastParsedType && lastParsedType != null)) {
        addToken.accept(lastParsedType, i);
      }

      builder.append(character);
      lastParsedType = characterType;
    }

    if (lastParsedType != null && builder.length() > 0) {
      addToken.accept(lastParsedType, characters.length);
    }
    return tokens;
  }

  private Type typeOf(final char character, final int index) {
    if (Character.isDigit(character) || character == '.') {
      return Type.NUMBER;
    }
    if (Operator.isOperator(character)) {
      return Type.OPERATOR;
    }
    if (character == '(' || character == ')') {
      return Type.BRACKET;
    }
    throw new CalculatorException(
        "Cannot parse [%s] into a valid token".formatted(character), index
    );
  }

  private void checkForNegativeNumber(final List<Token> tokens) {
    if (tokens.size() < 2) {
      return;
    }

    // requirement 1: last token is a number
    val lastToken = tokens.get(tokens.size() - 1);
    if (lastToken.getType() != Type.NUMBER) {
      return;
    }

    // requirement 2: second to last token is parsed as operator
    val previousToken = tokens.get(tokens.size() - 2);
    if (previousToken.getType() != Type.OPERATOR) {
      return;
    }
    val lastOperator = (OperatorToken) previousToken;
    val operatorValue = lastOperator.getOperator().getValue();
    // requirement 3: operator ends with '-' and is preceded by nothing, or another operator
    if (!operatorValue.endsWith("-")
        || (operatorValue.length() == 1
          && tokens.size() > 2
          && tokens.get(tokens.size() - 3).getType() != Type.OPERATOR)) {
      return;
    }

    tokens.remove(tokens.size() - 1); // remove lastToken
    tokens.remove(tokens.size() - 1); // remove previousToken
    if (operatorValue.length() > 1) {
      tokens.add(Token.of(
          Type.OPERATOR,
          operatorValue.substring(0, operatorValue.length() - 1),
          previousToken.getIndex()
      ));
    }
    tokens.add(Token.of(
        Type.NUMBER, "-" + ((NumberToken) lastToken).getValue(), lastToken.getIndex() - 1
    ));
  }

}
