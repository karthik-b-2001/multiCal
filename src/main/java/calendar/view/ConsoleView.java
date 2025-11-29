package calendar.view;

import calendar.model.Event;
import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Console-based implementation of the View interface.
 * Displays calendar information to the console using standard output.
 */
public class ConsoleView implements View {

  private final PrintStream out;
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Creates a ConsoleView that writes to the given PrintStream.
   *
   * @param out the output stream to write to
   */
  public ConsoleView(PrintStream out) {
    this.out = out;
  }

  /**
   * Creates a ConsoleView that writes to System.out.
   */
  public ConsoleView() {
    this(System.out);
  }

  @Override
  public void displayMessage(String message) {
    out.println(message);
  }

  @Override
  public void displayError(String error) {
    out.println("Error: " + error);
  }

  @Override
  public void displayEvents(List<Event> events) {
    if (events.isEmpty()) {
      out.println("No events found.");
      return;
    }

    for (Event event : events) {
      out.println("- " + formatEvent(event));
    }
  }

  @Override
  public void displayEventsOnDate(List<Event> events, String date) {
    if (events.isEmpty()) {
      out.println("No events on " + date);
      return;
    }

    out.println("Events on " + date + ":");
    for (Event event : events) {
      out.println("- " + formatEventForDate(event));
    }
  }

  @Override
  public void displayEventsInRange(List<Event> events) {
    if (events.isEmpty()) {
      out.println("No events in the specified range.");
      return;
    }

    out.println("Events in range:");
    for (Event event : events) {
      out.println("- " + formatEventWithFullDetails(event));
    }
  }

  @Override
  public void displayBusyStatus(boolean isBusy) {
    out.println(isBusy ? "busy" : "available");
  }

  @Override
  public void displayEventCreated(String eventSubject) {
    out.println("Event created: " + eventSubject);
  }

  @Override
  public void displayEventEdited(String eventSubject) {
    out.println("Event edited: " + eventSubject);
  }

  @Override
  public void displayExportSuccess(String absolutePath) {
    out.println("Calendar exported successfully to: " + absolutePath);
  }

  /**
   * Formats an event for display on a specific date.
   * Format: Subject from startTime to endTime [at location]
   */
  private String formatEventForDate(Event event) {
    StringBuilder sb = new StringBuilder();
    sb.append(event.getSubject());
    sb.append(" from ");
    sb.append(event.getStartDateTime().format(TIME_FORMATTER));
    sb.append(" to ");
    sb.append(event.getEndDateTime().format(TIME_FORMATTER));

    if (event.getLocation().isPresent()) {
      sb.append(" at ");
      sb.append(event.getLocation().get());
    }

    return sb.toString();
  }

  /**
   * Formats an event with full date and time details.
   * Format: Subject starting on date at time, ending on date at time [at location]
   */
  private String formatEventWithFullDetails(Event event) {
    StringBuilder sb = new StringBuilder();
    sb.append(event.getSubject());
    sb.append(" starting on ");
    sb.append(event.getStartDateTime().format(DATE_FORMATTER));
    sb.append(" at ");
    sb.append(event.getStartDateTime().format(TIME_FORMATTER));
    sb.append(", ending on ");
    sb.append(event.getEndDateTime().format(DATE_FORMATTER));
    sb.append(" at ");
    sb.append(event.getEndDateTime().format(TIME_FORMATTER));

    if (event.getLocation().isPresent()) {
      sb.append(" at ");
      sb.append(event.getLocation().get());
    }

    return sb.toString();
  }

  /**
   * Simple event formatting.
   */
  private String formatEvent(Event event) {
    return event.getSubject() + " (" + event.getStartDateTime().format(DATE_FORMATTER) + ")";
  }
}