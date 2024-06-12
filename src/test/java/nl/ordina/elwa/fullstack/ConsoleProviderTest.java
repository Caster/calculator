package nl.ordina.elwa.fullstack;

import static org.junit.jupiter.api.Assertions.assertSame;

import lombok.val;
import org.junit.jupiter.api.Test;

class ConsoleProviderTest {

  @Test
  void canGetSystemConsole() {
    val console = ConsoleProvider.getConsole();

    assertSame(System.console(), console);
  }

}
