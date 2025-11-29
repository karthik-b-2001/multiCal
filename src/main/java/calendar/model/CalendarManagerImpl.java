package calendar.model;

import calendar.model.exceptions.DuplicateEventException;
import calendar.model.exceptions.EventNotFoundException;
import calendar.model.exceptions.UnclearEventException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of CalendarManager interface for managing calendar operations.
 */
public class CalendarManagerImpl implements CalendarManager {

  private final Map<String, Calendar> calendarMap;
  private Calendar activeCalendar;
  private int seriesCount;

  /**
   * Default constructor initializing the calendar manager with an empty map
   * and no active calendar.
   */
  public CalendarManagerImpl() {
    this.calendarMap = new HashMap<>();
    this.activeCalendar = null;
    seriesCount = 0;
  }

  @Override
  public void createCalendar(String name, ZoneId timeZone) {
    checkCalendarDuplicate(name);

    Calendar newCalendar = new CalendarImpl(name, timeZone);
    calendarMap.put(name, newCalendar);
  }

  @Override
  public void useCalendar(String name) {
    checkCalendarExists(name);
    this.activeCalendar = calendarMap.get(name);
  }

  @Override
  public Calendar getActiveCalendar() {
    return this.activeCalendar;
  }

  @Override
  public void editCalendar(String name, String property, Object newValue) {
    Calendar calendar = getCalendarByName(name);

    if (property.equalsIgnoreCase("name")) {
      String newName = (String) newValue;
      checkCalendarDuplicate(newName);
      calendarMap.remove(name);
      calendar.setCalendarName(newName);
      calendarMap.put(newName, calendar);
    } else if (property.equalsIgnoreCase("timezone")) {
      if (!(newValue instanceof ZoneId)) {
        throw new IllegalArgumentException("Invalid value for timeZone property.");
      }
      calendar.setTimeZone((ZoneId) newValue);
    } else {
      throw new IllegalArgumentException("Invalid property: " + property);
    }
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime sourceDateTime, String targetCalendarName,
                        LocalDateTime targetDateTime) throws DuplicateEventException {

    Calendar targetCalendar = getCalendarByName(targetCalendarName);
    Event eventToCopy;
    try {
      eventToCopy = activeCalendar.findEvent(eventName, sourceDateTime);
    } catch (EventNotFoundException | UnclearEventException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

    long durationInSeconds =
        Duration.between(eventToCopy.getStartDateTime(), eventToCopy.getEndDateTime()).getSeconds();

    LocalDateTime newEndDateTime = targetDateTime.plusSeconds(durationInSeconds);

    Event copiedEvent = eventToCopy.copyWithNewTimes(targetDateTime, newEndDateTime);

    boolean isDifferentCalendar = !activeCalendar.equals(targetCalendar);
    if (isDifferentCalendar && copiedEvent.isInSeries()) {
      copiedEvent = copiedEvent.copyWithSeriesId(null);
    }
    targetCalendar.addEvent(copiedEvent);
  }

  @Override
  public void copyEventsOnDate(LocalDate sourceDate, String targetCalendarName,
                               LocalDate targetDate) throws DuplicateEventException {
    checkActiveCalendar();

    Calendar targetCalendar = getCalendarByName(targetCalendarName);
    List<Event> eventsOnDate = activeCalendar.getEventOnDate(sourceDate);

    Map<String, String> seriesIdMap = new HashMap<>();
    boolean isDifferentCalendar = !activeCalendar.equals(targetCalendar);

    for (Event event : eventsOnDate) {

      LocalDateTime convertedStart =
          convertTime(event.getStartDateTime(), activeCalendar, targetCalendar);
      LocalDateTime convertedEnd =
          convertTime(event.getEndDateTime(), activeCalendar, targetCalendar);

      LocalDateTime newStart = LocalDateTime.of(targetDate, convertedStart.toLocalTime());
      LocalDateTime newEnd = LocalDateTime.of(targetDate, convertedEnd.toLocalTime());

      Event copiedEvent = event.copyWithNewTimes(newStart, newEnd);
      if (isDifferentCalendar && copiedEvent.isInSeries()) {
        String oldSeriesId = copiedEvent.getSeriesId().get();

        String newSeriesId =
            seriesIdMap.computeIfAbsent(oldSeriesId, k -> generateUniqueSeriesId());

        copiedEvent = copiedEvent.copyWithSeriesId(newSeriesId);

      }
      targetCalendar.addEvent(copiedEvent);
    }
  }

  @Override
  public void copyEventsBetween(LocalDate startDate, LocalDate endDate, String targetCalendarName,
                                LocalDate targetStartDate) throws DuplicateEventException {
    checkActiveCalendar();

    Calendar targetCalendar = getCalendarByName(targetCalendarName);

    LocalDateTime rangeStart = startDate.atStartOfDay();
    LocalDateTime rangeEnd = endDate.plusDays(1).atStartOfDay();

    List<Event> eventsInRange = activeCalendar.getEventsInRange(rangeStart, rangeEnd);

    Map<String, String> seriesIdMapping = new HashMap<>();
    boolean isDifferentCalendar = !activeCalendar.equals(targetCalendar);

    for (Event event : eventsInRange) {
      LocalDate sourceEventDate = event.getStartDateTime().toLocalDate();
      DayOfWeek eventDayOfWeek = sourceEventDate.getDayOfWeek();

      long weeksSinceStart = ChronoUnit.WEEKS.between(
          startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
          sourceEventDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

      LocalDate targetEventDate = targetStartDate.with(TemporalAdjusters.nextOrSame(eventDayOfWeek))
          .plusWeeks(weeksSinceStart);

      LocalDateTime convertedStartTime =
          convertTime(event.getStartDateTime(), activeCalendar, targetCalendar);
      LocalDateTime convertedEndTime =
          convertTime(event.getEndDateTime(), activeCalendar, targetCalendar);

      LocalDateTime newStart = LocalDateTime.of(targetEventDate, convertedStartTime.toLocalTime());
      LocalDateTime newEnd = LocalDateTime.of(targetEventDate, convertedEndTime.toLocalTime());

      Event copiedEvent = event.copyWithNewTimes(newStart, newEnd);

      if (isDifferentCalendar && copiedEvent.isInSeries()) {
        String oldSeriesId = copiedEvent.getSeriesId().get();

        String newSeriesId =
            seriesIdMapping.computeIfAbsent(oldSeriesId, k -> generateUniqueSeriesId());

        copiedEvent = copiedEvent.copyWithSeriesId(newSeriesId);
      }

      targetCalendar.addEvent(copiedEvent);
    }
  }

  private void checkCalendarDuplicate(String name) throws IllegalArgumentException {
    if (calendarMap.containsKey(name)) {
      throw new IllegalArgumentException("Calendar with name " + name + " already exists.");
    }
  }

  private void checkCalendarExists(String name) throws IllegalArgumentException {
    if (!calendarMap.containsKey(name)) {
      throw new IllegalArgumentException("Calendar with name " + name + " does not exist.");
    }
  }

  private Calendar getCalendarByName(String name) {
    checkCalendarExists(name);
    return calendarMap.get(name);
  }

  private void checkActiveCalendar() {
    if (activeCalendar == null) {
      throw new IllegalStateException("No active calendar selected.");
    }
  }

  private LocalDateTime convertTime(LocalDateTime source, Calendar sourceCalendar,
                                    Calendar targetCalendar) {


    if (sourceCalendar.getTimeZone().equals(targetCalendar.getTimeZone())) {
      return source;
    }

    ZonedDateTime sourceZone = ZonedDateTime.of(source, sourceCalendar.getTimeZone());
    ZonedDateTime targetZone = sourceZone.withZoneSameInstant(targetCalendar.getTimeZone());

    return targetZone.toLocalDateTime();
  }

  private String generateUniqueSeriesId() {
    return "SID_COPY_" + System.currentTimeMillis() + "_" + (++seriesCount);
  }
}
