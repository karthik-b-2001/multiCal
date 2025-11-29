package calendar.controller.commands;

import calendar.model.CalendarManager;
import calendar.view.View;
import java.time.ZoneId;

/**
 * Command to create a new calendar.
 */
public class CreateCalendarCommand implements Command {

  private final String name;
  private final ZoneId timeZone;

  /**
   * Creates a CreateCalendarCommand.
   *
   * @param name the name of the new calendar
   * @param timeZone the time zone of the new calendar
   */
  public CreateCalendarCommand(String name, ZoneId timeZone) {
    this.name = name;
    this.timeZone = timeZone;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    manager.createCalendar(name, timeZone);
    view.displayMessage("Calendar created: " + name);
  }
}