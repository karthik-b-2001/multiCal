package calendar.model.exceptions;

/**
 * Exception thrown when attempting to add a duplicate event to the calendar.
 * A duplicate event is defined as an event with the same subject, start date-time,
 * and end date-time as an existing event in the calendar.
 */
public class DuplicateEventException extends Exception {
  /**
   * Constructs a new DuplicateEventException with the specified detail message.
   *
   * @param message the detail message
   */
  public DuplicateEventException(String message) {
    super(message);
  }
}