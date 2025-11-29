package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import calendar.model.Calendar;
import calendar.model.CalendarImpl;
import calendar.model.EditSettings;
import calendar.model.Event;
import calendar.model.EventStatus;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * This is class that tests EventImpl using createAndAdd methods.
 */
public class EventBuilderImplTest {
  private Calendar calendar;

  /**
   * Sets up a new CalendarImpl instance before each test with default name and timezone.
   */
  @Before
  public void setUp() {
    calendar = new CalendarImpl("TestCalendar", ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithNullSubject() throws Exception {
    calendar.createAndAddEvent(null, LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithEmptySubject() throws Exception {
    calendar.createAndAddEvent("", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithWhitespaceSubject() throws Exception {
    calendar.createAndAddEvent("   ", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithNullStartDateTime() throws Exception {
    calendar.createAndAddEvent("Meeting", null, LocalDateTime.of(2025, 5, 5, 11, 0), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithNullEndDateTimeForTimedEvent() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), null, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithEndBeforeStart() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 11, 0),
        LocalDateTime.of(2025, 5, 5, 10, 0), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithEndEqualsStart() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 10, 0), false);
  }

  @Test
  public void testCreateAllDayEventWithNullEndDateTime() throws Exception {
    calendar.createAndAddEvent("Holiday", LocalDateTime.of(2025, 5, 5, 10, 0), null, true);

    assertEquals(1, calendar.getAllEvents().size());
    assertTrue(calendar.getAllEvents().get(0).isAllDayEvent());
  }


  @Test
  public void testEqualsReflexive() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event = calendar.getAllEvents().get(0);
    assertTrue(event.equals(event));
  }

  @Test
  public void testEqualsSymmetric() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 6, 10, 0),
        LocalDateTime.of(2025, 5, 6, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar.getAllEvents().get(0);

    assertTrue(event1.equals(event2));
    assertTrue(event2.equals(event1));
  }

  @Test
  public void testEqualsWithNull() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event = calendar.getAllEvents().get(0);
    assertFalse(event.equals(null));
  }

  @Test
  public void testEqualsWithDifferentClass() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event = calendar.getAllEvents().get(0);
    assertFalse(event.equals("Not an Event"));
  }

  @Test
  public void testEqualsSameSubjectStartEnd() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Calendar calendar2 = new CalendarImpl("Other", ZoneId.of("America/New_York"));
    calendar2.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar2.getAllEvents().get(0);

    assertTrue(event1.equals(event2));
  }

  @Test
  public void testNotEqualsDifferentSubject() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Lunch", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar.getAllEvents().get(1);

    assertFalse(event1.equals(event2));
  }

  @Test
  public void testNotEqualsDifferentStart() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 30),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar.getAllEvents().get(1);

    assertFalse(event1.equals(event2));
  }

  @Test
  public void testNotEqualsDifferentEnd() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 30), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar.getAllEvents().get(1);

    assertFalse(event1.equals(event2));
  }

  @Test
  public void testEqualsIgnoresDescription() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description",
        "Important meeting", EditSettings.SINGLE);

    Calendar calendar2 = new CalendarImpl("Other", ZoneId.of("America/New_York"));
    calendar2.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar2.getAllEvents().get(0);

    assertTrue(event1.equals(event2));
  }

  @Test
  public void testEqualsIgnoresLocation() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location", "Room 101",
        EditSettings.SINGLE);

    Calendar calendar2 = new CalendarImpl("Other", ZoneId.of("America/New_York"));
    calendar2.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar2.getAllEvents().get(0);

    assertTrue(event1.equals(event2));
  }

  @Test
  public void testEqualsIgnoresStatus() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "status",
        EventStatus.PRIVATE, EditSettings.SINGLE);

    Calendar calendar2 = new CalendarImpl("Other", ZoneId.of("America/New_York"));
    calendar2.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar2.getAllEvents().get(0);

    assertTrue(event1.equals(event2));
  }

  @Test
  public void testEqualsIgnoresSeriesId() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    Calendar calendar2 = new CalendarImpl("Other", ZoneId.of("America/New_York"));
    calendar2.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar2.getAllEvents().get(0);

    assertTrue(event1.equals(event2));
  }


  @Test
  public void testHashCodeConsistency() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event = calendar.getAllEvents().get(0);
    int hash1 = event.hashCode();
    int hash2 = event.hashCode();

    assertEquals(hash1, hash2);
  }

  @Test
  public void testHashCodeEqualObjectsSameHash() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Calendar calendar2 = new CalendarImpl("Other", ZoneId.of("America/New_York"));
    calendar2.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar2.getAllEvents().get(0);

    assertEquals(event1.hashCode(), event2.hashCode());
  }

  @Test
  public void testHashCodeDifferentObjectsMayDiffer() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.createAndAddEvent("Lunch", LocalDateTime.of(2025, 5, 5, 12, 0),
        LocalDateTime.of(2025, 5, 5, 13, 0), false);

    Event event1 = calendar.getAllEvents().get(0);
    Event event2 = calendar.getAllEvents().get(1);

    assertNotEquals(event1.hashCode(), event2.hashCode());
  }


  @Test
  public void testCopyWithNewTimesPreservesSubject() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0));

    assertEquals(original.getSubject(), copy.getSubject());
  }

  @Test
  public void testCopyWithNewTimesPreservesDescription() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description", "Important",
        EditSettings.SINGLE);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0));

    assertEquals(original.getDescription(), copy.getDescription());
  }

  @Test
  public void testCopyWithNewTimesPreservesLocation() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location", "Room 101",
        EditSettings.SINGLE);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0));

    assertEquals(original.getLocation(), copy.getLocation());
  }

  @Test
  public void testCopyWithNewTimesPreservesStatus() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "status",
        EventStatus.PRIVATE, EditSettings.SINGLE);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0));

    assertEquals(original.getStatus(), copy.getStatus());
  }

  @Test
  public void testCopyWithNewTimesPreservesSeriesId() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0));

    assertEquals(original.getSeriesId(), copy.getSeriesId());
  }

  @Test
  public void testCopyWithNewTimesPreservesIsAllDay() throws Exception {
    calendar.createAndAddEvent("Holiday", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 17, 0), true);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 6, 10, 0),
        LocalDateTime.of(2025, 5, 6, 17, 0));

    assertEquals(original.isAllDayEvent(), copy.isAllDayEvent());
  }

  @Test
  public void testCopyWithNewTimesChangesStart() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event original = calendar.getAllEvents().get(0);
    LocalDateTime newStart = LocalDateTime.of(2025, 5, 5, 14, 0);
    Event copy = original.copyWithNewTimes(newStart, LocalDateTime.of(2025, 5, 5, 15, 0));

    assertEquals(newStart, copy.getStartDateTime());
  }

  @Test
  public void testCopyWithNewTimesChangesEnd() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event original = calendar.getAllEvents().get(0);
    LocalDateTime newEnd = LocalDateTime.of(2025, 5, 5, 15, 0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 5, 14, 0), newEnd);

    assertEquals(newEnd, copy.getEndDateTime());
  }

  @Test
  public void testCopyWithNewTimesCreatesNewInstance() throws Exception {
    calendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithNewTimes(LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0));

    assertFalse(original == copy);
  }

  @Test
  public void testCopyWithSeriesIdPreservesAllFieldsExceptSeriesId() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description", "Important",
        EditSettings.SINGLE);
    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location", "Room 101",
        EditSettings.SINGLE);
    calendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "status",
        EventStatus.PRIVATE, EditSettings.SINGLE);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithSeriesId("NEW_SID");

    assertEquals(original.getSubject(), copy.getSubject());
    assertEquals(original.getStartDateTime(), copy.getStartDateTime());
    assertEquals(original.getEndDateTime(), copy.getEndDateTime());
    assertEquals(original.getDescription(), copy.getDescription());
    assertEquals(original.getLocation(), copy.getLocation());
    assertEquals(original.getStatus(), copy.getStatus());
    assertEquals(original.isAllDayEvent(), copy.isAllDayEvent());
  }

  @Test
  public void testCopyWithSeriesIdChangesSeriesId() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithSeriesId("NEW_SID");

    assertEquals("NEW_SID", copy.getSeriesId().get());
    assertNotEquals(original.getSeriesId().get(), copy.getSeriesId().get());
  }

  @Test
  public void testCopyWithSeriesIdCanSetToNull() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithSeriesId(null);

    assertFalse(copy.getSeriesId().isPresent());
    assertFalse(copy.isInSeries());
  }

  @Test
  public void testCopyWithSeriesIdCreatesNewInstance() throws Exception {
    calendar.createEventSeries("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), 1);

    Event original = calendar.getAllEvents().get(0);
    Event copy = original.copyWithSeriesId("NEW_SID");

    assertFalse(original == copy);
  }

}
