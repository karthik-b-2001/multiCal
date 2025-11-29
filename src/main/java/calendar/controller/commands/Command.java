package calendar.controller.commands;

import calendar.model.CalendarManager;
import calendar.view.View;

/**
 * Represents a command that can be executed on a calendar manager.
 * Commands encapsulate the logic for a specific calendar operation.
 */
public interface Command {

  /**
   * Executes the command on the given calendar manager and displays results using the view.
   *
   * @param manager the calendar manager to operate on
   * @param view the view to display results
   * @throws Exception if the command execution fails
   */
  void execute(CalendarManager manager, View view) throws Exception;
}