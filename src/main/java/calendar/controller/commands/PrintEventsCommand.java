package calendar.controller.commands;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.Event;
import calendar.view.View;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command to print events on a specific date or within a date range.
 */
public class PrintEventsCommand implements Command {

  private final LocalDate date;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final boolean isRangeQuery;

  /**
   * Creates a PrintEventsCommand for a specific date.
   *
   * @param date the date to query
   */
  public PrintEventsCommand(LocalDate date) {
    this.date = date;
    this.startDateTime = null;
    this.endDateTime = null;
    this.isRangeQuery = false;
  }

  /**
   * Creates a PrintEventsCommand for a date range.
   *
   * @param startDateTime the start of the range
   * @param endDateTime the end of the range
   */
  public PrintEventsCommand(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this.date = null;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isRangeQuery = true;
  }

  @Override
  public void execute(CalendarManager manager, View view) {
    Calendar calendar = manager.getActiveCalendar();
    if (calendar == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    if (isRangeQuery) {
      List<Event> events = calendar.getEventsInRange(startDateTime, endDateTime);
      view.displayEventsInRange(events);
    } else {
      List<Event> events = calendar.getEventOnDate(date);
      view.displayEventsOnDate(events, date.toString());
    }
  }
}