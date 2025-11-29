package calendar.model.exceptions;

/**
 * Exception thrown when an event is not found in the calendar.
 */
public class EventNotFoundException extends Exception {
  /**
   * Constructs a new EventNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  public EventNotFoundException(String message) {
    super(message);
  }
}
