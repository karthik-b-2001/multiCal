package calendar.controller.commands;

import calendar.model.CalendarManager;
import calendar.view.View;
import java.time.LocalDateTime;

/**
 * Command to copy a single event to another calendar.
 */
public class CopyEventCommand implements Command {

  private final String eventName;
  private final LocalDateTime sourceDateTime;
  private final String targetCalendarName;
  private final LocalDateTime targetDateTime;

  /**
   * Creates a CopyEventCommand.
   *
   * @param eventName the name of the event to copy
   * @param sourceDateTime the start date-time of the event in source calendar
   * @param targetCalendarName the name of the target calendar
   * @param targetDateTime the date-time to place the event in target calendar
   */
  public CopyEventCommand(String eventName, LocalDateTime sourceDateTime,
                          String targetCalendarName, LocalDateTime targetDateTime) {
    this.eventName = eventName;
    this.sourceDateTime = sourceDateTime;
    this.targetCalendarName = targetCalendarName;
    this.targetDateTime = targetDateTime;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    if (manager.getActiveCalendar() == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    manager.copyEvent(eventName, sourceDateTime, targetCalendarName, targetDateTime);
    view.displayMessage("Event copied successfully");
  }
}