package calendar.controller.utils;

import calendar.controller.commands.Command;

/**
 * Interface for parsing command line strings into Command objects.
 */
public interface CommandParser {
  /**
   * Parses a command line string into a Command object.
   *
   * @param commandLine the command line string to parse
   * @return the corresponding Command object
   * @throws IllegalArgumentException if the command line is invalid
   */
  Command parse(String commandLine) throws IllegalArgumentException;
}
