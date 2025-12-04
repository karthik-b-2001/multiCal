package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import calendar.model.Calendar;
import calendar.model.CalendarImpl;
import calendar.model.EditSettings;
import calendar.model.Event;
import calendar.model.EventStatus;
import calendar.model.LocationType;
import calendar.model.exceptions.DuplicateEventException;
import calendar.model.exceptions.EventNotFoundException;
import calendar.model.exceptions.UnclearEventException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CalendarImpl class.
 * Tests cover adding events, creating event series, querying events,
 * editing events, and Assignment 5 features (name, timezone, findEvent).
 */
public class CalendarImplTest {
  private Calendar calendar;

  /**
   * Sets up a new CalendarImpl instance before each test with default name and timezone.
   */
  @Before
  public void setUp() {
    calendar = new CalendarImpl("TestCalendar", ZoneId.of("America/New_York"));
  }


  @Test
  public void testGetName() {
    assertEquals("TestCalendar", calendar.getCalendarName());
  }

  @Test
  public void testSetName() {
    calendar.setCalendarName("UpdatedCalendar");
    assertEquals("UpdatedCalendar", calendar.getCalendarName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameNull() {
    calendar.setCalendarName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameEmpty() {
    calendar.setCalendarName("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameWhitespace() {
    calendar.setCalendarName("   ");
  }

  @Test
  public void testGetTimezone() {
    assertEquals(ZoneId.of("America/New_York"), calendar.getTimeZone());
  }

  @Test
  public void testSetTimezone() {
    calendar.setTimeZone(ZoneId.of("Europe/Paris"));
    assertEquals(ZoneId.of("Europe/Paris"), calendar.getTimeZone());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTimezoneNull() {
    calendar.setTimeZone(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullName() {
    new CalendarImpl(null, ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorEmptyName() {
    new CalendarImpl("", ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullTimezone() {
    new CalendarImpl("TestCalendar", null);
  }

  @Test
  public void testFindEventSuccess() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event found = calendar.findEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0));
    assertNotNull(found);
    assertEquals("Meeting", found.getSubject());
  }

  @Test(expected = EventNotFoundException.class)
  public void testFindEventNotFound() throws Exception {
    calendar.findEvent("Nonexistent", LocalDateTime.of(2025, 5, 5, 10, 0));
  }

  @Test(expected = UnclearEventException.class)
  public void testFindEventAmbiguous() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 12, 0), false);

    calendar.findEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0));
  }

  @Test
  public void testCreateAndAddEventBasic() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    assertEquals(1, calendar.getAllEvents().size());
  }

  @Test
  public void testCreateAndAddEventAllDay() throws Exception {
    calendar.createAndAddEvent("Holiday", LocalDateTime.of(2025, 5, 5, 8, 0),
        LocalDateTime.of(2025, 5, 5, 17, 0), true);

    List<Event> events = calendar.getAllEvents();
    assertEquals(1, events.size());
    assertTrue(events.get(0).isAllDayEvent());
  }

  @Test(expected = DuplicateEventException.class)
  public void testCreateAndAddEventDuplicate() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
  }


  @Test
  public void testAddSingleEvent() throws DuplicateEventException {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    assertEquals(1, calendar.getAllEvents().size());
  }

  @Test(expected = DuplicateEventException.class)
  public void testAddDuplicateEvent() throws DuplicateEventException {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
  }

  @Test
  public void testAddMultipleDifferentEvents() throws DuplicateEventException {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Lunch", LocalDateTime.of(2025, 5, 5, 12, 0),
        LocalDateTime.of(2025, 5, 5, 13, 0), false);

    assertEquals(2, calendar.getAllEvents().size());
  }

  @Test
  public void testCreateEventSeriesBasic() throws Exception {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);

    calendar.createEventSeries("Standup", LocalDate.of(2025, 5, 5), LocalTime.of(9, 0),
        LocalTime.of(9, 15), weekdays, 4);

    assertEquals(4, calendar.getAllEvents().size());
  }

  @Test
  public void testCreateEventSeriesAllHaveSameSeriesId() throws Exception {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, 3);

    List<Event> events = calendar.getAllEvents();
    String seriesId = events.get(0).getSeriesId().get();

    assertTrue(events.stream()
        .allMatch(e -> e.getSeriesId().isPresent() && e.getSeriesId().get().equals(seriesId)));
  }

  @Test
  public void testCreateEventSeriesCorrectDates() throws Exception {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, 3);

    List<Event> events = calendar.getAllEvents();
    assertEquals(3, events.size());

    assertTrue(events.stream()
        .anyMatch(e -> e.getStartDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 5))));
    assertTrue(events.stream()
        .anyMatch(e -> e.getStartDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 12))));
    assertTrue(events.stream()
        .anyMatch(e -> e.getStartDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 19))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesZeroOccurrences() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesNegativeOccurrences() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesEndBeforeStart() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(11, 0),
        LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesEmptyWeekdays() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(), 3);
  }

  @Test
  public void testCreateEventSeriesTillBasic() throws Exception {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.FRIDAY);

    calendar.createEventSeriesTill("Review", LocalDate.of(2025, 5, 2), LocalTime.of(16, 0),
        LocalTime.of(17, 0), weekdays, LocalDate.of(2025, 5, 23));

    assertEquals(4, calendar.getAllEvents().size());
  }

  @Test
  public void testCreateEventSeriesTillIncludesEndDate() throws Exception {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    calendar.createEventSeriesTill("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, LocalDate.of(2025, 5, 19));

    List<Event> events = calendar.getAllEvents();
    assertTrue(events.stream()
        .anyMatch(e -> e.getStartDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 19))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesTillEndBeforeStart() throws Exception {
    calendar.createEventSeriesTill("Meeting", LocalDate.of(2025, 5, 20), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 5, 10));
  }

  @Test
  public void testCreateAllDayEventSeries() throws Exception {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

    calendar.createAllDayEventSeries("Gym", LocalDate.of(2025, 5, 5), weekdays, 6);

    List<Event> events = calendar.getAllEvents();
    assertEquals(6, events.size());
    assertTrue(events.stream().allMatch(Event::isAllDayEvent));
    assertTrue(events.stream().allMatch(e -> e.getStartDateTime().getHour() == 8));
    assertTrue(events.stream().allMatch(e -> e.getEndDateTime().getHour() == 17));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateAllDayEventSeriesZeroOccurrences() throws Exception {
    calendar.createAllDayEventSeries("Holiday", LocalDate.of(2025, 5, 5), Set.of(DayOfWeek.MONDAY),
        0);
  }

  @Test
  public void testGetEventsOnDate() throws Exception {
    calendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0), false);

    calendar.createAndAddEvent("Event3", LocalDateTime.of(2025, 5, 6, 10, 0),
        LocalDateTime.of(2025, 5, 6, 11, 0), false);

    List<Event> eventsOnMay5 = calendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    assertEquals(2, eventsOnMay5.size());
  }

  @Test
  public void testCreateAllDayEventSeriesTill() throws Exception {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    calendar.createAllDayEventSeriesTill("Holiday", LocalDate.of(2025, 5, 5), weekdays,
        LocalDate.of(2025, 5, 19));

    List<Event> events = calendar.getAllEvents();
    assertEquals(3, events.size());
    assertTrue(events.stream().allMatch(Event::isAllDayEvent));
  }

  @Test
  public void testGetEventsOnDateEmpty() {
    List<Event> events = calendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    assertEquals(0, events.size());
  }

  @Test
  public void testGetEventsOnDateSorted() throws Exception {
    calendar.createAndAddEvent("Late", LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0), false);

    calendar.createAndAddEvent("Early", LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 5, 10, 0), false);

    List<Event> events = calendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    assertEquals("Early", events.get(0).getSubject());
    assertEquals("Late", events.get(1).getSubject());
  }

  @Test
  public void testGetEventsInRange() throws Exception {
    calendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 10, 10, 0),
        LocalDateTime.of(2025, 5, 10, 11, 0), false);

    calendar.createAndAddEvent("Event3", LocalDateTime.of(2025, 5, 20, 10, 0),
        LocalDateTime.of(2025, 5, 20, 11, 0), false);

    List<Event> events = calendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 15, 23, 59));

    assertEquals(2, events.size());
  }

  @Test
  public void testGetEventsInRangeOverlapping() throws Exception {
    calendar.createAndAddEvent("MultiDay", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 7, 11, 0), false);

    List<Event> events = calendar.getEventsInRange(LocalDateTime.of(2025, 5, 6, 0, 0),
        LocalDateTime.of(2025, 5, 6, 23, 59));

    assertEquals(1, events.size());
  }

  @Test
  public void testIsBusyTrue() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    assertTrue(calendar.isBusy(LocalDateTime.of(2025, 5, 5, 10, 0)));
    assertTrue(calendar.isBusy(LocalDateTime.of(2025, 5, 5, 10, 30)));
  }

  @Test
  public void testIsBusyFalse() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    assertFalse(calendar.isBusy(LocalDateTime.of(2025, 5, 5, 9, 0)));
    assertFalse(calendar.isBusy(LocalDateTime.of(2025, 5, 5, 11, 0)));
  }

  @Test
  public void testEditEventSingleScope() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated Meeting",
        EditSettings.SINGLE);

    List<Event> events = calendar.getAllEvents();
    assertEquals(1, events.size());
    assertEquals("Updated Meeting", events.get(0).getSubject());
  }

  @Test
  public void testEditEventSeriesForward() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 4);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 7, 10, 0), "subject", "Updated",
        EditSettings.FORWARD);

    List<Event> events = calendar.getAllEvents();
    long updatedCount = events.stream().filter(e -> e.getSubject().equals("Updated")).count();

    assertEquals(3, updatedCount);
  }

  @Test(expected = EventNotFoundException.class)
  public void testEditEventNotFound() throws Exception {
    calendar.editEvent("Nonexistent", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "New",
        EditSettings.SINGLE);
  }

  @Test
  public void testEditEventSeriesAllEvents() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 12, 10, 0), "subject", "Updated",
        EditSettings.ALL_EVENTS);

    List<Event> events = calendar.getAllEvents();
    assertTrue(events.stream().allMatch(e -> e.getSubject().equals("Updated")));
  }

  @Test
  public void testEditTimeCreatesNewSeries() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    String originalSeriesId = calendar.getAllEvents().get(0).getSeriesId().get();

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 12, 10, 0), "start",
        LocalDateTime.of(2025, 5, 12, 10, 30), EditSettings.FORWARD);

    List<Event> events = calendar.getAllEvents();
    Event may5Event =
        events.stream().filter(e -> e.getStartDateTime().getDayOfMonth() == 5).findFirst().get();
    Event may12Event =
        events.stream().filter(e -> e.getStartDateTime().getDayOfMonth() == 12).findFirst().get();

    assertEquals(originalSeriesId, may5Event.getSeriesId().get());
    assertNotEquals(originalSeriesId, may12Event.getSeriesId().get());
  }

  @Test
  public void testEditTimePreservesDate() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(14, 0),
        LocalTime.of(15, 0), Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 4);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 7, 14, 0), "start",
        LocalDateTime.of(2025, 5, 7, 14, 30), EditSettings.FORWARD);

    List<Event> events = calendar.getAllEvents();
    Event may7 = events.stream()
        .filter(e -> e.getStartDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 7)))
        .findFirst().get();
    Event may12 = events.stream()
        .filter(e -> e.getStartDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 12)))
        .findFirst().get();
    Event may14 = events.stream()
        .filter(e -> e.getStartDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 14)))
        .findFirst().get();

    assertEquals(LocalDate.of(2025, 5, 7), may7.getStartDateTime().toLocalDate());
    assertEquals(LocalDate.of(2025, 5, 12), may12.getStartDateTime().toLocalDate());
    assertEquals(LocalDate.of(2025, 5, 14), may14.getStartDateTime().toLocalDate());
    assertEquals(LocalTime.of(14, 30), may7.getStartDateTime().toLocalTime());
    assertEquals(LocalTime.of(14, 30), may12.getStartDateTime().toLocalTime());
    assertEquals(LocalTime.of(14, 30), may14.getStartDateTime().toLocalTime());
  }

  @Test
  public void testGetAllEventsReturnsUnmodifiableList() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = calendar.getAllEvents();

    try {
      calendar.createAndAddEvent("Sneaky", LocalDateTime.of(2025, 5, 6, 10, 0),
          LocalDateTime.of(2025, 5, 6, 11, 0), false);

      events.clear();
      fail("Should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // Expected exception
    }
  }

  @Test
  public void testMultipleSeriesHaveUniqueIds() throws Exception {
    calendar.createEventSeries("Series1", LocalDate.of(2025, 5, 5), LocalTime.of(9, 0),
        LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), 2);

    calendar.createEventSeries("Series2", LocalDate.of(2025, 5, 6), LocalTime.of(9, 0),
        LocalTime.of(10, 0), Set.of(DayOfWeek.TUESDAY), 2);

    List<Event> events = calendar.getAllEvents();
    Event series1Event =
        events.stream().filter(e -> e.getSubject().equals("Series1")).findFirst().get();
    Event series2Event =
        events.stream().filter(e -> e.getSubject().equals("Series2")).findFirst().get();

    assertNotEquals(series1Event.getSeriesId().get(), series2Event.getSeriesId().get());
  }

  @Test(expected = UnclearEventException.class)
  public void testEditCreatesDuplicate() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Lunch", LocalDateTime.of(2025, 5, 5, 12, 0),
        LocalDateTime.of(2025, 5, 5, 13, 0), false);

    calendar.editEvent("Lunch", LocalDateTime.of(2025, 5, 5, 12, 0), "subject", "Meeting",
        EditSettings.SINGLE);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 12, 0), "start",
        LocalDateTime.of(2025, 5, 5, 10, 0), EditSettings.SINGLE);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        "end", LocalDateTime.of(2025, 5, 5, 11, 0), EditSettings.SINGLE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesTillEmptyWeekdays() throws Exception {
    calendar.createEventSeriesTill("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(), LocalDate.of(2025, 5, 19));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesTillTimeEndBeforeStart() throws Exception {
    calendar.createEventSeriesTill("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(11, 0),
        LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 5, 19));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventSeriesTillTimeEndEqualsStart() throws Exception {
    calendar.createEventSeriesTill("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 5, 19));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateAllDayEventSeriesEmptyWeekdays() throws Exception {
    calendar.createAllDayEventSeries("Holiday", LocalDate.of(2025, 5, 5), Set.of(), 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateAllDayEventSeriesNegativeOccurrences() throws Exception {
    calendar.createAllDayEventSeries("Holiday", LocalDate.of(2025, 5, 5), Set.of(DayOfWeek.MONDAY),
        -5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateAllDayEventSeriesTillEndBeforeStart() throws Exception {
    calendar.createAllDayEventSeriesTill("Holiday", LocalDate.of(2025, 5, 20),
        Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 5, 10));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateAllDayEventSeriesTillEmptyWeekdays() throws Exception {
    calendar.createAllDayEventSeriesTill("Holiday", LocalDate.of(2025, 5, 5), Set.of(),
        LocalDate.of(2025, 5, 19));
  }

  @Test(expected = UnclearEventException.class)
  public void testEditEventAmbiguous() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 12, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "New",
        EditSettings.SINGLE);
  }

  @Test
  public void testEditSingleNonSeriesSubject() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.SINGLE);

    assertEquals(1, calendar.getAllEvents().size());
    assertEquals("Updated", calendar.getAllEvents().get(0).getSubject());
  }

  @Test
  public void testEditSingleNonSeriesStart() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "start",
        LocalDateTime.of(2025, 5, 5, 9, 30), EditSettings.SINGLE);

    assertEquals(LocalTime.of(9, 30),
        calendar.getAllEvents().get(0).getStartDateTime().toLocalTime());
  }

  @Test
  public void testEditSingleSeriesEventStart() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "start",
        LocalDateTime.of(2025, 5, 5, 9, 30), EditSettings.SINGLE);

    List<Event> events = calendar.getAllEvents();
    Event edited =
        events.stream().filter(e -> e.getStartDateTime().toLocalTime().equals(LocalTime.of(9, 30)))
            .findFirst().get();

    assertFalse(edited.isInSeries());
    assertEquals(3, events.size());
  }

  @Test
  public void testEditForwardNonSeries() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.FORWARD);

    assertEquals("Updated", calendar.getAllEvents().get(0).getSubject());
  }

  @Test
  public void testEditForwardSeriesNonTimeProperty() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 12, 10, 0), "subject", "Updated",
        EditSettings.FORWARD);

    List<Event> events = calendar.getAllEvents();
    long meetingCount = events.stream().filter(e -> e.getSubject().equals("Meeting")).count();
    long updatedCount = events.stream().filter(e -> e.getSubject().equals("Updated")).count();

    assertEquals(1, meetingCount);
    assertEquals(2, updatedCount);
  }

  @Test
  public void testEditForwardSeriesTimeProperty() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 12, 10, 0), "start",
        LocalDateTime.of(2025, 5, 12, 9, 30), EditSettings.FORWARD);

    List<Event> events = calendar.getAllEvents();
    long newTimeCount =
        events.stream().filter(e -> e.getStartDateTime().toLocalTime().equals(LocalTime.of(9, 30)))
            .count();

    assertEquals(2, newTimeCount);
  }

  @Test
  public void testEditAllEventsSeries() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.ALL_EVENTS);

    assertTrue(calendar.getAllEvents().stream().allMatch(e -> e.getSubject().equals("Updated")));
  }

  @Test
  public void testEditEventWithDescription() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description",
        "Old description", EditSettings.SINGLE);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.SINGLE);

    assertEquals("Old description", calendar.getAllEvents().get(0).getDescription().get());
  }

  @Test
  public void testEditEventWithLocation() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        LocationType.PHYSICAL, EditSettings.SINGLE);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.SINGLE);

    assertEquals(LocationType.PHYSICAL, calendar.getAllEvents().get(0).getLocation());
  }

  @Test
  public void testEditSeriesEventPreservesSeriesId() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 2);

    String seriesId = calendar.getAllEvents().get(0).getSeriesId().get();

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.SINGLE);

    Event edited =
        calendar.getAllEvents().stream().filter(e -> e.getSubject().equals("Updated")).findFirst()
            .get();

    assertEquals(seriesId, edited.getSeriesId().get());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidProperty() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "invalid", "value",
        EditSettings.SINGLE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditStatusWithInvalidType() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "status", "INVALID_STRING",
        EditSettings.SINGLE);
  }

  @Test
  public void testEditSingleEventEnd() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "end",
        LocalDateTime.of(2025, 5, 5, 11, 30), EditSettings.SINGLE);

    assertEquals(LocalTime.of(11, 30),
        calendar.getAllEvents().get(0).getEndDateTime().toLocalTime());
  }

  @Test
  public void testEditSingleEventDescription() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description", "New desc",
        EditSettings.SINGLE);

    assertEquals("New desc", calendar.getAllEvents().get(0).getDescription().get());
  }

  @Test
  public void testEditSingleEventLocation() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        LocationType.ONLINE, EditSettings.SINGLE);

    assertEquals(LocationType.ONLINE, calendar.getAllEvents().get(0).getLocation());
  }


  @Test
  public void testEditSingleEventStatus() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "status",
        EventStatus.PRIVATE, EditSettings.SINGLE);

    assertEquals(EventStatus.PRIVATE, calendar.getAllEvents().get(0).getStatus());
  }

  @Test
  public void testOverlapOnStartDate() throws Exception {
    calendar.createAndAddEvent("MultiDay", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 10, 11, 0), false);

    List<Event> events = calendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    assertEquals(1, events.size());
  }

  @Test
  public void testOverlapOnEndDate() throws Exception {
    calendar.createAndAddEvent("MultiDay", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 10, 11, 0), false);

    List<Event> events = calendar.getEventOnDate(LocalDate.of(2025, 5, 10));
    assertEquals(1, events.size());
  }

  @Test
  public void testOverlapOnMiddleDate() throws Exception {
    calendar.createAndAddEvent("MultiDay", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 10, 11, 0), false);

    List<Event> events = calendar.getEventOnDate(LocalDate.of(2025, 5, 7));
    assertEquals(1, events.size());
  }

  @Test
  public void testNoOverlapBeforeStart() throws Exception {
    calendar.createAndAddEvent("Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = calendar.getEventOnDate(LocalDate.of(2025, 5, 4));
    assertEquals(0, events.size());
  }

  @Test
  public void testNoOverlapAfterEnd() throws Exception {
    calendar.createAndAddEvent("Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = calendar.getEventOnDate(LocalDate.of(2025, 5, 6));
    assertEquals(0, events.size());
  }

  @Test
  public void testGetEventsInRangeFullOverlap() throws Exception {
    calendar.createAndAddEvent("Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = calendar.getEventsInRange(LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 5, 12, 0));

    assertEquals(1, events.size());
  }

  @Test
  public void testGetEventsInRangePartialOverlapStart() throws Exception {
    calendar.createAndAddEvent("Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 12, 0), false);

    List<Event> events = calendar.getEventsInRange(LocalDateTime.of(2025, 5, 5, 11, 0),
        LocalDateTime.of(2025, 5, 5, 13, 0));

    assertEquals(1, events.size());
  }

  @Test
  public void testGetEventsInRangePartialOverlapEnd() throws Exception {
    calendar.createAndAddEvent("Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 12, 0), false);

    List<Event> events = calendar.getEventsInRange(LocalDateTime.of(2025, 5, 5, 8, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0));

    assertEquals(1, events.size());
  }

  @Test
  public void testGetEventsInRangeNoOverlapBefore() throws Exception {
    calendar.createAndAddEvent("Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = calendar.getEventsInRange(LocalDateTime.of(2025, 5, 5, 8, 0),
        LocalDateTime.of(2025, 5, 5, 9, 0));

    assertEquals(0, events.size());
  }

  @Test
  public void testGetEventsInRangeNoOverlapAfter() throws Exception {
    calendar.createAndAddEvent("Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = calendar.getEventsInRange(LocalDateTime.of(2025, 5, 5, 12, 0),
        LocalDateTime.of(2025, 5, 5, 13, 0));

    assertEquals(0, events.size());
  }

  @Test
  public void testEditForwardSeriesEndTime() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 12, 10, 0), "end",
        LocalDateTime.of(2025, 5, 12, 11, 30), EditSettings.FORWARD);

    List<Event> events = calendar.getAllEvents();
    Event may12 =
        events.stream().filter(e -> e.getStartDateTime().getDayOfMonth() == 12).findFirst().get();

    assertEquals(LocalTime.of(11, 30), may12.getEndDateTime().toLocalTime());
  }

  @Test
  public void testEditAllEventsNonSeriesEvent() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.ALL_EVENTS);

    assertEquals("Updated", calendar.getAllEvents().get(0).getSubject());
    assertEquals(1, calendar.getAllEvents().size());
  }

  @Test
  public void testMultipleSeriesIncrementCounter() throws Exception {
    calendar.createEventSeries("Series1", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    calendar.createEventSeries("Series2", LocalDate.of(2025, 5, 6), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.TUESDAY), 1);

    calendar.createEventSeries("Series3", LocalDate.of(2025, 5, 7), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.WEDNESDAY), 1);

    List<Event> events = calendar.getAllEvents();
    Set<String> seriesIds =
        events.stream().map(e -> e.getSeriesId().get()).collect(Collectors.toSet());

    assertEquals(3, seriesIds.size());
    assertTrue(seriesIds.contains("SID_1"));
    assertTrue(seriesIds.contains("SID_2"));
    assertTrue(seriesIds.contains("SID_3"));
  }

  @Test
  public void testCreateAllDayEventSeriesSkipsNonMatchingDays() throws Exception {
    calendar.createAllDayEventSeries("Gym", LocalDate.of(2025, 5, 5), Set.of(DayOfWeek.FRIDAY), 2);

    List<Event> events = calendar.getAllEvents();
    assertEquals(2, events.size());
    assertTrue(
        events.stream().allMatch(e -> e.getStartDateTime().getDayOfWeek() == DayOfWeek.FRIDAY));
  }

  @Test(expected = DuplicateEventException.class)
  public void testEditAllEventsDuplicateDetection() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 2);

    calendar.createAndAddEvent("Existing", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Existing",
        EditSettings.ALL_EVENTS);
  }

  @Test
  public void testSeriesIdIncrementsCorrectly() throws Exception {
    calendar.createEventSeries("Series1", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    calendar.createAllDayEventSeries("Series2", LocalDate.of(2025, 5, 6),
        Set.of(DayOfWeek.TUESDAY), 1);

    List<Event> events = calendar.getAllEvents();
    Event event1 = events.stream().filter(e -> e.getSubject().equals("Series1")).findFirst().get();
    Event event2 = events.stream().filter(e -> e.getSubject().equals("Series2")).findFirst().get();

    assertEquals("SID_1", event1.getSeriesId().get());
    assertEquals("SID_2", event2.getSeriesId().get());
  }

  @Test
  public void testGetEventsInSeriesExcludesNonSeriesEvents() throws Exception {
    calendar.createEventSeries("Series", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 2);

    calendar.createAndAddEvent("Single", LocalDateTime.of(2025, 5, 6, 10, 0),
        LocalDateTime.of(2025, 5, 6, 11, 0), false);

    assertEquals(3, calendar.getAllEvents().size());

    calendar.editEvent("Series", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.ALL_EVENTS);

    long updatedCount = calendar.getAllEvents().stream()
        .filter(e -> e.getSubject().equals("Updated"))
        .count();

    assertEquals(2, updatedCount);
  }

  @Test
  public void testEditForwardSeriesEnd() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 3);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 12, 10, 0), "end",
        LocalDateTime.of(2025, 5, 12, 11, 30), EditSettings.FORWARD);

    Event may5 = calendar.getAllEvents().stream()
        .filter(e -> e.getStartDateTime().getDayOfMonth() == 5)
        .findFirst().get();
    Event may12 = calendar.getAllEvents().stream()
        .filter(e -> e.getStartDateTime().getDayOfMonth() == 12)
        .findFirst().get();

    assertEquals(LocalTime.of(11, 0), may5.getEndDateTime().toLocalTime());
    assertEquals(LocalTime.of(11, 30), may12.getEndDateTime().toLocalTime());
    assertNotEquals(may5.getSeriesId().get(), may12.getSeriesId().get());
  }

  @Test
  public void testEditWithAllThreeScopes() throws Exception {
    calendar.createAndAddEvent("Single", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createEventSeries("Forward", LocalDate.of(2025, 5, 6), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.TUESDAY), 2);

    calendar.createEventSeries("All", LocalDate.of(2025, 5, 7), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.WEDNESDAY), 2);

    calendar.editEvent("Single", LocalDateTime.of(2025, 5, 5, 10, 0), "description", "Desc1",
        EditSettings.SINGLE);

    calendar.editEvent("Forward", LocalDateTime.of(2025, 5, 6, 10, 0), "description", "Desc2",
        EditSettings.FORWARD);

    calendar.editEvent("All", LocalDateTime.of(2025, 5, 7, 10, 0), "description", "Desc3",
        EditSettings.ALL_EVENTS);

    assertEquals(5, calendar.getAllEvents().size());
  }

  @Test
  public void testAddEventGeneric() throws Exception {
    calendar.createAndAddEvent("Temp", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event = calendar.getAllEvents().get(0);

    Calendar calendar2 = new CalendarImpl("Other", ZoneId.of("America/New_York"));
    calendar2.addEvent(event);

    assertEquals(1, calendar2.getAllEvents().size());
    assertEquals("Temp", calendar2.getAllEvents().get(0).getSubject());
  }

  @Test(expected = DuplicateEventException.class)
  public void testAddEventGenericDuplicate() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event = calendar.getAllEvents().get(0);

    calendar.addEvent(event);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddEventNull() throws Exception {
    calendar.addEvent(null);
  }

  @Test
  public void testSetTimeZoneConvertsEventTimes() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.setTimeZone(ZoneId.of("Europe/London"));

    Event event = calendar.getAllEvents().get(0);
    assertEquals(LocalTime.of(15, 0), event.getStartDateTime().toLocalTime());
    assertEquals(LocalTime.of(16, 0), event.getEndDateTime().toLocalTime());
  }

  @Test
  public void testSetTimeZonePreservesEventCount() throws Exception {
    calendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 6, 14, 0),
        LocalDateTime.of(2025, 5, 6, 15, 0), false);

    calendar.setTimeZone(ZoneId.of("Asia/Tokyo"));

    assertEquals(2, calendar.getAllEvents().size());
  }

  @Test
  public void testSetTimeZoneMultipleEvents() throws Exception {
    calendar.createEventSeries("Series", LocalDate.of(2025, 5, 5), LocalTime.of(9, 0),
        LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), 2);

    calendar.setTimeZone(ZoneId.of("America/Los_Angeles"));

    List<Event> events = calendar.getAllEvents();
    assertTrue(events.stream()
        .allMatch(e -> e.getStartDateTime().toLocalTime().equals(LocalTime.of(6, 0))));
  }

  @Test
  public void testSetTimeZonePreservesEventProperties() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description", "Test desc",
        EditSettings.SINGLE);

    calendar.setTimeZone(ZoneId.of("Europe/Paris"));

    Event event = calendar.getAllEvents().get(0);
    assertEquals("Test desc", event.getDescription().get());
    assertEquals("Meeting", event.getSubject());
  }

  @Test
  public void testSetTimeZonePreservesSeriesId() throws Exception {
    calendar.createEventSeries("Series", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 2);

    String originalSeriesId = calendar.getAllEvents().get(0).getSeriesId().get();

    calendar.setTimeZone(ZoneId.of("Asia/Kolkata"));

    List<Event> events = calendar.getAllEvents();
    assertTrue(events.stream().allMatch(
        e -> e.getSeriesId().isPresent() && e.getSeriesId().get().equals(originalSeriesId)));
  }

  @Test
  public void testSetTimeZoneAcrossDayBoundary() throws Exception {
    calendar.createAndAddEvent("Late", LocalDateTime.of(2025, 5, 5, 23, 0),
        LocalDateTime.of(2025, 5, 6, 0, 30), false);

    calendar.setTimeZone(ZoneId.of("Asia/Tokyo"));

    Event event = calendar.getAllEvents().get(0);
    assertEquals(LocalDate.of(2025, 5, 6), event.getStartDateTime().toLocalDate());
  }

  @Test
  public void testSetTimeZoneAllDayEvent() throws Exception {
    calendar.createAndAddEvent("AllDay", LocalDateTime.of(2025, 5, 5, 8, 0),
        LocalDateTime.of(2025, 5, 5, 17, 0), true);

    calendar.setTimeZone(ZoneId.of("Europe/London"));

    Event event = calendar.getAllEvents().get(0);
    assertTrue(event.isAllDayEvent());
  }

  @Test
  public void testSetTimeZoneSameZone() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.setTimeZone(ZoneId.of("America/New_York"));

    Event event = calendar.getAllEvents().get(0);
    assertEquals(LocalTime.of(10, 0), event.getStartDateTime().toLocalTime());
  }

  @Test
  public void testNewEventHasNoLocation() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    assertEquals(LocationType.NONE, calendar.getAllEvents().get(0).getLocation());
  }

  @Test
  public void testEditLocationToNone() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        LocationType.PHYSICAL, EditSettings.SINGLE);
    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        LocationType.NONE, EditSettings.SINGLE);

    assertEquals(LocationType.NONE, calendar.getAllEvents().get(0).getLocation());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditLocationWithInvalidType() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        "InvalidString", EditSettings.SINGLE);
  }
}