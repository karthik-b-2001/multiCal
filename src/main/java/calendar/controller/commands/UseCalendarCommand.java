package calendar.controller.commands;

import calendar.model.CalendarManager;
import calendar.view.View;

/**
 * Command to set the active calendar.
 */
public class UseCalendarCommand implements Command {

  private final String calendarName;

  /**
   * Creates a UseCalendarCommand.
   *
   * @param calendarName the name of the calendar to use
   */
  public UseCalendarCommand(String calendarName) {
    this.calendarName = calendarName;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    manager.useCalendar(calendarName);
    view.displayMessage("Now using calendar: " + calendarName);
  }
}