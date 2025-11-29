package calendar.controller;

import calendar.model.Calendar;
import calendar.model.EditSettings;
import calendar.model.Event;
import calendar.view.GuiView;
import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * Interface defining the controller for the GUI calendar application.
 */
public interface GuiController {

  /**
   * Sets the GUI view for the controller.
   *
   * @param view the GUI view to set
   */
  void setView(GuiView view);

  /**
   * Creates a new calendar with the specified name and timezone.
   *
   * @param name     the name of the calendar
   * @param timezone the timezone of the calendar
   */
  void createCalendar(String name, ZoneId timezone);

  /**
   * Switches the active calendar to the specified calendar name.
   *
   * @param calendarName the name of the calendar to switch to
   */
  void switchCalendar(String calendarName);

  /**
   * Creates a single event with the specified details.
   *
   * @param subject   the subject of the event
   * @param date      the date of the event
   * @param startTime the start time of the event
   * @param endTime   the end time of the event
   * @param isAllDay  whether the event is an all-day event
   */
  void createSingleEvent(String subject, LocalDate date, LocalTime startTime, LocalTime endTime,
                         boolean isAllDay);

  /**
   * Creates a recurring event with the specified details.
   *
   * @param subject     the subject of the event
   * @param startDate   the start date of the event
   * @param startTime   the start time of the event
   * @param endTime     the end time of the event
   * @param weekdays    the days of the week on which the event recurs
   * @param occurrences the number of occurrences (nullable if using endDate)
   * @param endDate     the end date of the recurrence (nullable if using occurrences)
   * @param isAllDay    whether the event is an all-day event
   */
  void createRecurringEvent(String subject, LocalDate startDate, LocalTime startTime,
                            LocalTime endTime, Set<DayOfWeek> weekdays, Integer occurrences,
                            LocalDate endDate, boolean isAllDay);

  /**
   * Edits an existing event's property.
   *
   * @param subject       the subject of the event
   * @param startDateTime the start date and time of the event
   * @param property      the property to edit
   * @param newValue      the new value for the property
   * @param scope         the scope of the edit (e.g., instance, global)
   *
   */
  void editEvent(String subject, LocalDateTime startDateTime, String property, Object newValue,
                 EditSettings scope);

  /**
   * Retrieves the list of events for a specific date.
   *
   * @param date the date for which to retrieve events
   * @return the list of events on the specified date
   */
  List<Event> getEventsForDate(LocalDate date);

  /**
   * Retrieves all calendar names.
   *
   * @return the list of all calendar names
   */
  List<String> getAllCalendarNames();

  /**
   * Retrieves the color associated with a specific calendar.
   *
   * @param calendarName the name of the calendar
   * @return the color of the specified calendar
   */
  Color getCalendarColor(String calendarName);

  /**
   * Retrieves the name of the currently active calendar.
   *
   * @return the name of the active calendar
   */
  String getActiveCalendarName();

  /**
   * Retrieves the current timezone of the application.
   *
   * @return the current ZoneId
   */
  ZoneId getCurrentTimezone();

  /**
   * Retrieves the currently active calendar.
   *
   * @return the active Calendar object
   */
  Calendar getActiveCalendar();
}
