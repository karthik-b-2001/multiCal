package calendar.controller.commands;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.view.View;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Command to create calendar events (single or series).
 */
public class CreateEventCommand implements Command {

  private final String subject;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final boolean isAllDay;
  private final boolean isSeries;
  private final Set<DayOfWeek> weekdays;
  private final Integer occurrences;
  private final LocalDate seriesEndDate;

  /**
   * Creates a command for a single timed event.
   */
  public CreateEventCommand(String subject, LocalDateTime startDateTime,
                            LocalDateTime endDateTime) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isAllDay = false;
    this.isSeries = false;
    this.weekdays = null;
    this.occurrences = null;
    this.seriesEndDate = null;
  }

  /**
   * Creates a command for a single all-day event.
   */
  public CreateEventCommand(String subject, LocalDate date) {
    this.subject = subject;
    this.startDateTime = date.atTime(8, 0);
    this.endDateTime = null;
    this.isAllDay = true;
    this.isSeries = false;
    this.weekdays = null;
    this.occurrences = null;
    this.seriesEndDate = null;
  }

  /**
   * Creates a command for a timed event series with fixed occurrences.
   */
  public CreateEventCommand(String subject, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                            int occurrences) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isAllDay = false;
    this.isSeries = true;
    this.weekdays = weekdays;
    this.occurrences = occurrences;
    this.seriesEndDate = null;
  }

  /**
   * Creates a command for a timed event series until a date.
   */
  public CreateEventCommand(String subject, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                            LocalDate endDate) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isAllDay = false;
    this.isSeries = true;
    this.weekdays = weekdays;
    this.occurrences = null;
    this.seriesEndDate = endDate;
  }

  /**
   * Creates a command for an all-day event series with fixed occurrences.
   */
  public CreateEventCommand(String subject, LocalDate startDate,
                            Set<DayOfWeek> weekdays, int occurrences) {
    this.subject = subject;
    this.startDateTime = startDate.atTime(8, 0);
    this.endDateTime = null;
    this.isAllDay = true;
    this.isSeries = true;
    this.weekdays = weekdays;
    this.occurrences = occurrences;
    this.seriesEndDate = null;
  }

  /**
   * Creates a command for an all-day event series until a date.
   */
  public CreateEventCommand(String subject, LocalDate startDate,
                            Set<DayOfWeek> weekdays, LocalDate endDate) {
    this.subject = subject;
    this.startDateTime = startDate.atTime(8, 0);
    this.endDateTime = null;
    this.isAllDay = true;
    this.isSeries = true;
    this.weekdays = weekdays;
    this.occurrences = null;
    this.seriesEndDate = endDate;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    Calendar calendar = manager.getActiveCalendar();
    if (calendar == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    if (isSeries) {
      if (isAllDay) {
        if (occurrences != null) {
          calendar.createAllDayEventSeries(subject, startDateTime.toLocalDate(),
              weekdays, occurrences);
        } else {
          calendar.createAllDayEventSeriesTill(subject, startDateTime.toLocalDate(),
              weekdays, seriesEndDate);
        }
      } else {
        if (occurrences != null) {
          calendar.createEventSeries(subject, startDateTime.toLocalDate(),
              startDateTime.toLocalTime(),
              endDateTime.toLocalTime(),
              weekdays, occurrences);
        } else {
          calendar.createEventSeriesTill(subject, startDateTime.toLocalDate(),
              startDateTime.toLocalTime(),
              endDateTime.toLocalTime(),
              weekdays, seriesEndDate);
        }
      }
    } else {
      LocalDateTime finalEndDateTime =
          endDateTime != null ? endDateTime : startDateTime.withHour(17);
      calendar.createAndAddEvent(subject, startDateTime, finalEndDateTime, isAllDay);
    }

    view.displayEventCreated(subject);
  }
}