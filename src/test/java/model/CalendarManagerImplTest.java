package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarManagerImpl;
import calendar.model.Event;
import calendar.model.exceptions.DuplicateEventException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

/**
 * A class to test the calendar manager implementation.
 */
public class CalendarManagerImplTest {
  private CalendarManager manager;

  /**
   * A setup method that setups the manager.
   */
  @Before
  public void setUp() {
    manager = new CalendarManagerImpl();
  }

  @Test
  public void testConstructorInitializesEmptyManager() {
    assertNull(manager.getActiveCalendar());
  }

  @Test
  public void testCreateCalendarBasic() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    assertEquals("Work", manager.getActiveCalendar().getCalendarName());
    assertEquals(ZoneId.of("America/New_York"), manager.getActiveCalendar().getTimeZone());
  }

  @Test
  public void testCreateMultipleCalendars() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));
    manager.createCalendar("School", ZoneId.of("Asia/Tokyo"));

    manager.useCalendar("Work");
    assertEquals("Work", manager.getActiveCalendar().getCalendarName());

    manager.useCalendar("Personal");
    assertEquals("Personal", manager.getActiveCalendar().getCalendarName());

    manager.useCalendar("School");
    assertEquals("School", manager.getActiveCalendar().getCalendarName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarDuplicateName() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarNullName() {
    manager.createCalendar(null, ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarEmptyName() {
    manager.createCalendar("", ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarNullTimezone() {
    manager.createCalendar("Work", null);
  }

  @Test
  public void testUseCalendarSwitchesActiveCalendar() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));

    manager.useCalendar("Work");
    assertEquals("Work", manager.getActiveCalendar().getCalendarName());

    manager.useCalendar("Personal");
    assertEquals("Personal", manager.getActiveCalendar().getCalendarName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUseCalendarNonExistent() {
    manager.useCalendar("NonExistent");
  }

  @Test
  public void testGetActiveCalendarInitiallyNull() {
    assertNull(manager.getActiveCalendar());
  }

  @Test
  public void testGetActiveCalendarAfterUse() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    assertNotNull(manager.getActiveCalendar());
    assertEquals("Work", manager.getActiveCalendar().getCalendarName());
  }

  @Test
  public void testEditCalendarName() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.editCalendar("Work", "name", "Business");

    manager.useCalendar("Business");
    assertEquals("Business", manager.getActiveCalendar().getCalendarName());
  }

  @Test
  public void testEditCalendarNameUpdatesActiveCalendar() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    Calendar calendarRef = manager.getActiveCalendar();
    manager.editCalendar("Work", "name", "Business");

    assertEquals("Business", calendarRef.getCalendarName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarNameToDuplicateName() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));

    manager.editCalendar("Work", "name", "Personal");
  }

  @Test
  public void testEditCalendarTimezone() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    manager.editCalendar("Work", "timezone", ZoneId.of("Europe/Paris"));

    manager.useCalendar("Work");
    assertEquals(ZoneId.of("Europe/Paris"), manager.getActiveCalendar().getTimeZone());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarTimezoneWithInvalidType() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    manager.editCalendar("Work", "timezone", "Not a ZoneId");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarInvalidProperty() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    manager.editCalendar("Work", "invalid", "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarNonExistent() {
    manager.editCalendar("NonExistent", "name", "NewName");
  }

  @Test
  public void testEditCalendarPropertyCaseInsensitive() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    manager.editCalendar("Work", "NAME", "Business");
    manager.useCalendar("Business");
    assertEquals("Business", manager.getActiveCalendar().getCalendarName());

    manager.editCalendar("Business", "TimeZone", ZoneId.of("Europe/Paris"));
    assertEquals(ZoneId.of("Europe/Paris"), manager.getActiveCalendar().getTimeZone());
  }

  @Test
  public void testCopyEventPreservesDuration() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createAndAddEvent("Long Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
            LocalDateTime.of(2025, 5, 5, 13, 30), false);

    manager.copyEvent("Long Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "Work",
        LocalDateTime.of(2025, 5, 6, 9, 0));

    List<Event> events = manager.getActiveCalendar().getAllEvents();
    Event copiedEvent =
        events.stream().filter(e -> e.getStartDateTime().equals(LocalDateTime.of(2025, 5, 6, 9, 0)))
            .findFirst().get();

    assertEquals(LocalDateTime.of(2025, 5, 6, 12, 30), copiedEvent.getEndDateTime());
  }

  @Test
  public void testCopyEventToDifferentCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "Personal",
        LocalDateTime.of(2025, 5, 6, 14, 0));

    manager.useCalendar("Personal");
    assertEquals(1, manager.getActiveCalendar().getAllEvents().size());

    Event copiedEvent = manager.getActiveCalendar().getAllEvents().get(0);
    assertEquals("Meeting", copiedEvent.getSubject());
    assertEquals(LocalDateTime.of(2025, 5, 6, 14, 0), copiedEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2025, 5, 6, 15, 0), copiedEvent.getEndDateTime());
  }

  @Test
  public void testCopyEventSeriesWithinSameCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createEventSeries("Standup", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(9, 15), Set.of(DayOfWeek.MONDAY), 2);

    manager.copyEvent("Standup", LocalDateTime.of(2025, 5, 5, 9, 0), "Work",
        LocalDateTime.of(2025, 5, 6, 10, 0));

    assertEquals(3, manager.getActiveCalendar().getAllEvents().size());

    Event copiedEvent = manager.getActiveCalendar().getAllEvents().stream()
        .filter(e -> e.getStartDateTime().equals(LocalDateTime.of(2025, 5, 6, 10, 0))).findFirst()
        .get();

    assertTrue(copiedEvent.isInSeries());
  }

  @Test
  public void testCopyEventSeriesBetweenDifferentCalendarsRemovesSeriesId() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar()
        .createEventSeries("Standup", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(9, 15), Set.of(DayOfWeek.MONDAY), 2);

    manager.copyEvent("Standup", LocalDateTime.of(2025, 5, 5, 9, 0), "Personal",
        LocalDateTime.of(2025, 5, 6, 10, 0));

    manager.useCalendar("Personal");
    Event copiedEvent = manager.getActiveCalendar().getAllEvents().get(0);

    assertFalse(copiedEvent.isInSeries());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventNotFound() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.copyEvent("NonExistent", LocalDateTime.of(2025, 5, 5, 10, 0), "Work",
        LocalDateTime.of(2025, 5, 6, 10, 0));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventAmbiguous() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 12, 0), false);

    manager.copyEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "Work",
        LocalDateTime.of(2025, 5, 6, 10, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventToNonExistentCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "NonExistent",
        LocalDateTime.of(2025, 5, 6, 10, 0));
  }

  @Test(expected = DuplicateEventException.class)
  public void testCopyEventCreatesDuplicate() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "Work",
        LocalDateTime.of(2025, 5, 5, 10, 0));
  }


  @Test
  public void testCopyEventsOnDateToSameCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar().createAndAddEvent("Meeting1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.getActiveCalendar().createAndAddEvent("Meeting2", LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Work", LocalDate.of(2025, 5, 10));

    assertEquals(4, manager.getActiveCalendar().getAllEvents().size());

    List<Event> may10Events = manager.getActiveCalendar().getEventOnDate(LocalDate.of(2025, 5, 10));
    assertEquals(2, may10Events.size());
  }

  @Test
  public void testCopyEventsOnDateToDifferentCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.getActiveCalendar().createAndAddEvent("Meeting2", LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 10));

    manager.useCalendar("Personal");
    assertEquals(2, manager.getActiveCalendar().getAllEvents().size());
  }

  @Test
  public void testCopyEventsOnDatePreservesTime() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 30),
        LocalDateTime.of(2025, 5, 5, 11, 45), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Work", LocalDate.of(2025, 5, 10));

    List<Event> may10Events = manager.getActiveCalendar().getEventOnDate(LocalDate.of(2025, 5, 10));
    Event copiedEvent = may10Events.get(0);

    assertEquals(java.time.LocalTime.of(10, 30), copiedEvent.getStartDateTime().toLocalTime());
    assertEquals(java.time.LocalTime.of(11, 45), copiedEvent.getEndDateTime().toLocalTime());
  }

  @Test
  public void testCopyEventsOnDateWithSeriesInSameCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createEventSeries("Standup", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(9, 15), Set.of(DayOfWeek.MONDAY), 2);


    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Work", LocalDate.of(2025, 5, 19));

    List<Event> may19Events = manager.getActiveCalendar().getEventOnDate(LocalDate.of(2025, 5, 19));
    Event copiedEvent = may19Events.get(0);
    String originalSeriesId = manager.getActiveCalendar().getAllEvents().get(0).getSeriesId().get();
    assertTrue(copiedEvent.isInSeries());
    assertEquals(originalSeriesId, copiedEvent.getSeriesId().get());
  }

  @Test
  public void testCopyEventsOnDateWithSeriesBetweenDifferentCalendars() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar()
        .createEventSeries("Standup", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(9, 15), Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 4);

    List<Event> may5Events = manager.getActiveCalendar().getEventOnDate(LocalDate.of(2025, 5, 5));


    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 19));

    manager.useCalendar("Personal");
    List<Event> copiedEvents = manager.getActiveCalendar().getAllEvents();
    String originalSeriesId = may5Events.get(0).getSeriesId().get();
    assertTrue(copiedEvents.get(0).isInSeries());
    assertNotEquals(originalSeriesId, copiedEvents.get(0).getSeriesId().get());
  }

  @Test
  public void testCopyEventsOnDateMultipleSeriesGetDifferentIds() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar()
        .createEventSeries("Series1", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), 1);

    manager.getActiveCalendar()
        .createEventSeries("Series2", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(11, 0),
            java.time.LocalTime.of(12, 0), Set.of(DayOfWeek.MONDAY), 1);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 12));

    manager.useCalendar("Personal");
    List<Event> copiedEvents = manager.getActiveCalendar().getAllEvents();

    String seriesId1 = copiedEvents.get(0).getSeriesId().get();
    String seriesId2 = copiedEvents.get(1).getSeriesId().get();

    assertNotEquals(seriesId1, seriesId2);
  }

  @Test
  public void testCopyEventsOnDateWithTimezoneConversion() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 10));

    manager.useCalendar("Personal");
    Event copiedEvent = manager.getActiveCalendar().getAllEvents().get(0);

    assertEquals(LocalDate.of(2025, 5, 10), copiedEvent.getStartDateTime().toLocalDate());
    assertEquals(java.time.LocalTime.of(15, 0), copiedEvent.getStartDateTime().toLocalTime());
  }

  @Test
  public void testCopyEventsOnDateEmpty() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Work", LocalDate.of(2025, 5, 10));

    assertEquals(0, manager.getActiveCalendar().getAllEvents().size());
  }

  @Test(expected = IllegalStateException.class)
  public void testCopyEventsOnDateWithoutActiveCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Work", LocalDate.of(2025, 5, 10));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateToNonExistentCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "NonExistent", LocalDate.of(2025, 5, 10));
  }

  @Test(expected = DuplicateEventException.class)
  public void testCopyEventsOnDateCreatesDuplicate() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Work", LocalDate.of(2025, 5, 5));
  }

  @Test
  public void testCopyEventsBetweenSingleWeek() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createAndAddEvent("Monday Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
            LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.getActiveCalendar()
        .createAndAddEvent("Wednesday Meeting", LocalDateTime.of(2025, 5, 7, 14, 0),
            LocalDateTime.of(2025, 5, 7, 15, 0), false);

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 7), "Work",
        LocalDate.of(2025, 5, 12));

    assertEquals(4, manager.getActiveCalendar().getAllEvents().size());

    List<Event> may12Events = manager.getActiveCalendar().getEventOnDate(LocalDate.of(2025, 5, 12));
    assertEquals(1, may12Events.size());
    assertEquals("Monday Meeting", may12Events.get(0).getSubject());

    List<Event> may14Events = manager.getActiveCalendar().getEventOnDate(LocalDate.of(2025, 5, 14));
    assertEquals(1, may14Events.size());
    assertEquals("Wednesday Meeting", may14Events.get(0).getSubject());
  }

  @Test
  public void testCopyEventsBetweenMultipleWeeks() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createEventSeries("Weekly Standup", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(9, 15),
            Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), 6);

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 16), "Work",
        LocalDate.of(2025, 6, 2));

    List<Event> allEvents = manager.getActiveCalendar().getAllEvents();
    long originalCount = allEvents.stream()
        .filter(e -> e.getStartDateTime().isBefore(LocalDateTime.of(2025, 6, 1, 0, 0))).count();
    long copiedCount = allEvents.stream()
        .filter(e -> !e.getStartDateTime().isBefore(LocalDateTime.of(2025, 6, 1, 0, 0))).count();

    assertEquals(6, originalCount);
    assertEquals(6, copiedCount);
  }

  @Test
  public void testCopyEventsBetweenPreservesDayOfWeek() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createAndAddEvent("Tuesday Event", LocalDateTime.of(2025, 5, 6, 10, 0),
            LocalDateTime.of(2025, 5, 6, 11, 0), false);

    manager.getActiveCalendar()
        .createAndAddEvent("Thursday Event", LocalDateTime.of(2025, 5, 8, 14, 0),
            LocalDateTime.of(2025, 5, 8, 15, 0), false);

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 11), "Work",
        LocalDate.of(2025, 5, 19));

    List<Event> copiedEvents = manager.getActiveCalendar().getAllEvents().stream()
        .filter(e -> !e.getStartDateTime().isBefore(LocalDateTime.of(2025, 5, 19, 0, 0)))
        .collect(Collectors.toList());

    assertEquals(DayOfWeek.TUESDAY, copiedEvents.get(0).getStartDateTime().getDayOfWeek());
    assertEquals(DayOfWeek.THURSDAY, copiedEvents.get(1).getStartDateTime().getDayOfWeek());
  }

  @Test
  public void testCopyEventsBetweenToDifferentCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 5), "Personal",
        LocalDate.of(2025, 5, 12));

    manager.useCalendar("Personal");
    assertEquals(1, manager.getActiveCalendar().getAllEvents().size());
  }


  @Test
  public void testCopyEventsBetweenWithSeriesInSameCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createEventSeries("Standup", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(9, 15), Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 4);


    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 14), "Work",
        LocalDate.of(2025, 5, 19));


    List<Event> copiedEvents = manager.getActiveCalendar().getAllEvents().stream()
        .filter(e -> !e.getStartDateTime().isBefore(LocalDateTime.of(2025, 5, 19, 0, 0)))
        .collect(Collectors.toList());
    String originalSeriesId = manager.getActiveCalendar().getAllEvents().get(0).getSeriesId().get();
    assertTrue(copiedEvents.get(0).isInSeries());
    assertEquals(originalSeriesId, copiedEvents.get(0).getSeriesId().get());
  }

  @Test
  public void testCopyEventsBetweenWithSeriesBetweenDifferentCalendars() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar()
        .createEventSeries("Standup", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(9, 15), Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 4);


    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 14), "Personal",
        LocalDate.of(2025, 5, 19));

    String originalSeriesId = manager.getActiveCalendar().getAllEvents().get(0).getSeriesId().get();
    manager.useCalendar("Personal");
    List<Event> copiedEvents = manager.getActiveCalendar().getAllEvents();

    assertTrue(copiedEvents.get(0).isInSeries());
    assertNotEquals(originalSeriesId, copiedEvents.get(0).getSeriesId().get());

    String newSeriesId = copiedEvents.get(0).getSeriesId().get();
    assertTrue(copiedEvents.stream().allMatch(e -> e.getSeriesId().get().equals(newSeriesId)));
  }

  @Test
  public void testCopyEventsBetweenWithMultipleSeriesMaintainsMapping() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar()
        .createEventSeries("Series1", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 4);

    manager.getActiveCalendar()
        .createEventSeries("Series2", LocalDate.of(2025, 5, 6), java.time.LocalTime.of(11, 0),
            java.time.LocalTime.of(12, 0), Set.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY), 4);

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 15), "Personal",
        LocalDate.of(2025, 5, 19));

    manager.useCalendar("Personal");
    List<Event> copiedEvents = manager.getActiveCalendar().getAllEvents();

    List<Event> series1Events = copiedEvents.stream().filter(e -> e.getSubject().equals("Series1"))
        .collect(Collectors.toList());

    List<Event> series2Events = copiedEvents.stream().filter(e -> e.getSubject().equals("Series2"))
        .collect(Collectors.toList());

    String series1Id = series1Events.get(0).getSeriesId().get();
    String series2Id = series2Events.get(0).getSeriesId().get();

    assertNotEquals(series1Id, series2Id);
    assertTrue(series1Events.stream().allMatch(e -> e.getSeriesId().get().equals(series1Id)));
    assertTrue(series2Events.stream().allMatch(e -> e.getSeriesId().get().equals(series2Id)));
  }


  @Test
  public void testCopyEventsBetweenWithTimezoneConversion() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 5), "Personal",
        LocalDate.of(2025, 5, 12));

    manager.useCalendar("Personal");
    Event copiedEvent = manager.getActiveCalendar().getAllEvents().get(0);

    assertEquals(java.time.LocalTime.of(15, 0), copiedEvent.getStartDateTime().toLocalTime());
  }

  @Test
  public void testCopyEventsBetweenEmpty() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 11), "Work",
        LocalDate.of(2025, 5, 19));

    assertEquals(0, manager.getActiveCalendar().getAllEvents().size());
  }

  @Test(expected = IllegalStateException.class)
  public void testCopyEventsBetweenWithoutActiveCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 11), "Work",
        LocalDate.of(2025, 5, 19));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenToNonExistentCalendar() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 11), "NonExistent",
        LocalDate.of(2025, 5, 19));
  }

  @Test(expected = DuplicateEventException.class)
  public void testCopyEventsBetweenCreatesDuplicate() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");

    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEventsBetween(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 5), "Work",
        LocalDate.of(2025, 5, 5));
  }

  @Test
  public void testTimezoneConversionSameTimezone() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 10));

    manager.useCalendar("Personal");
    Event copiedEvent = manager.getActiveCalendar().getAllEvents().get(0);

    assertEquals(java.time.LocalTime.of(10, 0), copiedEvent.getStartDateTime().toLocalTime());
  }

  @Test
  public void testTimezoneConversionNyToLondon() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 10));

    manager.useCalendar("Personal");
    Event copiedEvent = manager.getActiveCalendar().getAllEvents().get(0);

    assertEquals(java.time.LocalTime.of(19, 0), copiedEvent.getStartDateTime().toLocalTime());
  }

  @Test
  public void testTimezoneConversionLondonToTokyo() throws Exception {
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
    manager.createCalendar("Personal", ZoneId.of("Asia/Tokyo"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 5, 10, 0), false);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 10));

    manager.useCalendar("Personal");
    Event copiedEvent = manager.getActiveCalendar().getAllEvents().get(0);

    assertEquals(java.time.LocalTime.of(17, 0), copiedEvent.getStartDateTime().toLocalTime());
  }

  @Test
  public void testMultipleOperationsOnSameManager() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));
    manager.createCalendar("School", ZoneId.of("Asia/Tokyo"));

    manager.useCalendar("Work");
    manager.getActiveCalendar().createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    manager.copyEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "Personal",
        LocalDateTime.of(2025, 5, 6, 10, 0));

    manager.useCalendar("Personal");
    manager.copyEventsOnDate(LocalDate.of(2025, 5, 6), "School", LocalDate.of(2025, 5, 7));

    manager.useCalendar("School");
    assertEquals(1, manager.getActiveCalendar().getAllEvents().size());
  }

  @Test
  public void testUniqueSeriesIdGeneration() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");
    manager.getActiveCalendar()
        .createEventSeries("Series1", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), 2);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 12));

    manager.getActiveCalendar()
        .createEventSeries("Series2", LocalDate.of(2025, 5, 6), java.time.LocalTime.of(11, 0),
            java.time.LocalTime.of(12, 0), Set.of(DayOfWeek.TUESDAY), 2);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 6), "Personal", LocalDate.of(2025, 5, 13));

    manager.useCalendar("Personal");
    List<Event> allEvents = manager.getActiveCalendar().getAllEvents();

    String seriesId1 = allEvents.get(0).getSeriesId().get();
    String seriesId2 = allEvents.get(1).getSeriesId().get();

    assertNotEquals(seriesId1, seriesId2);
    assertTrue(seriesId1.startsWith("SID_COPY_"));
    assertTrue(seriesId2.startsWith("SID_COPY_"));
  }

  @Test
  public void testSeriesCountIncrements() throws Exception {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));

    manager.useCalendar("Work");

    manager.getActiveCalendar()
        .createEventSeries("Series1", LocalDate.of(2025, 5, 5), java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), 2);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 5), "Personal", LocalDate.of(2025, 5, 12));

    String firstSeriesId = null;
    manager.useCalendar("Personal");
    for (Event e : manager.getActiveCalendar().getAllEvents()) {
      if (e.isInSeries()) {
        firstSeriesId = e.getSeriesId().get();
        break;
      }
    }

    manager.useCalendar("Work");
    manager.getActiveCalendar()
        .createEventSeries("Series2", LocalDate.of(2025, 5, 6), java.time.LocalTime.of(11, 0),
            java.time.LocalTime.of(12, 0), Set.of(DayOfWeek.TUESDAY), 2);

    manager.copyEventsOnDate(LocalDate.of(2025, 5, 6), "Personal", LocalDate.of(2025, 5, 13));

    String secondSeriesId = null;
    manager.useCalendar("Personal");
    for (Event e : manager.getActiveCalendar().getEventOnDate(LocalDate.of(2025, 5, 13))) {
      if (e.isInSeries()) {
        secondSeriesId = e.getSeriesId().get();
        break;
      }
    }

    assertNotNull(firstSeriesId);
    assertNotNull(secondSeriesId);

    int firstCounter =
        Integer.parseInt(firstSeriesId.substring(firstSeriesId.lastIndexOf('_') + 1));
    int secondCounter =
        Integer.parseInt(secondSeriesId.substring(secondSeriesId.lastIndexOf('_') + 1));

    assertTrue(secondCounter > firstCounter);
  }


}
