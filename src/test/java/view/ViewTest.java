package view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.model.Calendar;
import calendar.model.CalendarImpl;
import calendar.model.EditSettings;
import calendar.model.Event;
import calendar.model.LocationType;
import calendar.view.ConsoleView;
import calendar.view.View;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ConsoleView.
 * Tests the view's ability to display various types of messages and events.
 */
public class ViewTest {

  private ByteArrayOutputStream outputStream;
  private View view;
  private Calendar testCalendar;

  /**
   * Initializes test fixtures before each test method.
   */
  @Before
  public void setUp() {
    outputStream = new ByteArrayOutputStream();
    view = new ConsoleView(new PrintStream(outputStream));
    testCalendar = new CalendarImpl("TestCalendar", ZoneId.of("America/New_York"));
  }

  @Test
  public void testDisplayMessage() {
    view.displayMessage("Hello World");
    assertEquals("Hello World\n", outputStream.toString());
  }

  @Test
  public void testDisplayError() {
    view.displayError("Something went wrong");
    assertEquals("Error: Something went wrong\n", outputStream.toString());
  }

  @Test
  public void testDisplayBusyStatus() {
    view.displayBusyStatus(true);
    assertEquals("busy\n", outputStream.toString());
  }

  @Test
  public void testDisplayAvailableStatus() {
    view.displayBusyStatus(false);
    assertEquals("available\n", outputStream.toString());
  }

  @Test
  public void testDisplayEventCreated() {
    view.displayEventCreated("Meeting");
    assertEquals("Event created: Meeting\n", outputStream.toString());
  }

  @Test
  public void testDisplayEventEdited() {
    view.displayEventEdited("Meeting");
    assertEquals("Event edited: Meeting\n", outputStream.toString());
  }

  @Test
  public void testDisplayExportSuccess() {
    view.displayExportSuccess("/path/to/calendar.csv");
    assertTrue(outputStream.toString().contains("Calendar exported successfully"));
    assertTrue(outputStream.toString().contains("/path/to/calendar.csv"));
  }

  @Test
  public void testDisplayEventsEmpty() throws Exception {
    List<Event> events = testCalendar.getAllEvents();
    view.displayEvents(events);
    assertEquals("No events found.\n", outputStream.toString());
  }

  @Test
  public void testDisplayEventsOnDateEmpty() throws Exception {
    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");
    assertTrue(outputStream.toString().contains("No events on 2025-05-05"));
  }

  @Test
  public void testDisplayEventsOnDate() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("Events on 2025-05-05:"));
    assertTrue(output.contains("Meeting"));
    assertTrue(output.contains("10:00"));
    assertTrue(output.contains("11:00"));
  }

  @Test
  public void testDisplayEventsOnDateWithLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        LocationType.PHYSICAL, EditSettings.SINGLE);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("Physical"));
  }

  @Test
  public void testDisplayEventsInRange() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 10, 23, 59));

    view.displayEventsInRange(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Events in range:"));
    assertTrue(output.contains("Meeting"));
    assertTrue(output.contains("starting on"));
    assertTrue(output.contains("ending on"));
  }

  @Test
  public void testDisplayEventsInRangeEmpty() throws Exception {
    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 10, 23, 59));

    view.displayEventsInRange(events);
    assertTrue(outputStream.toString().contains("No events in the specified range"));
  }

  @Test
  public void testDisplayEventsInRangeWithLocation() throws Exception {
    testCalendar.createAndAddEvent("Conference", LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 7, 17, 0), false);

    testCalendar.editEvent("Conference", LocalDateTime.of(2025, 5, 5, 9, 0), "location",
        LocationType.PHYSICAL, EditSettings.SINGLE);

    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 10, 23, 59));

    view.displayEventsInRange(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Physical"));
  }

  @Test
  public void testDisplayMultipleEvents() throws Exception {
    testCalendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0), false);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("Event1"));
    assertTrue(output.contains("Event2"));
  }

  @Test
  public void testDisplayAllDayEvent() throws Exception {
    testCalendar.createAndAddEvent("Holiday", LocalDateTime.of(2025, 5, 5, 8, 0),
        LocalDateTime.of(2025, 5, 5, 17, 0), true);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("Holiday"));
    assertTrue(output.contains("08:00"));
    assertTrue(output.contains("17:00"));
  }

  @Test
  public void testDisplayEventWithoutLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("Meeting"));
    assertFalse(output.contains(" at ") || output.contains("("));
  }

  @Test
  public void testFormatPreservesTimeZeros() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 5, 10, 0), false);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("09:00"));
    assertTrue(output.contains("10:00"));
  }

  @Test
  public void testDisplayEventsInRangeFullDetails() throws Exception {
    testCalendar.createAndAddEvent("Multi-day Conference", LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 7, 17, 0), false);

    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 10, 23, 59));

    view.displayEventsInRange(events);

    String output = outputStream.toString();
    assertTrue(output.contains("2025-05-05"));
    assertTrue(output.contains("2025-05-07"));
    assertTrue(output.contains("09:00"));
    assertTrue(output.contains("17:00"));
  }

  @Test
  public void testDefaultConstructor() {
    View defaultView = new ConsoleView();
    assertNotNull(defaultView);
  }

  @Test
  public void testDisplayEventsNonEmpty() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    view.displayEvents(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Meeting"));
    assertTrue(output.contains("2025-05-05"));
  }

  @Test
  public void testDisplayEventsMultiple() throws Exception {
    testCalendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 6, 14, 0),
        LocalDateTime.of(2025, 5, 6, 15, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    view.displayEvents(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Event1"));
    assertTrue(output.contains("Event2"));
    assertTrue(output.contains("2025-05-05"));
    assertTrue(output.contains("2025-05-06"));
  }

  @Test
  public void testFormatEventForDateWithoutLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("Meeting from 10:00 to 11:00"));
    assertFalse(output.contains(" at "));
  }

  @Test
  public void testFormatEventWithFullDetailsWithoutLocation() throws Exception {
    testCalendar.createAndAddEvent("Conference", LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 7, 17, 0), false);

    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 10, 23, 59));

    view.displayEventsInRange(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Conference starting on 2025-05-05 at 09:00"));
    assertTrue(output.contains("ending on 2025-05-07 at 17:00"));
    int atCount = output.split(" at ", -1).length - 1;
    assertEquals(2, atCount);
  }

  @Test
  public void testFormatEventWithFullDetailsWithLocation() throws Exception {
    testCalendar.createAndAddEvent("Workshop", LocalDateTime.of(2025, 5, 10, 14, 30),
        LocalDateTime.of(2025, 5, 10, 16, 45), false);

    testCalendar.editEvent("Workshop", LocalDateTime.of(2025, 5, 10, 14, 30), "location",
        LocationType.ONLINE, EditSettings.SINGLE);

    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 15, 23, 59));

    view.displayEventsInRange(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Workshop"));
    assertTrue(output.contains("2025-05-10"));
    assertTrue(output.contains("14:30"));
    assertTrue(output.contains("16:45"));
    assertTrue(output.contains("at Online"));
  }

  @Test
  public void testDisplayEventsOnDateMultipleEvents() throws Exception {
    testCalendar.createAndAddEvent("Morning Meeting", LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 5, 10, 0), false);

    testCalendar.createAndAddEvent("Lunch", LocalDateTime.of(2025, 5, 5, 12, 0),
        LocalDateTime.of(2025, 5, 5, 13, 0), false);

    testCalendar.editEvent("Lunch", LocalDateTime.of(2025, 5, 5, 12, 0), "location",
        LocationType.PHYSICAL, EditSettings.SINGLE);

    testCalendar.createAndAddEvent("Afternoon Session", LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 16, 0), false);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("Morning Meeting"));
    assertTrue(output.contains("Lunch"));
    assertTrue(output.contains("Afternoon Session"));
    assertTrue(output.contains("Physical"));
  }

  @Test
  public void testDisplayEventsInRangeMultipleEvents() throws Exception {
    testCalendar.createAndAddEvent("Day 1 Event", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.createAndAddEvent("Day 2 Event", LocalDateTime.of(2025, 5, 6, 14, 0),
        LocalDateTime.of(2025, 5, 6, 15, 0), false);

    testCalendar.editEvent("Day 2 Event", LocalDateTime.of(2025, 5, 6, 14, 0), "location",
        LocationType.ONLINE, EditSettings.SINGLE);

    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 10, 23, 59));

    view.displayEventsInRange(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Day 1 Event"));
    assertTrue(output.contains("Day 2 Event"));
    assertTrue(output.contains("2025-05-05"));
    assertTrue(output.contains("2025-05-06"));
    assertTrue(output.contains("Online"));
  }

  @Test
  public void testFormatEventSimple() throws Exception {
    testCalendar.createAndAddEvent("Simple Event", LocalDateTime.of(2025, 12, 25, 10, 0),
        LocalDateTime.of(2025, 12, 25, 11, 0), false);

    testCalendar.editEvent("Simple Event", LocalDateTime.of(2025, 12, 25, 10, 0), "location",
        LocationType.PHYSICAL, EditSettings.SINGLE);

    testCalendar.editEvent("Simple Event", LocalDateTime.of(2025, 12, 25, 10, 0), "description",
        "Some description", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    view.displayEvents(events);

    String output = outputStream.toString();
    assertTrue(output.contains("Simple Event"));
    assertTrue(output.contains("2025-12-25"));
  }

  @Test
  public void testTimeFormatting() throws Exception {
    testCalendar.createAndAddEvent("Early Morning", LocalDateTime.of(2025, 5, 5, 0, 0),
        LocalDateTime.of(2025, 5, 5, 1, 30), false);

    List<Event> events = testCalendar.getEventOnDate(LocalDate.of(2025, 5, 5));
    view.displayEventsOnDate(events, "2025-05-05");

    String output = outputStream.toString();
    assertTrue(output.contains("00:00"));
    assertTrue(output.contains("01:30"));
  }

  @Test
  public void testDateFormatting() throws Exception {
    testCalendar.createAndAddEvent("New Year", LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 1, 23, 59), false);

    List<Event> events = testCalendar.getEventsInRange(LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 31, 23, 59));

    view.displayEventsInRange(events);

    String output = outputStream.toString();
    assertTrue(output.contains("2025-01-01"));
  }
}