package controller;

import calendar.view.GuiView;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the GuiView interface for testing purposes.
 */
public class MockGuiView implements GuiView {
  public int refreshCalendarCallCount = 0;
  public int showErrorCallCount = 0;
  public int showMessageCallCount = 0;
  public int updateCalendarListCallCount = 0;
  public int displayCallCount = 0;

  public List<String> messages = new ArrayList<>();
  public List<String> errors = new ArrayList<>();
  public List<String> lastCalendarList = new ArrayList<>();


  @Override
  public void refreshCalendar() {
    refreshCalendarCallCount++;
  }

  @Override
  public void showError(String message) {
    ++showErrorCallCount;
    errors.add(message);
  }

  @Override
  public void showMessage(String message) {
    ++showMessageCallCount;
    messages.add(message);
  }

  @Override
  public void updateCalendarList(List<String> allCalendarNames) {
    ++updateCalendarListCallCount;
    lastCalendarList = new ArrayList<>(allCalendarNames);
  }

  @Override
  public void display() {
    displayCallCount++;
  }

  /**
   * Resets all counters and stored values.
   */
  public void reset() {
    refreshCalendarCallCount = 0;
    showErrorCallCount = 0;
    showMessageCallCount = 0;
    updateCalendarListCallCount = 0;
    displayCallCount = 0;
    messages.clear();
    errors.clear();
    lastCalendarList.clear();
  }
}
