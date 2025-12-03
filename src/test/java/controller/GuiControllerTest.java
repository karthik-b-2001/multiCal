package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.controller.GuiController;
import calendar.controller.GuiControllerImpl;
import calendar.model.CalendarManager;
import calendar.model.CalendarManagerImpl;
import calendar.model.EditSettings;
import calendar.model.Event;
import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the GuiController implementation.
 */
public class GuiControllerTest {
  private GuiController controller;
  private MockGuiView mockView;
  private CalendarManager manager;


  /**
   * Sets up the test environment.
   */
  @Before
  public void setUp() {
    manager = new CalendarManagerImpl();
    controller = new GuiControllerImpl(manager);
    mockView = new MockGuiView();
    controller.setView(mockView);
  }

  @Test
  public void testSetViewUpdatesCalendarList() {
    assertEquals(1, mockView.updateCalendarListCallCount);
    assertTrue(mockView.lastCalendarList.contains("Personal"));
  }

  @Test
  public void testCreateDefaultCalendar() {
    List<String> names = controller.getAllCalendarNames();
    assertEquals(1, names.size());
    assertEquals("Personal", names.get(0));
    assertEquals("Personal", controller.getActiveCalendarName());
  }

  @Test
  public void testCreateCalendar() {
    controller.createCalendar("Work", ZoneId.of("America/New_York"));

    assertEquals(2, controller.getAllCalendarNames().size());
    assertTrue(controller.getAllCalendarNames().contains("Work"));
    assertEquals(1, mockView.showMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Calendar created: Work"));
    assertEquals(2, mockView.updateCalendarListCallCount);
    assertEquals(1, mockView.refreshCalendarCallCount);
  }

  @Test
  public void testCreateCalendarSwitchesToNewCalendar() {
    controller.createCalendar("Work", ZoneId.of("America/Chicago"));
    assertEquals("Work", controller.getActiveCalendarName());
  }

  @Test
  public void testCreateCalendarAssignsColor() {
    controller.createCalendar("Work", ZoneId.of("America/New_York"));
    Color color = controller.getCalendarColor("Work");
    assertNotNull(color);
  }

  @Test
  public void testCreateMultipleCalendarsHaveDifferentColors() {
    controller.createCalendar("Work", ZoneId.of("America/New_York"));
    controller.createCalendar("School", ZoneId.of("America/Chicago"));

    Color color1 = controller.getCalendarColor("Personal");
    Color color2 = controller.getCalendarColor("Work");
    Color color3 = controller.getCalendarColor("School");

    assertNotEquals(color1, color2);
    assertNotEquals(color2, color3);
  }

  @Test
  public void testCreateCalendarDuplicateNameShowsError() {
    controller.createCalendar("Duplicate", ZoneId.of("America/New_York"));

    mockView.reset();
    controller.createCalendar("Duplicate", ZoneId.of("America/Chicago"));

    assertEquals(1, mockView.showErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Failed to create calendar"));
  }

  @Test
  public void testSwitchCalendar() {
    controller.createCalendar("Work", ZoneId.of("America/New_York"));

    mockView.reset();
    controller.switchCalendar("Personal");

    assertEquals("Personal", controller.getActiveCalendarName());
    assertEquals(1, mockView.refreshCalendarCallCount);
    assertEquals(0, mockView.showErrorCallCount);
  }

  @Test
  public void testSwitchToNonexistentCalendarShowsError() {
    controller.switchCalendar("Nonexistent");

    assertEquals(1, mockView.showErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Failed to switch calendar"));
  }

  @Test
  public void testCreateSingleEvent() {
    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertEquals(1, events.size());
    assertEquals("Meeting", events.get(0).getSubject());
    assertEquals(1, mockView.showMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Event created: Meeting"));
    assertEquals(1, mockView.refreshCalendarCallCount);
  }

  @Test
  public void testCreateAllDayEvent() {
    controller.createSingleEvent("Holiday", LocalDate.of(2025, 12, 25), LocalTime.of(10, 0),
        LocalTime.of(11, 0), true);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 12, 25));
    assertEquals(1, events.size());
    assertTrue(events.get(0).isAllDayEvent());
    assertEquals(LocalTime.of(8, 0), events.get(0).getStartDateTime().toLocalTime());
    assertEquals(LocalTime.of(17, 0), events.get(0).getEndDateTime().toLocalTime());
  }

  @Test
  public void testCreateRecurringEventWithOccurrences() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);

    controller.createRecurringEvent("Standup", LocalDate.of(2025, 5, 5), LocalTime.of(9, 0),
        LocalTime.of(9, 30), weekdays, 5, null, false);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertFalse(events.isEmpty());
    assertEquals(1, mockView.showMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Recurring event created"));
  }

  @Test
  public void testCreateRecurringEventWithEndDate() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.FRIDAY);

    controller.createRecurringEvent("Review", LocalDate.of(2025, 5, 2), LocalTime.of(14, 0),
        LocalTime.of(15, 0), weekdays, null, LocalDate.of(2025, 5, 30), false);

    assertEquals(1, mockView.showMessageCallCount);
    assertEquals(1, mockView.refreshCalendarCallCount);
  }

  @Test
  public void testCreateAllDayRecurringSeries() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    controller.createRecurringEvent("Gym", LocalDate.of(2025, 5, 3), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, 4, null, true);

    assertEquals(1, mockView.showMessageCallCount);
  }


  @Test
  public void testEditEventLocation() {
    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    controller.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location", "Room 301",
        EditSettings.SINGLE);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertEquals("Room 301", events.get(0).getLocation().get());
  }

  @Test
  public void testEditEventDescription() {
    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    controller.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description",
        "Important discussion", EditSettings.SINGLE);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertEquals("Important discussion", events.get(0).getDescription().get());
  }

  @Test
  public void testGetEventsForDateEmptyCalendar() {
    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertEquals(0, events.size());
  }

  @Test
  public void testGetEventsForDateMultipleEvents() {
    controller.createSingleEvent("Event1", LocalDate.of(2025, 5, 5), LocalTime.of(9, 0),
        LocalTime.of(10, 0), false);
    controller.createSingleEvent("Event2", LocalDate.of(2025, 5, 5), LocalTime.of(14, 0),
        LocalTime.of(15, 0), false);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertEquals(2, events.size());
  }

  @Test
  public void testGetCurrentTimezone() {
    ZoneId timezone = controller.getCurrentTimezone();
    assertNotNull(timezone);
    assertEquals(ZoneId.systemDefault(), timezone);
  }

  @Test
  public void testGetCurrentTimezoneAfterCreatingCalendar() {
    controller.createCalendar("Tokyo", ZoneId.of("Asia/Tokyo"));

    ZoneId timezone = controller.getCurrentTimezone();
    assertEquals(ZoneId.of("Asia/Tokyo"), timezone);
  }

  @Test
  public void testGetActiveCalendar() {
    calendar.model.Calendar cal = controller.getActiveCalendar();
    assertNotNull(cal);
    assertEquals("Personal", cal.getCalendarName());
  }

  @Test
  public void testGetCalendarColorForNonexistentCalendar() {
    Color color = controller.getCalendarColor("Nonexistent");
    assertNotNull(color);
  }

  @Test
  public void testColorRotation() {
    for (int i = 0; i < 10; i++) {
      controller.createCalendar("Cal" + i, ZoneId.systemDefault());
    }

    Color color1 = controller.getCalendarColor("Cal0");
    Color color6 = controller.getCalendarColor("Cal5");

    assertEquals(color1, color6);
  }

  @Test
  public void testCreateEventRefreshesView() {
    controller.createSingleEvent("Test", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    assertEquals(1, mockView.refreshCalendarCallCount);
  }

  @Test
  public void testEditEventRefreshesView() {
    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    mockView.reset();

    controller.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "Updated",
        EditSettings.SINGLE);

    assertEquals(1, mockView.refreshCalendarCallCount);
  }

  @Test
  public void testCreateCalendarRefreshesView() {
    mockView.reset();

    controller.createCalendar("Work", ZoneId.of("America/New_York"));

    assertEquals(1, mockView.refreshCalendarCallCount);
  }

  @Test
  public void testSwitchCalendarRefreshesView() {
    controller.createCalendar("Work", ZoneId.of("America/New_York"));

    mockView.reset();
    controller.switchCalendar("Personal");

    assertEquals(1, mockView.refreshCalendarCallCount);
  }

  @Test
  public void testCreateEventShowsSuccessMessage() {
    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    assertEquals(1, mockView.showMessageCallCount);
    assertEquals("Event created: Meeting", mockView.messages.get(0));
  }

  @Test
  public void testCreateRecurringEventShowsSuccessMessage() {
    controller.createRecurringEvent("Daily", LocalDate.of(2025, 5, 5), LocalTime.of(9, 0),
        LocalTime.of(10, 0), Set.of(DayOfWeek.MONDAY), 5, null, false);

    assertTrue(mockView.messages.get(0).contains("Recurring event created"));
  }

  @Test
  public void testCreateEventDuplicateShowsError() {
    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    mockView.reset();

    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    assertEquals(1, mockView.showErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Failed to create event"));
  }

  @Test
  public void testEditNonexistentEventShowsError() {
    controller.editEvent("Nonexistent", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "New",
        EditSettings.SINGLE);

    assertEquals(1, mockView.showErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Failed to edit event"));
  }



  @Test
  public void testGetEventsForDateReturnsCorrectEvents() {
    controller.createSingleEvent("Event1", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);
    controller.createSingleEvent("Event2", LocalDate.of(2025, 5, 6), LocalTime.of(14, 0),
        LocalTime.of(15, 0), false);

    List<Event> may5 = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    List<Event> may6 = controller.getEventsForDate(LocalDate.of(2025, 5, 6));

    assertEquals(1, may5.size());
    assertEquals(1, may6.size());
    assertEquals("Event1", may5.get(0).getSubject());
    assertEquals("Event2", may6.get(0).getSubject());
  }

  @Test
  public void testMultipleCalendarsIndependentEvents() {
    controller.createSingleEvent("Personal Event", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), false);

    controller.createCalendar("Work", ZoneId.of("America/New_York"));
    controller.createSingleEvent("Work Event", LocalDate.of(2025, 5, 5), LocalTime.of(14, 0),
        LocalTime.of(15, 0), false);

    controller.switchCalendar("Personal");
    List<Event> personalEvents = controller.getEventsForDate(LocalDate.of(2025, 5, 5));

    controller.switchCalendar("Work");
    List<Event> workEvents = controller.getEventsForDate(LocalDate.of(2025, 5, 5));

    assertEquals(1, personalEvents.size());
    assertEquals(1, workEvents.size());
    assertEquals("Personal Event", personalEvents.get(0).getSubject());
    assertEquals("Work Event", workEvents.get(0).getSubject());
  }

  @Test
  public void testGetTimezoneForDifferentCalendars() {
    controller.createCalendar("Tokyo", ZoneId.of("Asia/Tokyo"));

    assertEquals(ZoneId.of("Asia/Tokyo"), controller.getCurrentTimezone());

    controller.switchCalendar("Personal");
    assertEquals(ZoneId.systemDefault(), controller.getCurrentTimezone());
  }



  @Test
  public void testCreateAllDayRecurringEventWithEndDate() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);

    controller.createRecurringEvent("Gym", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, null, LocalDate.of(2025, 5, 30), true);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertFalse(events.isEmpty());
    assertTrue(events.get(0).isAllDayEvent());
    assertEquals(1, mockView.showMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Recurring event created"));
  }

  @Test
  public void testCreateRecurringEventWithInvalidDataShowsError() {
    Set<DayOfWeek> emptyWeekdays = Set.of();

    controller.createRecurringEvent("Invalid", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), emptyWeekdays, 5, null, false);

    assertEquals(1, mockView.showErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Failed to create recurring event"));
  }

  @Test
  public void testCreateRecurringEventWithEndBeforeStartShowsError() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    controller.createRecurringEvent("Invalid", LocalDate.of(2025, 5, 20), LocalTime.of(11, 0),
        LocalTime.of(10, 0), weekdays, 3, null, false);

    assertEquals(1, mockView.showErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Failed to create recurring event"));
  }

  @Test
  public void testCreateRecurringEventWithZeroOccurrencesShowsError() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    controller.createRecurringEvent("Invalid", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, 0, null, false);

    assertEquals(1, mockView.showErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Failed to create recurring event"));
  }


  @Test
  public void testCreateAllDayRecurringEventWithOccurrencesCreatesEvents() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    controller.createRecurringEvent("Gym", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, 3, null, true);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertFalse(events.isEmpty());
    assertTrue(events.get(0).isAllDayEvent());

    int count = 0;
    LocalDate current = LocalDate.of(2025, 5, 5);
    for (int i = 0; i < 30; i++) {
      if (current.getDayOfWeek() == DayOfWeek.MONDAY) {
        if (!controller.getEventsForDate(current).isEmpty()) {
          count++;
        }
      }
      current = current.plusDays(1);
    }
    assertEquals(3, count);
  }

  @Test
  public void testCreateTimedRecurringEventWithEndDateCreatesEvents() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.FRIDAY);

    controller.createRecurringEvent("Review", LocalDate.of(2025, 5, 2), LocalTime.of(14, 0),
        LocalTime.of(15, 0), weekdays, null, LocalDate.of(2025, 5, 30), false);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 2));
    assertFalse(events.isEmpty());
    assertEquals("Review", events.get(0).getSubject());
    assertFalse(events.get(0).isAllDayEvent());

    List<Event> endDateEvents = controller.getEventsForDate(LocalDate.of(2025, 5, 30));
    assertFalse(endDateEvents.isEmpty());
  }

  @Test
  public void testCreateSingleEventAllDayHasCorrectEndTime() {
    controller.createSingleEvent("Holiday", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), true);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertEquals(LocalTime.of(17, 0), events.get(0).getEndDateTime().toLocalTime());
  }

  @Test
  public void testCreateSingleEventTimedHasCorrectEndTime() {
    controller.createSingleEvent("Meeting", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 30), false);

    List<Event> events = controller.getEventsForDate(LocalDate.of(2025, 5, 5));
    assertEquals(LocalTime.of(11, 30), events.get(0).getEndDateTime().toLocalTime());
  }

  @Test
  public void testCreateAllDayRecurringWithOccurrencesCreatesMultipleEvents() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    controller.createRecurringEvent("Gym", LocalDate.of(2025, 5, 5), LocalTime.of(10, 0),
        LocalTime.of(11, 0), weekdays, 3, null, true);

    int count = 0;
    LocalDate current = LocalDate.of(2025, 5, 5);
    for (int i = 0; i < 30; i++) {
      if (current.getDayOfWeek() == DayOfWeek.MONDAY) {
        if (!controller.getEventsForDate(current).isEmpty()) {
          count++;
        }
      }
      current = current.plusDays(1);
    }
    assertEquals(3, count);
  }

  @Test
  public void testCreateTimedRecurringWithEndDateCreatesEvents() {
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.FRIDAY);

    controller.createRecurringEvent("Review", LocalDate.of(2025, 5, 2), LocalTime.of(14, 0),
        LocalTime.of(15, 0), weekdays, null, LocalDate.of(2025, 5, 30), false);

    List<Event> startEvents = controller.getEventsForDate(LocalDate.of(2025, 5, 2));
    List<Event> endEvents = controller.getEventsForDate(LocalDate.of(2025, 5, 30));

    assertFalse(startEvents.isEmpty());
    assertFalse(endEvents.isEmpty());
  }

  @Test
  public void testDefaultCalendarHasFirstColor() {
    Color defaultColor = controller.getCalendarColor("Personal");
    assertEquals(new Color(173, 216, 230), defaultColor);
  }


  @Test
  public void testCalendarColorsAssignedCorrectly() {
    controller.createCalendar("Cal1", ZoneId.systemDefault());
    controller.createCalendar("Cal2", ZoneId.systemDefault());

    Color personal = controller.getCalendarColor("Personal");
    Color cal1 = controller.getCalendarColor("Cal1");
    Color cal2 = controller.getCalendarColor("Cal2");

    assertEquals(new Color(173, 216, 230), personal);
    assertEquals(new Color(255, 182, 193), cal1);
    assertEquals(new Color(144, 238, 144), cal2);
  }

  @Test
  public void testCalendarColorsWrapAround() {
    for (int i = 0; i < 5; i++) {
      controller.createCalendar("Cal" + i, ZoneId.systemDefault());
    }

    Color firstColor = controller.getCalendarColor("Personal");
    Color wrappedColor = controller.getCalendarColor("Cal4");

    assertEquals(firstColor, wrappedColor);
  }

  @Test
  public void testCalendarColorsAssignedSequentially() {
    Color[] expectedColors = {
        new Color(173, 216, 230),
        new Color(255, 182, 193),
        new Color(144, 238, 144),
        new Color(255, 218, 185),
        new Color(221, 160, 221)
    };

    String[] calNames = new String[5];
    calNames[0] = "Personal";

    for (int i = 1; i < 5; i++) {
      calNames[i] = "Cal" + i;
      controller.createCalendar(calNames[i], ZoneId.systemDefault());
    }

    for (int i = 0; i < 5; i++) {
      Color actual = controller.getCalendarColor(calNames[i]);
      assertEquals(expectedColors[i], actual);
    }
  }

  @Test
  public void testCalendarColorsWrapAroundCorrectly() {
    for (int i = 0; i < 10; i++) {
      controller.createCalendar("Cal" + i, ZoneId.systemDefault());
    }

    assertEquals(controller.getCalendarColor("Personal"),
        controller.getCalendarColor("Cal4"));
    assertEquals(controller.getCalendarColor("Personal"),
        controller.getCalendarColor("Cal9"));
    assertEquals(controller.getCalendarColor("Cal0"),
        controller.getCalendarColor("Cal5"));
    assertEquals(controller.getCalendarColor("Cal1"),
        controller.getCalendarColor("Cal6"));
  }

  @Test
  public void testTwentyCalendarsColorRotation() {
    for (int i = 0; i < 20; i++) {
      controller.createCalendar("Cal" + i, ZoneId.systemDefault());
    }

    for (int i = 0; i < 15; i++) {
      String first = i == 0 ? "Personal" : "Cal" + (i - 1);
      String second = "Cal" + (i + 4);
      assertEquals(controller.getCalendarColor(first),
          controller.getCalendarColor(second));
    }
  }

}
