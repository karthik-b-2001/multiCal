package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import calendar.controller.utils.IcalExporter;
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
 * Test class for ICalExporter.
 */
public class IcalExporterTest {

  private IcalExporter exporter;
  private List<Path> filesToCleanup;
  private Calendar testCalendar;

  /**
   * Sets up the test environment before each test case.
   */
  @Before
  public void setUp() {
    exporter = new IcalExporter();
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
        // Ignore cleanup errors
      }
    }
  }

  @Test
  public void testExportEmptyCalendar() throws IOException {
    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-empty.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);
    String result = exporter.export(events, filePath, testCalendar);

    assertTrue(Files.exists(filePath));
    assertTrue(result.contains("test-empty.ical"));

    String content = Files.readString(filePath);
    assertTrue(content.contains("BEGIN:VCALENDAR"));
    assertTrue(content.contains("VERSION:2.0"));
    assertTrue(content.contains("END:VCALENDAR"));
  }

  @Test
  public void testExportSingleEvent() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-single.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("BEGIN:VEVENT"));
    assertTrue(content.contains("SUMMARY:Meeting"));
    assertTrue(content.contains("DTSTART:"));
    assertTrue(content.contains("DTEND:"));
    assertTrue(content.contains("END:VEVENT"));
  }

  @Test
  public void testExportWithDescription() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        "description", "Important meeting", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-desc.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("DESCRIPTION:Important meeting"));
  }

  @Test
  public void testExportWithLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        "location", "Room 301", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-loc.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("LOCATION:Room 301"));
  }

  @Test
  public void testExportPrivateEvent() throws Exception {
    testCalendar.createAndAddEvent("Private", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
    testCalendar.editEvent("Private", LocalDateTime.of(2025, 5, 5, 10, 0),
        "status", EventStatus.PRIVATE, EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-private.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("CLASS:PRIVATE"));
  }

  @Test
  public void testExportPublicEvent() throws Exception {
    testCalendar.createAndAddEvent("Public", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-public.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("CLASS:PUBLIC"));
  }

  @Test
  public void testExportAllDayEvent() throws Exception {
    testCalendar.createAndAddEvent("Holiday", LocalDateTime.of(2025, 12, 25, 8, 0),
        LocalDateTime.of(2025, 12, 25, 17, 0), true);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-allday.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("X-MICROSOFT-CDO-ALLDAYEVENT:TRUE"));
  }

  @Test
  public void testExportMultipleEvents() throws Exception {
    testCalendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
    testCalendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 6, 14, 0),
        LocalDateTime.of(2025, 5, 6, 15, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-multiple.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    int eventCount = content.split("BEGIN:VEVENT").length - 1;
    assertEquals(2, eventCount);
  }

  @Test
  public void testExportEscapesComma() throws Exception {
    testCalendar.createAndAddEvent("Meeting, Discussion", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-comma.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("SUMMARY:Meeting\\, Discussion"));
  }

  @Test
  public void testExportEscapesSemicolon() throws Exception {
    testCalendar.createAndAddEvent("Meeting; Important", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-semicolon.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("SUMMARY:Meeting\\; Important"));
  }

  @Test
  public void testExportEscapesNewline() throws Exception {
    testCalendar.createAndAddEvent("Line1\nLine2", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-newline.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("SUMMARY:Line1\\nLine2"));
  }

  @Test
  public void testExportEscapesBackslash() throws Exception {
    testCalendar.createAndAddEvent("Path\\Name", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-backslash.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("SUMMARY:Path\\\\Name"));
  }

  @Test
  public void testExportHasUid() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-uid.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("UID:"));
    assertTrue(content.contains("@calendar-app"));
  }

  @Test
  public void testExportHasDtsStamp() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-dtstamp.ical");
    filesToCleanup.add(filePath);


    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("DTSTAMP:"));
  }

  @Test
  public void testExportCalendarProperties() throws Exception {
    Calendar europeCal = new CalendarImpl("TestCalendar", ZoneId.of("Europe/Paris"));
    List<Event> events = europeCal.getAllEvents();
    Path filePath = Paths.get("test-calprops.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, europeCal);

    String content = Files.readString(filePath);
    assertTrue(content.contains("X-WR-CALNAME:TestCalendar"));
    assertTrue(content.contains("X-WR-TIMEZONE:Europe/Paris"));
    assertTrue(content.contains("PRODID:-//Calendar Application//EN"));
    assertTrue(content.contains("CALSCALE:GREGORIAN"));
    assertTrue(content.contains("METHOD:PUBLISH"));
  }

  @Test
  public void testExportDateTimeFormatUtc() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-utc.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("DTSTART:20250505T"));
    assertTrue(content.contains("Z"));
  }

  @Test
  public void testExportReturnsAbsolutePath() throws Exception {
    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-absolute.ical");
    filesToCleanup.add(filePath);

    String result = exporter.export(events, filePath, testCalendar);

    assertTrue(result.contains("test-absolute.ical"));
    Path resultPath = Paths.get(result);
    assertTrue(resultPath.isAbsolute());
  }

  @Test
  public void testExportEventWithoutOptionalFields() throws Exception {
    testCalendar.createAndAddEvent("Simple", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-minimal.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("SUMMARY:Simple"));
    assertTrue(content.contains("CLASS:PUBLIC"));
  }

  @Test
  public void testExportMultipleEventsEachHasUid() throws Exception {
    testCalendar.createAndAddEvent("Event1", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
    testCalendar.createAndAddEvent("Event2", LocalDateTime.of(2025, 5, 6, 14, 0),
        LocalDateTime.of(2025, 5, 6, 15, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-uids.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);
    String content = Files.readString(filePath);
    int uidCount = content.split("UID:").length - 1;
    assertEquals(2, uidCount);
  }

  @Test
  public void testExportNoDescriptionIfNotPresent() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-no-desc.ical");
    filesToCleanup.add(filePath);
    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    int descCount = content.split("DESCRIPTION:").length - 1;
    assertEquals(0, descCount);
  }

  @Test
  public void testExportNoLocationIfNotPresent() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-no-loc.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    int locCount = content.split("LOCATION:").length - 1;
    assertEquals(0, locCount);
  }

  @Test
  public void testExportWithNullDescription() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-null-desc.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("BEGIN:VEVENT"));
    assertTrue(content.contains("SUMMARY:Meeting"));
  }

  @Test
  public void testExportWithEmptyDescription() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        "description", "", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-empty-desc.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("DESCRIPTION:"));
  }

  @Test
  public void testExportWithEmptyLocation() throws Exception {
    testCalendar.createAndAddEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);
    testCalendar.editEvent("Meeting", LocalDateTime.of(2025, 5, 5, 10, 0),
        "location", "", EditSettings.SINGLE);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-empty-loc.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("LOCATION:"));
  }

  @Test
  public void testExportEscapesAllSpecialCharacters() throws Exception {
    testCalendar.createAndAddEvent("Test\\,;\nEvent", LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0), false);

    List<Event> events = testCalendar.getAllEvents();
    Path filePath = Paths.get("test-all-escape.ical");
    filesToCleanup.add(filePath);

    exporter.export(events, filePath, testCalendar);

    String content = Files.readString(filePath);
    assertTrue(content.contains("SUMMARY:Test\\\\\\,\\;\\nEvent"));
  }
}