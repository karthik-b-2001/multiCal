package controller;

import calendar.model.Calendar;
import calendar.model.EditSettings;
import calendar.model.Event;
import calendar.model.EventStatus;
import calendar.model.exceptions.DuplicateEventException;
import calendar.model.exceptions.EventNotFoundException;
import calendar.model.exceptions.UnclearEventException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Mock implementation of Calendar for testing.
 * Tracks method calls and allows configurable behavior.
 */
public class MockCalendar implements Calendar {

  public int addEventCallCount = 0;
  public int createEventSeriesCallCount = 0;
  public int createEventSeriesTillCallCount = 0;
  public int createAllDayEventSeriesCallCount = 0;
  public int createAllDayEventSeriesTillCallCount = 0;
  public int editEventCallCount = 0;
  public int getEventOnDateCallCount = 0;
  public int getEventsInRangeCallCount = 0;
  public int isBusyCallCount = 0;
  public int getAllEventsCallCount = 0;
  public int createAndAddEventCallCount = 0;

  public Event lastAddedEvent;
  public String lastEditedSubject;
  public LocalDateTime lastEditedStartDateTime;
  public String lastEditedProperty;
  public Object lastEditedNewValue;
  public EditSettings lastEditScope;
  public Set<DayOfWeek> lastWeekdaysUsed;
  public LocalTime lastStartTime;
  public LocalTime lastEndTime;

  public boolean shouldThrowDuplicateException = false;
  public boolean shouldThrowNotFoundException = false;
  public boolean shouldThrowUnclearException = false;
  public boolean isBusyReturnValue = false;
  public List<Event> eventsToReturn = new ArrayList<>();

  @Override
  public void addEvent(Event event) throws DuplicateEventException {
    addEventCallCount++;
    lastAddedEvent = event;
    if (shouldThrowDuplicateException) {
      throw new DuplicateEventException("Mock duplicate event");
    }
    if (event != null) {
      eventsToReturn.add(event);
    }
  }

  @Override
  public void createEventSeries(String subject, LocalDate startDate,
                                LocalTime startTime, LocalTime endTime,
                                Set<DayOfWeek> weekdays, int occurrences)
      throws DuplicateEventException {
    createEventSeriesCallCount++;
    lastWeekdaysUsed = weekdays;
    lastStartTime = startTime;
    lastEndTime = endTime;
    if (shouldThrowDuplicateException) {
      throw new DuplicateEventException("Mock duplicate event");
    }
  }

  @Override
  public void createEventSeriesTill(String subject, LocalDate startDate,
                                    LocalTime startTime, LocalTime endTime,
                                    Set<DayOfWeek> weekDays, LocalDate endDate)
      throws DuplicateEventException {
    createEventSeriesTillCallCount++;
    lastWeekdaysUsed = weekDays;
    lastStartTime = startTime;
    lastEndTime = endTime;
    if (shouldThrowDuplicateException) {
      throw new DuplicateEventException("Mock duplicate event");
    }
  }

  @Override
  public void createAllDayEventSeries(String subject, LocalDate startDate,
                                      Set<DayOfWeek> weekDays, int occurrences)
      throws DuplicateEventException {
    createAllDayEventSeriesCallCount++;
    lastWeekdaysUsed = weekDays;
    if (shouldThrowDuplicateException) {
      throw new DuplicateEventException("Mock duplicate event");
    }
  }

  @Override
  public void createAllDayEventSeriesTill(String subject, LocalDate startDate,
                                          Set<DayOfWeek> weekDays, LocalDate endDate)
      throws DuplicateEventException {
    createAllDayEventSeriesTillCallCount++;
    lastWeekdaysUsed = weekDays;
    if (shouldThrowDuplicateException) {
      throw new DuplicateEventException("Mock duplicate event");
    }
  }

  @Override
  public void editEvent(String subject, LocalDateTime startDateTime, String property,
                        Object newValue, EditSettings scope)
      throws EventNotFoundException, UnclearEventException, DuplicateEventException {
    editEventCallCount++;
    lastEditedSubject = subject;
    lastEditedStartDateTime = startDateTime;
    lastEditedProperty = property;
    lastEditedNewValue = newValue;
    lastEditScope = scope;

    if (shouldThrowNotFoundException) {
      throw new EventNotFoundException("Mock event not found");
    }
    if (shouldThrowUnclearException) {
      throw new UnclearEventException("Mock unclear event");
    }
    if (shouldThrowDuplicateException) {
      throw new DuplicateEventException("Mock duplicate event");
    }
  }

  @Override
  public List<Event> getEventOnDate(LocalDate date) {
    getEventOnDateCallCount++;
    return eventsToReturn;
  }

  @Override
  public List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    getEventsInRangeCallCount++;
    return eventsToReturn;
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    isBusyCallCount++;
    return isBusyReturnValue;
  }

  @Override
  public List<Event> getAllEvents() {
    getAllEventsCallCount++;
    return eventsToReturn;
  }

  @Override
  public String getCalendarName() {
    return "MockCalendar";
  }

  @Override
  public void setCalendarName(String name) {
  }

  @Override
  public ZoneId getTimeZone() {
    return ZoneId.of("America/New_York");
  }

  @Override
  public void setTimeZone(ZoneId zoneId) {
  }

  @Override
  public Event findEvent(String subject, LocalDateTime startDateTime)
      throws EventNotFoundException, UnclearEventException {
    List<Event> matching = new ArrayList<>();
    for (Event e : eventsToReturn) {
      if (e.getSubject().equals(subject) && e.getStartDateTime().equals(startDateTime)) {
        matching.add(e);
      }
    }

    if (matching.isEmpty()) {
      throw new EventNotFoundException("Event not found");
    }
    if (matching.size() > 1) {
      throw new UnclearEventException("Multiple events found");
    }

    return matching.get(0);
  }

  @Override
  public void createAndAddEvent(String subject, LocalDateTime startDateTime,
                                LocalDateTime endDateTime, boolean isAllDay)
      throws DuplicateEventException {
    createAndAddEventCallCount++;
    addEventCallCount++;

    MockEvent mockEvent = new MockEvent(subject, startDateTime, endDateTime, isAllDay);
    lastAddedEvent = mockEvent;
    eventsToReturn.add(mockEvent);

    if (shouldThrowDuplicateException) {
      throw new DuplicateEventException("Mock duplicate event");
    }
  }


  /**
   * A simple mock Event implementation for testing.
   */
  private static class MockEvent implements Event {
    private final String subject;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final boolean isAllDay;
    public String description;
    public String location;
    public EventStatus status = EventStatus.PUBLIC;

    /**
     * Constructor for MockEvent.
     *
     * @param subject  mock subject
     * @param start    mock start
     * @param end      mock end
     * @param isAllDay mock is all day
     */
    MockEvent(String subject, LocalDateTime start, LocalDateTime end, boolean isAllDay) {
      this.subject = subject;
      this.startDateTime = start;
      this.endDateTime = end;
      this.isAllDay = isAllDay;
    }

    @Override
    public String getSubject() {
      return subject;
    }

    @Override
    public LocalDateTime getStartDateTime() {
      return startDateTime;
    }

    @Override
    public LocalDateTime getEndDateTime() {
      return endDateTime;
    }

    @Override
    public Optional<String> getDescription() {
      return Optional.ofNullable(description);
    }

    @Override
    public Optional<String> getLocation() {
      return Optional.ofNullable(location);
    }

    @Override
    public EventStatus getStatus() {
      return status;
    }

    @Override
    public boolean isAllDayEvent() {
      return isAllDay;
    }

    @Override
    public boolean isInSeries() {
      return false;
    }

    @Override
    public Optional<String> getSeriesId() {
      return Optional.empty();
    }

    @Override
    public Event copyWithNewTimes(LocalDateTime newStart, LocalDateTime newEnd) {
      return new MockEvent(subject, newStart, newEnd, isAllDay);
    }

    @Override
    public Event copyWithSeriesId(String newSeriesId) {
      return null;
    }

  }

  /**
   * Resets all counters and stored values.
   */
  protected void reset() {
    addEventCallCount = 0;
    createEventSeriesCallCount = 0;
    createEventSeriesTillCallCount = 0;
    createAllDayEventSeriesCallCount = 0;
    createAllDayEventSeriesTillCallCount = 0;
    editEventCallCount = 0;
    getEventOnDateCallCount = 0;
    getEventsInRangeCallCount = 0;
    isBusyCallCount = 0;
    getAllEventsCallCount = 0;
    createAndAddEventCallCount = 0;

    lastAddedEvent = null;
    lastEditedSubject = null;
    lastEditedStartDateTime = null;
    lastEditedProperty = null;
    lastEditedNewValue = null;
    lastEditScope = null;
    lastWeekdaysUsed = null;
    lastStartTime = null;
    lastEndTime = null;

    shouldThrowDuplicateException = false;
    shouldThrowNotFoundException = false;
    shouldThrowUnclearException = false;
    isBusyReturnValue = false;
    eventsToReturn.clear();
  }
}