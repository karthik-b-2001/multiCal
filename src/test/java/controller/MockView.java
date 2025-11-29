package controller;

import calendar.model.Event;
import calendar.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of View for testing.
 * Captures all output for verification.
 */
public class MockView implements View {

  public int displayMessageCallCount = 0;
  public int displayErrorCallCount = 0;
  public int displayEventsCallCount = 0;
  public int displayEventsOnDateCallCount = 0;
  public int displayEventsInRangeCallCount = 0;
  public int displayBusyStatusCallCount = 0;
  public int displayEventCreatedCallCount = 0;
  public int displayEventEditedCallCount = 0;
  public int displayExportSuccessCallCount = 0;

  public List<String> messages = new ArrayList<>();
  public List<String> errors = new ArrayList<>();
  public String lastEventCreated;
  public String lastEventEdited;
  public String lastExportPath;
  public Boolean lastBusyStatus;

  @Override
  public void displayMessage(String message) {
    displayMessageCallCount++;
    messages.add(message);
  }

  @Override
  public void displayError(String error) {
    displayErrorCallCount++;
    errors.add(error);
  }

  @Override
  public void displayEvents(List<Event> events) {
    displayEventsCallCount++;
  }

  @Override
  public void displayEventsOnDate(List<Event> events, String date) {
    displayEventsOnDateCallCount++;
  }

  @Override
  public void displayEventsInRange(List<Event> events) {
    displayEventsInRangeCallCount++;
  }

  @Override
  public void displayBusyStatus(boolean isBusy) {
    displayBusyStatusCallCount++;
    lastBusyStatus = isBusy;
  }

  @Override
  public void displayEventCreated(String eventSubject) {
    displayEventCreatedCallCount++;
    lastEventCreated = eventSubject;
  }

  @Override
  public void displayEventEdited(String eventSubject) {
    displayEventEditedCallCount++;
    lastEventEdited = eventSubject;
  }

  @Override
  public void displayExportSuccess(String absolutePath) {
    displayExportSuccessCallCount++;
    lastExportPath = absolutePath;
  }

  /**
   * Resets all counters and stored values.
   */
  public void reset() {
    displayMessageCallCount = 0;
    displayErrorCallCount = 0;
    displayEventsCallCount = 0;
    displayEventsOnDateCallCount = 0;
    displayEventsInRangeCallCount = 0;
    displayBusyStatusCallCount = 0;
    displayEventCreatedCallCount = 0;
    displayEventEditedCallCount = 0;
    displayExportSuccessCallCount = 0;

    messages.clear();
    errors.clear();
    lastEventCreated = null;
    lastEventEdited = null;
    lastExportPath = null;
    lastBusyStatus = null;
  }
}