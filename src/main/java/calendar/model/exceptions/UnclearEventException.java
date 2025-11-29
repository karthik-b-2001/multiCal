package calendar.model.exceptions;

/**
 * Exception thrown when an event's details are unclear or ambiguous.
 * This occurs when multiple events match the search criteria, making it
 * impossible to uniquely identify which event to edit.
 */
public class UnclearEventException extends Exception {
  /**
   * Constructs a new UnclearEventException with the specified detail message.
   *
   * @param message the detail message
   */
  public UnclearEventException(String message) {
    super(message);
  }
}