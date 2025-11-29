package calendar.controller.commands;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.EditSettings;
import calendar.view.View;
import java.time.LocalDateTime;

/**
 * Command to edit an existing calendar event.
 */
public class EditEventCommand implements Command {

  private final String subject;
  private final LocalDateTime startDateTime;
  private final String property;
  private final Object newValue;
  private final EditSettings scope;

  /**
   * Creates an EditEventCommand.
   *
   * @param subject the subject of the event to edit
   * @param startDateTime the start date-time of the event to edit
   * @param property the property to modify
   * @param newValue the new value for the property
   * @param scope the scope of the edit (SINGLE, FORWARD, or ALL_EVENTS)
   */
  public EditEventCommand(String subject, LocalDateTime startDateTime,
                          String property, Object newValue, EditSettings scope) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.property = property;
    this.newValue = newValue;
    this.scope = scope;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    Calendar calendar = manager.getActiveCalendar();
    if (calendar == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    calendar.editEvent(subject, startDateTime, property, newValue, scope);
    view.displayEventEdited(subject);
  }
}