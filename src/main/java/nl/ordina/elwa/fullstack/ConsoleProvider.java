package nl.ordina.elwa.fullstack;

import java.io.Console;
import lombok.experimental.UtilityClass;

@UtilityClass
final class ConsoleProvider {

  static Console getConsole() {
    return System.console();
  }

}
