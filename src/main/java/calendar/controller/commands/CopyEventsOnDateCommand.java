package calendar.controller.commands;

import calendar.model.CalendarManager;
import calendar.view.View;
import java.time.LocalDate;

/**
 * Command to copy all events on a specific date to another calendar.
 */
public class CopyEventsOnDateCommand implements Command {

  private final LocalDate sourceDate;
  private final String targetCalendarName;
  private final LocalDate targetDate;

  /**
   * Creates a CopyEventsOnDateCommand.
   *
   * @param sourceDate the date to copy events from
   * @param targetCalendarName the name of the target calendar
   * @param targetDate the date to place events in target calendar
   */
  public CopyEventsOnDateCommand(LocalDate sourceDate, String targetCalendarName,
                                 LocalDate targetDate) {
    this.sourceDate = sourceDate;
    this.targetCalendarName = targetCalendarName;
    this.targetDate = targetDate;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    if (manager.getActiveCalendar() == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    manager.copyEventsOnDate(sourceDate, targetCalendarName, targetDate);
    view.displayMessage("Events copied successfully");
  }
}