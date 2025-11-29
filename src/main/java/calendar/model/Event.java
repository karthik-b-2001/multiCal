package calendar.model;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents an immutable calendar event.
 * Events have a subject, start and end date-times, and optional properties such as
 * description, location, and status. Events can be part of a recurring series.
 */
public interface Event {


  /**
   * Gets the subject (title) of the event.
   *
   * @return the subject of the event
   */
  String getSubject();

  /**
   * Gets the start date and time of the event.
   *
   * @return the start date-time of the event
   */

  LocalDateTime getStartDateTime();

  /**
   * Gets the end date and time of the event.
   *
   * @return the end date-time of the event
   */
  LocalDateTime getEndDateTime();

  /**
   * Gets the optional description of the event.
   *
   * @return an Optional containing the description if present, empty Optional otherwise
   */
  Optional<String> getDescription();

  /**
   * Gets the optional location of the event.
   *
   * @return an Optional containing the location if present, empty Optional otherwise
   */
  Optional<String> getLocation();

  /**
   * Gets the status of the event (PUBLIC or PRIVATE).
   *
   * @return the status of the event
   */
  EventStatus getStatus();

  /**
   * Checks if this is an all-day event.
   * All-day events run from 8:00 AM to 5:00 PM.
   *
   * @return true if this is an all-day event, false otherwise
   */
  boolean isAllDayEvent();

  /**
   * Checks if this event is part of a recurring series.
   *
   * @return true if this event belongs to a series, false otherwise
   */
  boolean isInSeries();

  /**
   * Gets the series ID if this event is part of a recurring series.
   *
   * @return an Optional containing the series ID if present, empty Optional otherwise
   */

  Optional<String> getSeriesId();
  /**
   * Creates a copy of this event with new start and end times.
   * All other properties are preserved.
   */

  Event copyWithNewTimes(LocalDateTime newStart, LocalDateTime newEnd);

  /**
   * Creates a copy of this event with a different series ID.
   */
  Event copyWithSeriesId(String newSeriesId);
}
