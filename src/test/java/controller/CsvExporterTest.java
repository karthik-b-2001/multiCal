package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.controller.utils.CsvExporter;
import calendar.model.Calendar;
import calendar.model.CalendarImpl;
import calendar.model.EditSettings;
import calendar.model.Event;
import calendar.model.EventStatus;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CsvExporter.
 */
public class CsvExporterTest {

  private CsvExporter exporter;
  private List<Path> filesToCleanup;
  private Calendar testCalendar;

  /**
   * Sets up the test environment before each test.
   * Initializes the CsvExporter and a test calendar.
   */
  @Before
  public void setUp() {
    exporter = new CsvExporter();
    filesToCleanup = new java.util.ArrayList<>();
    testCalendar = new CalendarImpl("TestCalendar", ZoneId.of("America/New_York"));
  }

  /**
   * Cleans up any files created during the tests.
   */
  @After
  public void cleanup() {
    for (Path path : filesToCleanup) {
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        // do nothing
      }
    }
  }

  @Test
  public void testExportEmptyList() throws IOException {
    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-empty.csv");
    filesToCleanup.add(filePath);

    String result = exporter.export(events, filePath, testCalendar);

    assertTrue(Files.exists(filePath));
    assertNotNull(result);
    assertTrue(result.contains("test-empty.csv"));

    List<String> lines = Files.readAllLines(filePath);
    assertEquals(1, lines.size());
    assertTrue(lines.get(0).contains("Subject,Start Date"));
  }

  @Test
  public void testExportSingleTimedEvent() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 30),
        LocalDateTime.of(2025, 5, 5, 11, 45), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-timed.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertEquals(2, lines.size());

    String eventLine = lines.get(1);
    assertTrue(eventLine.contains("Meeting"));
    assertTrue(eventLine.contains("05/05/2025"));
    assertTrue(eventLine.contains("10:30 AM"));
    assertTrue(eventLine.contains("11:45 AM"));
    assertTrue(eventLine.contains("False"));
  }

  @Test
  public void testExportSingleAllDayEvent() throws Exception {
    testCalendar.createAndAddEvent("Holiday", LocalDateTime.of(2025, 12, 25, 8, 0),
        LocalDateTime.of(2025, 12, 25, 17, 0), true);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-allday.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);

    assertTrue(eventLine.contains("Holiday"));
    assertTrue(eventLine.contains("12/25/2025"));
    assertTrue(eventLine.contains("True"));

    String[] fields = eventLine.split(",", -1);
    assertEquals("", fields[2]);
    assertEquals("", fields[4]);
  }

  @Test
  public void testExportEventWithDescription() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description",
        "Important discussion", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-desc.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertTrue(lines.get(1).contains("Important discussion"));
  }

  @Test
  public void testExportEventWithLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location", "Room 301",
        EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-loc.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertTrue(lines.get(1).contains("Room 301"));
  }

  @Test
  public void testExportPrivateEvent() throws Exception {
    testCalendar.createAndAddEvent("Private Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Private Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "status",
        EventStatus.PRIVATE, EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-private.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    assertTrue(eventLine.endsWith("True"));
  }

  @Test
  public void testExportPublicEvent() throws Exception {
    testCalendar.createAndAddEvent("Public Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Public Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "status",
        EventStatus.PUBLIC, EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-public.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    assertTrue(eventLine.endsWith("False"));
  }

  @Test
  public void testEscapeCsvWithComma() throws Exception {
    testCalendar.createAndAddEvent("Meeting, Discussion", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-comma.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertTrue(lines.get(1).contains("\"Meeting, Discussion\""));
  }

  @Test
  public void testEscapeCsvWithQuote() throws Exception {
    testCalendar.createAndAddEvent("Say \"Hello\"", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-quote.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertTrue(lines.get(1).contains("\"\""));
  }

  @Test
  public void testEscapeCsvWithNewline() throws Exception {
    testCalendar.createAndAddEvent("Line1\nLine2", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-newline.csv");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String content = String.join("\n", lines);
    assertTrue(content.contains("\"Line1\nLine2\""));
  }

  @Test
  public void testEscapeCsvNoEscaping() throws Exception {
    testCalendar.createAndAddEvent("SimpleSubject", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-simple.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    assertTrue(eventLine.startsWith("SimpleSubject,"));
    assertFalse(eventLine.startsWith("\"SimpleSubject\""));
  }

  @Test
  public void testEscapeCsvEmptyString() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-empty-fields.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    assertTrue(eventLine.contains(",,"));
  }

  @Test
  public void testEscapeCsvNull() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-null.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    assertTrue(Files.exists(filePath));
  }

  @Test
  public void testExportMultipleEvents() throws Exception {
    testCalendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 6, 14, 0),
        LocalDateTime.of(2025, 5, 6, 15, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-multiple.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertEquals(3, lines.size());
    assertTrue(lines.get(1).contains("Event1"));
    assertTrue(lines.get(2).contains("Event2"));
  }

  @Test
  public void testDateTimeFormatting() throws Exception {
    testCalendar.createAndAddEvent("Test", LocalDateTime.of(2025, 1, 9, 9, 5),
        LocalDateTime.of(2025, 1, 9, 10, 5), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-format.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    assertTrue(eventLine.contains("01/09/2025"));
    assertTrue(eventLine.contains("09:05 AM"));
    assertTrue(eventLine.contains("10:05 AM"));
  }

  @Test
  public void testPmTimeFormatting() throws Exception {
    testCalendar.createAndAddEvent("Afternoon", LocalDateTime.of(2025, 5, 5, 14, 30),
        LocalDateTime.of(2025, 5, 5, 16, 45), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-pm.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    assertTrue(eventLine.contains("02:30 PM"));
    assertTrue(eventLine.contains("04:45 PM"));
  }

  @Test
  public void testAllDayEventEmptyTimes() throws Exception {
    testCalendar.createAndAddEvent("Holiday", LocalDateTime.of(2025, 7, 4, 8, 0),
        LocalDateTime.of(2025, 7, 4, 17, 0), true);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-allday-empty.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);

    String[] fields = eventLine.split(",", -1);
    assertEquals("", fields[2]);
    assertEquals("", fields[4]);
  }

  @Test
  public void testTimedEventHasTimes() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-timed-has-times.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);

    String[] fields = eventLine.split(",", -1);
    assertFalse(fields[2].isEmpty());
    assertFalse(fields[4].isEmpty());
  }

  @Test
  public void testEscapeCommaInSubject() throws Exception {
    testCalendar.createAndAddEvent("Meeting, Team Sync", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-escape-comma.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertTrue(lines.get(1).startsWith("\"Meeting, Team Sync\""));
  }

  @Test
  public void testEscapeQuoteInDescription() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description",
        "Discuss \"Project Alpha\"", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-escape-quote.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertTrue(lines.get(1).contains("\"\"Project Alpha\"\""));
  }

  @Test
  public void testEscapeNewlineInLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        "Building A\nFloor 3", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-escape-newline.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("\"Building A\nFloor 3\""));
  }

  @Test
  public void testNoEscapeForSimpleText() throws Exception {
    testCalendar.createAndAddEvent("SimpleMeeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("SimpleMeeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description",
        "Simple description", EditSettings.SINGLE);

    testCalendar.editEvent("SimpleMeeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location",
        "Room101", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-no-escape.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    assertFalse(eventLine.contains("\"SimpleMeeting\""));
    assertFalse(eventLine.contains("\"Simple description\""));
    assertFalse(eventLine.contains("\"Room101\""));
  }

  @Test
  public void testEmptyDescriptionAndLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-empty-opt.csv");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);
    String[] fields = eventLine.split(",", -1);
    assertEquals("", fields[6]);
    assertEquals("", fields[7]);
  }

  @Test
  public void testReturnsAbsolutePath() throws Exception {
    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-absolute.csv");
    filesToCleanup.add(filePath);


    String result = exporter.export(events, filePath, testCalendar);
    assertNotNull(result);
    assertTrue(result.contains("test-absolute.csv"));
    Path resultPath = Paths.get(result);
    assertTrue(resultPath.isAbsolute());
  }

  @Test
  public void testEscapeCsvWithNullValue() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-null-fields.csv");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    String eventLine = lines.get(1);

    String[] fields = eventLine.split(",", -1);
    assertEquals("", fields[6]);
    assertEquals("", fields[7]);
  }

  @Test
  public void testEscapeCsvWithEmptyDescription() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "description", "",
        EditSettings.SINGLE);

    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0), "location", "",
        EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-empty-strings.csv");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    List<String> lines = Files.readAllLines(filePath);
    assertTrue(lines.get(1).contains(",,"));
  }

}