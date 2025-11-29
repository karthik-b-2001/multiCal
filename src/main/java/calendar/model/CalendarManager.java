package calendar.model;

import calendar.model.exceptions.DuplicateEventException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Interface for managing calendar operations.
 */
public interface CalendarManager {

  /**
   * Creates a new calendar with the specified name and time zone.
   *
   * @param name     the name of the calendar
   * @param timeZone the time zone of the calendar
   */
  void createCalendar(String name, ZoneId timeZone);

  /**
   * Sets the active calendar to the one with the specified name.
   *
   * @param name the name of the calendar to set as active
   */
  void useCalendar(String name);

  /**
   * Retrieves the currently active calendar.
   *
   * @return the active calendar
   */
  Calendar getActiveCalendar();

  /**
   * Edits a property of the specified calendar.
   *
   * @param name     the name of the calendar to edit
   * @param property the property to edit (e.g., "name", "timeZone")
   * @param newValue the new value for the property
   */
  void editCalendar(String name, String property, Object newValue);

  /**
   * Copies a specific event from the source date-time to the target calendar
   * at the target date-time.
   *
   * @param eventName          the name of the event to copy
   * @param sourceDateTime     the date-time of the event in the source calendar
   * @param targetCalendarName the name of the target calendar
   * @param targetDateTime     the date-time to place the copied event in the target calendar
   * @throws DuplicateEventException if an event with the same details already exists
   *                                 in the target calendar
   */
  void copyEvent(String eventName, LocalDateTime sourceDateTime, String targetCalendarName,
                 LocalDateTime targetDateTime) throws DuplicateEventException;


  /**
   * Copies all events occurring on a specific date to the target calendar
   * on the specified target date.
   *
   * @param sourceDate         the date of the events to copy from the source calendar
   * @param targetCalendarName the name of the target calendar
   * @param targetDate         the date to place the copied events in the target calendar
   * @throws DuplicateEventException if any event with the same details already exists
   *                                 in the target calendar
   */
  void copyEventsOnDate(LocalDate sourceDate, String targetCalendarName, LocalDate targetDate)
      throws DuplicateEventException;


  /**
   * Copies all events occurring between the specified start and end dates
   * to the target calendar starting from the specified target start date.
   *
   * @param startDate          the start date of the events to copy from the source calendar
   * @param endDate            the end date of the events to copy from the source calendar
   * @param targetCalendarName the name of the target calendar
   * @param targetStartDate    the date to start placing the copied events in the target calendar
   * @throws DuplicateEventException if any event with the same details already exists
   *                                 in the target calendar
   */
  void copyEventsBetween(LocalDate startDate, LocalDate endDate, String targetCalendarName,
                         LocalDate targetStartDate) throws DuplicateEventException;
}
