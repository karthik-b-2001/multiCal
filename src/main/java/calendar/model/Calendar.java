package calendar.model;

import calendar.model.exceptions.DuplicateEventException;
import calendar.model.exceptions.EventNotFoundException;
import calendar.model.exceptions.UnclearEventException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * Interface representing a calendar that manages events.
 * Provides functionality to create, edit, and query calendar events,
 * including support for single events and recurring event series.
 */
public interface Calendar {

  /**
   * Adds an event to the calendar.
   *
   * @param event the event to be added
   * @throws IllegalArgumentException if the event is null
   * @throws DuplicateEventException  if an event with the same subject,
   *                                  start date-time, and end date-time
   *                                  already exists in the calendar
   */
  void addEvent(Event event) throws IllegalArgumentException,
      DuplicateEventException;

  /**
   * Creates a series of recurring events on specified weekdays for a
   * given number of occurrences.
   *
   * @param subject     the subject of the event series
   * @param startDate   the start date of the first event in the series
   * @param startTime   the start time of each event in the series
   * @param endTime     the end time of each event in the series
   * @param weekdays    the set of weekdays on which the events should occur
   * @param occurrences the total number of occurrences in the series
   * @throws IllegalArgumentException if any argument is invalid (e.g., null
   *                                  values, endTime before startTime)
   * @throws DuplicateEventException  if any event in the series conflicts
   *                                  with an existing event in the calendar
   */
  void createEventSeries(String subject,
                         LocalDate startDate,
                         LocalTime startTime,
                         LocalTime endTime,
                         Set<DayOfWeek> weekdays,
                         int occurrences)
      throws IllegalArgumentException, DuplicateEventException;

  /**
   * Creates a series of recurring events on specified weekdays until a
   * given end date.
   *
   * @param subject   the subject of the event series
   * @param startDate the start date of the first event in the series
   * @param startTime the start time of each event in the series
   * @param endTime   the end time of each event in the series
   * @param weekDays  the set of weekdays on which the events should occur
   * @param endDate   the end date until which events should be created
   *                  (inclusive)
   * @throws IllegalArgumentException if any argument is invalid (e.g., null
   *                                  values, endTime before startTime)
   * @throws DuplicateEventException  if any event in the series conflicts
   *                                  with an existing event in the calendar
   */
  void createEventSeriesTill(String subject, LocalDate startDate,
                             LocalTime startTime, LocalTime endTime,
                             Set<DayOfWeek> weekDays,
                             LocalDate endDate)
      throws IllegalArgumentException, DuplicateEventException;

  /**
   * Creates a series of all-day recurring events on specified weekdays
   * for a given number of occurrences.
   *
   * @param subject     the subject of the event series
   * @param startDate   the start date of the first event in the series
   * @param weekDays    the set of weekdays on which the events should occur
   * @param occurrences the total number of occurrences in the series
   * @throws IllegalArgumentException if any argument is invalid (e.g., null
   *                                  values)
   * @throws DuplicateEventException  if any event in the series conflicts
   *                                  with an existing event in the calendar
   */
  void createAllDayEventSeries(String subject, LocalDate startDate,
                               Set<DayOfWeek> weekDays, int occurrences)
      throws IllegalArgumentException, DuplicateEventException;


  /**
   * Creates a series of all-day recurring events on specified weekdays
   * until a given end date.
   *
   * @param subject   the subject of the event series
   * @param startDate the start date of the first event in the series
   * @param weekDays  the set of weekdays on which the events should occur
   * @param endDate   the end date until which events should be created
   *                  (inclusive)
   * @throws IllegalArgumentException if any argument is invalid (e.g., null
   *                                  values)
   * @throws DuplicateEventException  if any event in the series conflicts
   *                                  with an existing event in the calendar
   */
  void createAllDayEventSeriesTill(String subject, LocalDate startDate,
                                   Set<DayOfWeek> weekDays,
                                   LocalDate endDate)
      throws IllegalArgumentException, DuplicateEventException;


  /**
   * Edits an existing event identified by its subject and start date-time.
   *
   * @param subject       the subject of the event to be edited
   * @param startDateTime the start date-time of the event to be edited
   * @param property      the property of the event to be edited (e.g.,
   *                      "subject", "start", "end", "description",
   *                      "location", "status")
   * @param newValue      the new value for the specified property
   * @param scope         the scope of the edit (SINGLE, FORWARD, or
   *                      ALL_EVENTS)
   * @throws EventNotFoundException  if no event matching the subject and
   *                                 start date-time is found
   * @throws UnclearEventException   if multiple events match the subject
   *                                 and start date-time
   * @throws DuplicateEventException if the edit would result in a duplicate
   *                                 event in the calendar
   */
  void editEvent(String subject, LocalDateTime startDateTime, String property,
                 Object newValue, EditSettings scope) throws
      EventNotFoundException, UnclearEventException, DuplicateEventException;

  /**
   * Retrieves all events occurring on a specific date.
   * An event occurs on a date if the date falls within the event's start
   * and end date range.
   *
   * @param date the date to query
   * @return an unmodifiable list of events on the given date, sorted by
   *         start time, empty list if no events found
   */
  List<Event> getEventOnDate(LocalDate date);

  /**
   * Retrieves all events that overlap with a given date-time range.
   * Events overlap if they start before the range ends and end after the
   * range starts.
   *
   * @param start the start of the date-time range
   * @param end the end of the date-time range
   * @return an unmodifiable list of events in the given range, sorted by
   *         start time, empty list if no events found
   */
  List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end);

  /**
   * Checks if the user is busy at a specific date and time.
   * The user is considered busy if there is any event occurring at the
   * given time.
   *
   * @param dateTime the date and time to check
   * @return true if there is an event at the given date-time, false
   *         otherwise
   */
  boolean isBusy(LocalDateTime dateTime);

  /**
   * Retrieves all events in the calendar.
   *
   * @return an unmodifiable list of all events in the calendar
   */
  List<Event> getAllEvents();

  /**
   * Gets the name of the calendar.
   *
   * @return the name of the calendar
   */
  String getCalendarName();

  /**
   * Sets the name of the calendar.
   *
   * @param name the new name of the calendar
   * @throws IllegalArgumentException if the name is null or empty
   */
  void setCalendarName(String name);

  /**
   * Gets the time zone of the calendar.
   *
   * @return the time zone of the calendar
   */
  ZoneId getTimeZone();

  /**
   * Sets the time zone of the calendar.
   *
   * @param zoneId the new time zone of the calendar
   * @throws IllegalArgumentException if the zoneId is null
   */
  void setTimeZone(ZoneId zoneId);

  /**
   * Finds a specific event by subject and start date-time.
   *
   * @param subject       the subject of the event
   * @param startDateTime the start date-time of the event
   * @return the event if found
   * @throws EventNotFoundException if no event matches the criteria
   * @throws UnclearEventException  if multiple events match
   */
  Event findEvent(String subject, LocalDateTime startDateTime)
      throws EventNotFoundException, UnclearEventException;

  /**
   * Creates and adds a single event to the calendar.
   *
   * @param subject       the event subject
   * @param startDateTime the start date-time
   * @param endDateTime   the end date-time
   * @param isAllDay      whether this is an all-day event
   * @throws DuplicateEventException if event already exists
   */
  void createAndAddEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                         boolean isAllDay) throws DuplicateEventException;


}