package calendar.controller.commands;

import calendar.model.CalendarManager;
import calendar.view.View;

/**
 * Command to edit calendar properties.
 */
public class EditCalendarCommand implements Command {

  private final String calendarName;
  private final String property;
  private final Object newValue;

  /**
   * Creates an EditCalendarCommand.
   *
   * @param calendarName the name of the calendar to edit
   * @param property the property to edit (name or timezone)
   * @param newValue the new value for the property
   */
  public EditCalendarCommand(String calendarName, String property, Object newValue) {
    this.calendarName = calendarName;
    this.property = property;
    this.newValue = newValue;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    manager.editCalendar(calendarName, property, newValue);
    view.displayMessage("Calendar edited: " + calendarName);
  }
}