package calendar.controller;

import calendar.controller.commands.Command;
import calendar.controller.utils.CommandParserImpl;
import calendar.model.CalendarManager;
import calendar.view.View;
import java.util.Scanner;

/**
 * Implementation of the Controller interface for the calendar application.
 * Handles command processing for both interactive and headless modes.
 */
public class CalendarController implements Controller {

  private final CalendarManager manager;
  private final View view;
  private final Scanner scanner;
  private final CommandParserImpl parser;
  private final boolean isInteractive;

  /**
   * Creates a CalendarController.
   *
   * @param manager the calendar manager to operate on
   * @param view the view for displaying output
   * @param scanner the scanner for reading input commands
   * @param isInteractive true if running in interactive mode, false for headless
   */
  public CalendarController(CalendarManager manager, View view, Scanner scanner,
                            boolean isInteractive) {
    this.manager = manager;
    this.view = view;
    this.scanner = scanner;
    this.parser = new CommandParserImpl();
    this.isInteractive = isInteractive;
  }

  @Override
  public void run() {
    if (isInteractive) {
      view.displayMessage("Calendar application started. Type 'exit' to quit.");
    }

    boolean shouldContinue = true;
    boolean exitCommandSeen = false;

    while (shouldContinue && scanner.hasNextLine()) {
      String commandLine = scanner.nextLine().trim();

      if (commandLine.isEmpty()) {
        continue;
      }

      if (commandLine.equals("exit")) {
        exitCommandSeen = true;
        shouldContinue = false;
        if (isInteractive) {
          view.displayMessage("Exiting calendar application.");
        }
        continue;
      }

      try {
        Command command = parser.parse(commandLine);
        command.execute(manager, view);

      } catch (IllegalArgumentException e) {
        view.displayError("Invalid command: " + e.getMessage());
      } catch (Exception e) {
        view.displayError("Error executing command: " + e.getMessage());
      }
    }

    if (!isInteractive && !exitCommandSeen) {
      view.displayError("Headless mode file must end with 'exit' command");
    }
  }
}