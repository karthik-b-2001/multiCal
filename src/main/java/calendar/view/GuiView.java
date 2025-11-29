package calendar.view;

import java.util.List;

/**
 * Interface representing the GUI view of the calendar application.
 */
public interface GuiView {

  /**
   * Refreshes the calendar display.
   */
  void refreshCalendar();

  /**
   * Displays an error message.
   *
   * @param message the error message to display
   */
  void showError(String message);

  /**
   * Displays a message to the user.
   *
   * @param message the message to display
   */

  void showMessage(String message);

  /**
   * Updates the list of calendars displayed in the GUI.
   */
  void updateCalendarList(List<String> allCalendarNames);

  /**
   * Displays the main calendar view.
   */
  void display();
}
