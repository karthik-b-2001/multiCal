package calendar.view;

import calendar.model.Event;
import java.util.List;

/**
 * Interface for displaying calendar information to the user.
 * Handles all output operations for the calendar application.
 */
public interface View {

  /**
   * Displays a general message to the user.
   *
   * @param message the message to display
   */
  void displayMessage(String message);

  /**
   * Displays an error message to the user.
   *
   * @param error the error message to display
   */
  void displayError(String error);

  /**
   * Displays a list of events.
   *
   * @param events the list of events to display
   */
  void displayEvents(List<Event> events);

  /**
   * Displays events for a specific date with formatting.
   *
   * @param events the list of events to display
   * @param date the date string for the header
   */
  void displayEventsOnDate(List<Event> events, String date);

  /**
   * Displays events within a date range with formatting.
   *
   * @param events the list of events to display
   */
  void displayEventsInRange(List<Event> events);

  /**
   * Displays the busy status for a given date-time.
   *
   * @param isBusy true if busy, false if available
   */
  void displayBusyStatus(boolean isBusy);

  /**
   * Displays a success message for event creation.
   *
   * @param eventSubject the subject of the created event
   */
  void displayEventCreated(String eventSubject);

  /**
   * Displays a success message for event editing.
   *
   * @param eventSubject the subject of the edited event
   */
  void displayEventEdited(String eventSubject);

  /**
   * Displays information about the exported CSV file.
   *
   * @param absolutePath the absolute path of the exported file
   */
  void displayExportSuccess(String absolutePath);
}