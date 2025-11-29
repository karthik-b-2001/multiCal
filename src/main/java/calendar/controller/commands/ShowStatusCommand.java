package calendar.controller.commands;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.view.View;
import java.time.LocalDateTime;

/**
 * Command to check if the user is busy at a specific date and time.
 */
public class ShowStatusCommand implements Command {

  private final LocalDateTime dateTime;

  /**
   * Creates a ShowStatusCommand for the specified date and time.
   *
   * @param dateTime the date and time to check
   */
  public ShowStatusCommand(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public void execute(CalendarManager manager, View view) {
    Calendar calendar = manager.getActiveCalendar();
    if (calendar == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    boolean isBusy = calendar.isBusy(dateTime);
    view.displayBusyStatus(isBusy);
  }
}