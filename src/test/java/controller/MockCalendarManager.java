package controller;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.exceptions.DuplicateEventException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mock implementation of CalendarManager for testing.
 */
public class MockCalendarManager implements CalendarManager {

  public MockCalendar mockCalendar;
  public int createCalendarCallCount = 0;
  public int useCalendarCallCount = 0;
  public int editCalendarCallCount = 0;
  public int copyEventCallCount = 0;
  public int copyEventsOnDateCallCount = 0;
  public int copyEventsBetweenCallCount = 0;

  /**
   * Default constructor initializing the mock calendar manager.
   */
  public MockCalendarManager() {
    this.mockCalendar = new MockCalendar();
  }

  @Override
  public void createCalendar(String name, ZoneId timeZone) {
    createCalendarCallCount++;
  }

  @Override
  public void useCalendar(String name) {
    useCalendarCallCount++;
  }

  @Override
  public Calendar getActiveCalendar() {
    return mockCalendar;
  }

  @Override
  public void editCalendar(String name, String property, Object newValue) {
    editCalendarCallCount++;
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime sourceDateTime,
                        String targetCalendarName, LocalDateTime targetDateTime)
      throws DuplicateEventException {
    copyEventCallCount++;
  }

  @Override
  public void copyEventsOnDate(LocalDate sourceDate, String targetCalendarName,
                               LocalDate targetDate) throws DuplicateEventException {
    copyEventsOnDateCallCount++;
  }

  @Override
  public void copyEventsBetween(LocalDate startDate, LocalDate endDate,
                                String targetCalendarName, LocalDate targetStartDate)
      throws DuplicateEventException {
    copyEventsBetweenCallCount++;
  }

  public MockCalendar getMockCalendar() {
    return mockCalendar;
  }

  /**
   * Resets all call counts and the mock calendar state.
   */
  public void reset() {
    createCalendarCallCount = 0;
    useCalendarCallCount = 0;
    editCalendarCallCount = 0;
    copyEventCallCount = 0;
    copyEventsOnDateCallCount = 0;
    copyEventsBetweenCallCount = 0;
    mockCalendar.reset();
  }
}