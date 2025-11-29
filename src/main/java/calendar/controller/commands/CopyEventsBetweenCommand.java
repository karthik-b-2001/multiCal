package calendar.controller.commands;

import calendar.model.CalendarManager;
import calendar.view.View;
import java.time.LocalDate;

/**
 * Command to copy events between two dates to another calendar.
 */
public class CopyEventsBetweenCommand implements Command {

  private final LocalDate startDate;
  private final LocalDate endDate;
  private final String targetCalendarName;
  private final LocalDate targetStartDate;

  /**
   * Creates a CopyEventsBetweenCommand.
   *
   * @param startDate the start date of the range to copy
   * @param endDate the end date of the range to copy
   * @param targetCalendarName the name of the target calendar
   * @param targetStartDate the start date in the target calendar
   */
  public CopyEventsBetweenCommand(LocalDate startDate, LocalDate endDate,
                                  String targetCalendarName, LocalDate targetStartDate) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.targetCalendarName = targetCalendarName;
    this.targetStartDate = targetStartDate;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    if (manager.getActiveCalendar() == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    manager.copyEventsBetween(startDate, endDate, targetCalendarName, targetStartDate);
    view.displayMessage("Events copied successfully");
  }
}