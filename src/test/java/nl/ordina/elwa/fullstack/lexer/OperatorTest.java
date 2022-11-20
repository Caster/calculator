package nl.ordina.elwa.fullstack.lexer;

import static nl.ordina.elwa.fullstack.lexer.Operator.OPERATORS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OperatorTest {

  @Test
  void isOperator() {
    assertTrue(Operator.isOperatorCharacter('+'));
    assertTrue(Operator.isOperatorCharacter('√'));
    assertFalse(Operator.isOperatorCharacter('a'));
  }

  @Test
  void of() {
    assertEquals(OPERATORS.get(0), Operator.of("+", 0));
  }

  @Test
  void compareTo() {
    assertEquals("+", OPERATORS.get(0).getValue());
    assertEquals("-", OPERATORS.get(1).getValue());
    assertEquals("*", OPERATORS.get(2).getValue());
    assertEquals(1, OPERATORS.get(0).comparePriorityTo(OPERATORS.get(2)));
    assertEquals(0, OPERATORS.get(0).comparePriorityTo(OPERATORS.get(1)));
  }
}
