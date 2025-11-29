package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.controller.commands.CreateEventCommand;
import calendar.controller.commands.EditEventCommand;
import calendar.controller.commands.ExportCommand;
import calendar.controller.commands.PrintEventsCommand;
import calendar.controller.commands.ShowStatusCommand;
import calendar.model.EditSettings;
import calendar.model.EventStatus;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Command implementations.
 */
public class CommandsTest {

  private MockCalendarManager mockManager;
  private MockView mockView;

  /**
   * Sets up the mock manager and view before each test.
   */
  @Before
  public void setUp() {
    mockManager = new MockCalendarManager();
    mockView = new MockView();
  }

  @Test
  public void testCreateSingleTimedEvent() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 5, 11, 0);

    CreateEventCommand cmd = new CreateEventCommand("Meeting", start, end);
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.addEventCallCount);
    assertNotNull(mockCal.lastAddedEvent);
    Assert.assertEquals("Meeting", mockCal.lastAddedEvent.getSubject());
    Assert.assertEquals(start, mockCal.lastAddedEvent.getStartDateTime());
    Assert.assertEquals(end, mockCal.lastAddedEvent.getEndDateTime());
    assertFalse(mockCal.lastAddedEvent.isAllDayEvent());

    Assert.assertEquals(1, mockView.displayEventCreatedCallCount);
    Assert.assertEquals("Meeting", mockView.lastEventCreated);
  }

  @Test
  public void testCreateSingleAllDayEvent() throws Exception {
    LocalDate date = LocalDate.of(2025, 5, 5);

    CreateEventCommand cmd = new CreateEventCommand("Holiday", date);
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.addEventCallCount);
    assertTrue(mockCal.lastAddedEvent.isAllDayEvent());
    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 8, 0),
        mockCal.lastAddedEvent.getStartDateTime());
    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 17, 0),
        mockCal.lastAddedEvent.getEndDateTime());

    Assert.assertEquals(1, mockView.displayEventCreatedCallCount);
  }

  @Test
  public void testCreateTimedSeriesWithOccurrences() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 5, 9, 30);
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);

    CreateEventCommand cmd = new CreateEventCommand("Standup", start, end, weekdays, 5);
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(0, mockCal.addEventCallCount);
    Assert.assertEquals(1, mockCal.createEventSeriesCallCount);
    Assert.assertEquals(0, mockCal.createEventSeriesTillCallCount);

    Assert.assertEquals(1, mockView.displayEventCreatedCallCount);
    Assert.assertEquals("Standup", mockView.lastEventCreated);
  }

  @Test
  public void testCreateTimedSeriesUntilDate() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 14, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 5, 15, 0);
    LocalDate endDate = LocalDate.of(2025, 5, 31);
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    CreateEventCommand cmd = new CreateEventCommand("Review", start, end, weekdays, endDate);
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(0, mockCal.addEventCallCount);
    Assert.assertEquals(0, mockCal.createEventSeriesCallCount);
    Assert.assertEquals(1, mockCal.createEventSeriesTillCallCount);
  }

  @Test
  public void testCreateAllDaySeriesWithOccurrences() throws Exception {
    LocalDate startDate = LocalDate.of(2025, 5, 3);
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    CreateEventCommand cmd = new CreateEventCommand("Weekend", startDate, weekdays, 4);
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(0, mockCal.addEventCallCount);
    Assert.assertEquals(0, mockCal.createEventSeriesCallCount);
    Assert.assertEquals(1, mockCal.createAllDayEventSeriesCallCount);
    Assert.assertEquals(0, mockCal.createAllDayEventSeriesTillCallCount);
  }

  @Test
  public void testCreateAllDaySeriesUntilDate() throws Exception {
    LocalDate startDate = LocalDate.of(2025, 5, 5);
    LocalDate endDate = LocalDate.of(2025, 5, 26);
    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY);

    CreateEventCommand cmd = new CreateEventCommand("Gym", startDate, weekdays, endDate);
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(0, mockCal.createAllDayEventSeriesCallCount);
    Assert.assertEquals(1, mockCal.createAllDayEventSeriesTillCallCount);
  }

  @Test
  public void testEditEventSubject() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "subject",
        "Updated Meeting",
        EditSettings.SINGLE
    );

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.editEventCallCount);
    Assert.assertEquals("Meeting", mockCal.lastEditedSubject);
    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 10, 0),
        mockCal.lastEditedStartDateTime);
    Assert.assertEquals("subject", mockCal.lastEditedProperty);
    Assert.assertEquals("Updated Meeting", mockCal.lastEditedNewValue);
    Assert.assertEquals(EditSettings.SINGLE, mockCal.lastEditScope);

    Assert.assertEquals(1, mockView.displayEventEditedCallCount);
    Assert.assertEquals("Meeting", mockView.lastEventEdited);
  }

  @Test
  public void testEditEventStart() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "start",
        LocalDateTime.of(2025, 5, 5, 10, 30),
        EditSettings.SINGLE
    );

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals("start", mockCal.lastEditedProperty);
    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 10, 30),
        mockCal.lastEditedNewValue);
  }

  @Test
  public void testEditEventEnd() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "end",
        LocalDateTime.of(2025, 5, 5, 11, 30),
        EditSettings.SINGLE
    );

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals("end", mockCal.lastEditedProperty);
    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 11, 30),
        mockCal.lastEditedNewValue);
  }

  @Test
  public void testEditEventDescription() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "description",
        "Important meeting",
        EditSettings.SINGLE
    );

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals("description", mockCal.lastEditedProperty);
    Assert.assertEquals("Important meeting", mockCal.lastEditedNewValue);
  }

  @Test
  public void testEditEventLocation() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "location",
        "Room 301",
        EditSettings.SINGLE
    );

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals("location", mockCal.lastEditedProperty);
    Assert.assertEquals("Room 301", mockCal.lastEditedNewValue);
  }

  @Test
  public void testEditEventStatus() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "status",
        EventStatus.PRIVATE,
        EditSettings.SINGLE
    );

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals("status", mockCal.lastEditedProperty);
    Assert.assertEquals(EventStatus.PRIVATE, mockCal.lastEditedNewValue);
  }

  @Test
  public void testEditWithForwardScope() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Series",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "subject",
        "Updated",
        EditSettings.FORWARD
    );

    cmd.execute(mockManager, mockView);

    Assert.assertEquals(EditSettings.FORWARD,
        mockManager.getMockCalendar().lastEditScope);
  }

  @Test
  public void testEditWithAllEventsScope() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "Series",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "subject",
        "Updated",
        EditSettings.ALL_EVENTS
    );

    cmd.execute(mockManager, mockView);

    Assert.assertEquals(EditSettings.ALL_EVENTS,
        mockManager.getMockCalendar().lastEditScope);
  }

  @Test
  public void testPrintEventsOnDate() {
    PrintEventsCommand cmd = new PrintEventsCommand(LocalDate.of(2025, 5, 5));
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.getEventOnDateCallCount);
    Assert.assertEquals(0, mockCal.getEventsInRangeCallCount);
    Assert.assertEquals(1, mockView.displayEventsOnDateCallCount);
    Assert.assertEquals(0, mockView.displayEventsInRangeCallCount);
  }

  @Test
  public void testPrintEventsInRange() {
    PrintEventsCommand cmd = new PrintEventsCommand(
        LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 31, 23, 59)
    );
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(0, mockCal.getEventOnDateCallCount);
    Assert.assertEquals(1, mockCal.getEventsInRangeCallCount);
    Assert.assertEquals(0, mockView.displayEventsOnDateCallCount);
    Assert.assertEquals(1, mockView.displayEventsInRangeCallCount);
  }

  @Test
  public void testShowStatusBusy() {
    mockManager.getMockCalendar().isBusyReturnValue = true;

    ShowStatusCommand cmd = new ShowStatusCommand(
        LocalDateTime.of(2025, 5, 5, 10, 30));
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.isBusyCallCount);
    Assert.assertEquals(1, mockView.displayBusyStatusCallCount);
    assertTrue(mockView.lastBusyStatus);
  }

  @Test
  public void testShowStatusAvailable() {
    mockManager.getMockCalendar().isBusyReturnValue = false;

    ShowStatusCommand cmd = new ShowStatusCommand(
        LocalDateTime.of(2025, 5, 5, 10, 30));
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.isBusyCallCount);
    assertFalse(mockView.lastBusyStatus);
  }

  @Test
  public void testShowStatusAtDifferentTimes() {
    ShowStatusCommand cmd1 = new ShowStatusCommand(
        LocalDateTime.of(2025, 5, 5, 0, 0));
    ShowStatusCommand cmd2 = new ShowStatusCommand(
        LocalDateTime.of(2025, 5, 5, 12, 0));
    ShowStatusCommand cmd3 = new ShowStatusCommand(
        LocalDateTime.of(2025, 5, 5, 23, 59));

    cmd1.execute(mockManager, mockView);
    cmd2.execute(mockManager, mockView);
    cmd3.execute(mockManager, mockView);

    Assert.assertEquals(3, mockManager.getMockCalendar().isBusyCallCount);
  }

  @Test
  public void testExportIcalWithMock() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("test.ical", mockExporter);

    cmd.execute(mockManager, mockView);

    Assert.assertEquals(1, mockExporter.exportCallCount);
    Assert.assertEquals(1, mockManager.getMockCalendar().getAllEventsCallCount);
    Assert.assertEquals(1, mockView.displayExportSuccessCallCount);
  }

  @Test
  public void testExportAppendsExtensionWhenMissing() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("noextension", mockExporter);

    cmd.execute(mockManager, mockView);

    assertTrue(mockExporter.lastFilePath.toString().endsWith(".csv"));
    Assert.assertEquals(1, mockExporter.exportCallCount);
  }

  @Test
  public void testExportDoesNotAppendExtensionWhenPresent() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("hasextension.csv", mockExporter);

    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".csv"));
    assertFalse(path.contains(".csv.csv"));
  }

  @Test
  public void testExportWithUpperCaseExtension() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("UPPERCASE.CSV", mockExporter);

    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertFalse(path.contains(".CSV.csv"));
    assertFalse(path.contains(".csv.csv"));
  }

  @Test
  public void testExportWithMixedCaseExtension() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("file.CsV", mockExporter);

    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertFalse(path.contains(".csv.csv"));
    assertFalse(path.contains(".CsV.csv"));
  }

  @Test
  public void testExportWithPathInFilename() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("output/data/file.csv", mockExporter);

    cmd.execute(mockManager, mockView);

    assertTrue(mockExporter.lastFilePath.toString().contains("output"));
    assertTrue(mockExporter.lastFilePath.toString().contains("data"));
  }

  @Test
  public void testAllSixCreateEventConstructors() throws Exception {
    CreateEventCommand cmd1 = new CreateEventCommand(
        "Test1",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0)
    );
    cmd1.execute(mockManager, mockView);
    Assert.assertEquals(1, mockManager.getMockCalendar().addEventCallCount);

    mockManager.reset();
    mockView.reset();

    CreateEventCommand cmd2 = new CreateEventCommand(
        "Test2",
        LocalDate.of(2025, 5, 5)
    );
    cmd2.execute(mockManager, mockView);
    Assert.assertEquals(1, mockManager.getMockCalendar().addEventCallCount);

    mockManager.reset();
    mockView.reset();

    CreateEventCommand cmd3 = new CreateEventCommand(
        "Test3",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0),
        Set.of(DayOfWeek.MONDAY),
        3
    );
    cmd3.execute(mockManager, mockView);
    Assert.assertEquals(1, mockManager.getMockCalendar().createEventSeriesCallCount);

    mockManager.reset();
    mockView.reset();

    CreateEventCommand cmd4 = new CreateEventCommand(
        "Test4",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0),
        Set.of(DayOfWeek.MONDAY),
        LocalDate.of(2025, 5, 31)
    );
    cmd4.execute(mockManager, mockView);
    Assert.assertEquals(1, mockManager.getMockCalendar().createEventSeriesTillCallCount);

    mockManager.reset();
    mockView.reset();

    CreateEventCommand cmd5 = new CreateEventCommand(
        "Test5",
        LocalDate.of(2025, 5, 5),
        Set.of(DayOfWeek.MONDAY),
        3
    );
    cmd5.execute(mockManager, mockView);
    Assert.assertEquals(1, mockManager.getMockCalendar().createAllDayEventSeriesCallCount);

    mockManager.reset();
    mockView.reset();

    CreateEventCommand cmd6 = new CreateEventCommand(
        "Test6",
        LocalDate.of(2025, 5, 5),
        Set.of(DayOfWeek.MONDAY),
        LocalDate.of(2025, 5, 31)
    );
    cmd6.execute(mockManager, mockView);
    Assert.assertEquals(1, mockManager.getMockCalendar().createAllDayEventSeriesTillCallCount);
  }

  @Test
  public void testAllThreeEditScopes() throws Exception {
    EditEventCommand cmd1 = new EditEventCommand(
        "M", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "U",
        EditSettings.SINGLE
    );
    cmd1.execute(mockManager, mockView);
    Assert.assertEquals(EditSettings.SINGLE, mockManager.getMockCalendar().lastEditScope);

    mockManager.reset();

    EditEventCommand cmd2 = new EditEventCommand(
        "M", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "U",
        EditSettings.FORWARD
    );
    cmd2.execute(mockManager, mockView);
    Assert.assertEquals(EditSettings.FORWARD, mockManager.getMockCalendar().lastEditScope);

    mockManager.reset();

    EditEventCommand cmd3 = new EditEventCommand(
        "M", LocalDateTime.of(2025, 5, 5, 10, 0), "subject", "U",
        EditSettings.ALL_EVENTS
    );
    cmd3.execute(mockManager, mockView);
    Assert.assertEquals(EditSettings.ALL_EVENTS, mockManager.getMockCalendar().lastEditScope);
  }

  @Test
  public void testPrintEventsDateVsRange() {
    PrintEventsCommand cmd1 = new PrintEventsCommand(LocalDate.of(2025, 5, 5));
    cmd1.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.getEventOnDateCallCount);
    Assert.assertEquals(0, mockCal.getEventsInRangeCallCount);

    mockManager.reset();
    mockView.reset();

    PrintEventsCommand cmd2 = new PrintEventsCommand(
        LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 31, 23, 59)
    );
    cmd2.execute(mockManager, mockView);

    mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(0, mockCal.getEventOnDateCallCount);
    Assert.assertEquals(1, mockCal.getEventsInRangeCallCount);
  }

  @Test
  public void testExportCallsGetAllEvents() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("test.csv", mockExporter);

    cmd.execute(mockManager, mockView);

    Assert.assertEquals(1, mockManager.getMockCalendar().getAllEventsCallCount);
  }

  @Test
  public void testMultipleCommandExecutions() throws Exception {
    CreateEventCommand createCmd = new CreateEventCommand(
        "Event",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0)
    );

    createCmd.execute(mockManager, mockView);
    createCmd.execute(mockManager, mockView);
    createCmd.execute(mockManager, mockView);

    Assert.assertEquals(3, mockManager.getMockCalendar().addEventCallCount);
    Assert.assertEquals(3, mockView.displayEventCreatedCallCount);
  }

  @Test
  public void testCommandsWithDifferentSubjects() throws Exception {
    CreateEventCommand cmd1 = new CreateEventCommand(
        "Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0)
    );
    CreateEventCommand cmd2 = new CreateEventCommand(
        "Lunch",
        LocalDateTime.of(2025, 5, 5, 12, 0),
        LocalDateTime.of(2025, 5, 5, 13, 0)
    );

    cmd1.execute(mockManager, mockView);
    cmd2.execute(mockManager, mockView);

    Assert.assertEquals(2, mockManager.getMockCalendar().addEventCallCount);
  }

  @Test
  public void testEditCommandPreservesEventSubject() throws Exception {
    EditEventCommand cmd = new EditEventCommand(
        "OriginalSubject",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "location",
        "New Location",
        EditSettings.SINGLE
    );

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals("OriginalSubject", mockCal.lastEditedSubject);
    Assert.assertEquals("OriginalSubject", mockView.lastEventEdited);
  }

  @Test(expected = IllegalStateException.class)
  public void testCreateEventNoActiveCalendar() throws Exception {
    mockManager.mockCalendar = null;

    CreateEventCommand cmd = new CreateEventCommand("Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 0));

    cmd.execute(mockManager, mockView);
  }

  @Test(expected = IllegalStateException.class)
  public void testEditEventNoActiveCalendar() throws Exception {
    mockManager.mockCalendar = null;

    EditEventCommand cmd = new EditEventCommand("Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "subject",
        "Updated",
        EditSettings.SINGLE);

    cmd.execute(mockManager, mockView);
  }

  @Test(expected = IllegalStateException.class)
  public void testPrintEventsNoActiveCalendar() {
    mockManager.mockCalendar = null;

    PrintEventsCommand cmd = new PrintEventsCommand(LocalDate.of(2025, 5, 5));
    cmd.execute(mockManager, mockView);
  }

  @Test(expected = IllegalStateException.class)
  public void testShowStatusNoActiveCalendar() {
    mockManager.mockCalendar = null;

    ShowStatusCommand cmd = new ShowStatusCommand(LocalDateTime.of(2025, 5, 5, 10, 0));
    cmd.execute(mockManager, mockView);
  }

  @Test
  public void testCreateEventAllDayWithNullEndTime() throws Exception {
    CreateEventCommand cmd = new CreateEventCommand("Holiday", LocalDate.of(2025, 5, 5));
    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    assertTrue(mockCal.lastAddedEvent.isAllDayEvent());
    assertEquals(LocalDateTime.of(2025, 5, 5, 8, 0), mockCal.lastAddedEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2025, 5, 5, 17, 0), mockCal.lastAddedEvent.getEndDateTime());
  }

  @Test
  public void testCreateEventTimedWithExplicitEndTime() throws Exception {
    CreateEventCommand cmd = new CreateEventCommand("Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        LocalDateTime.of(2025, 5, 5, 11, 30));

    cmd.execute(mockManager, mockView);

    MockCalendar mockCal = mockManager.getMockCalendar();
    assertEquals(LocalDateTime.of(2025, 5, 5, 11, 30),
        mockCal.lastAddedEvent.getEndDateTime());
  }

  @Test
  public void testCreateAllDaySeriesWithOccurrencesPath() throws Exception {
    CreateEventCommand cmd = new CreateEventCommand("Weekend",
        LocalDate.of(2025, 5, 3),
        Set.of(DayOfWeek.SATURDAY),
        4);

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.getMockCalendar().createAllDayEventSeriesCallCount);
  }

  @Test
  public void testCreateAllDaySeriesTillPath() throws Exception {
    CreateEventCommand cmd = new CreateEventCommand("Gym",
        LocalDate.of(2025, 5, 5),
        Set.of(DayOfWeek.MONDAY),
        LocalDate.of(2025, 5, 26));

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.getMockCalendar().createAllDayEventSeriesTillCallCount);
  }

  @Test
  public void testCreateTimedSeriesWithOccurrencesPath() throws Exception {
    CreateEventCommand cmd = new CreateEventCommand("Standup",
        LocalDateTime.of(2025, 5, 5, 9, 0),
        LocalDateTime.of(2025, 5, 5, 9, 30),
        Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
        5);

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.getMockCalendar().createEventSeriesCallCount);
  }

  @Test
  public void testCreateTimedSeriesTillPath() throws Exception {
    CreateEventCommand cmd = new CreateEventCommand("Review",
        LocalDateTime.of(2025, 5, 5, 14, 0),
        LocalDateTime.of(2025, 5, 5, 15, 0),
        Set.of(DayOfWeek.FRIDAY),
        LocalDate.of(2025, 5, 30));

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.getMockCalendar().createEventSeriesTillCallCount);
  }

  @Test
  public void testPrintEventsRangePath() {
    PrintEventsCommand cmd = new PrintEventsCommand(
        LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 31, 23, 59));

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.getMockCalendar().getEventsInRangeCallCount);
    assertEquals(1, mockView.displayEventsInRangeCallCount);
  }

  @Test
  public void testPrintEventsDatePath() {
    PrintEventsCommand cmd = new PrintEventsCommand(LocalDate.of(2025, 5, 5));

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.getMockCalendar().getEventOnDateCallCount);
    assertEquals(1, mockView.displayEventsOnDateCallCount);
  }

  @Test(expected = IllegalStateException.class)
  public void testExportNoActiveCalendar() throws Exception {
    mockManager.mockCalendar = null;

    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("test.csv", mockExporter);
    cmd.execute(mockManager, mockView);
  }

  @Test
  public void testExportIcalFormat() throws Exception {
    MockIcalExporter mockIcalExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("test.ical", mockIcalExporter);

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockIcalExporter.exportCallCount);
    assertEquals(1, mockManager.getMockCalendar().getAllEventsCallCount);
    assertEquals(1, mockView.displayExportSuccessCallCount);
  }

  @Test
  public void testExportCsvFormatExplicit() throws Exception {
    MockCsvExporter mockCsvExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("test.csv", mockCsvExporter);

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockCsvExporter.exportCallCount);
    assertEquals(1, mockManager.getMockCalendar().getAllEventsCallCount);
    assertEquals(1, mockView.displayExportSuccessCallCount);
  }

  @Test
  public void testCsvExportWithCsvExtensionDoesNotAppend() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("file.csv", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("file.csv"));
    assertFalse(path.endsWith(".csv.csv"));
  }

  @Test
  public void testCsvExportWithUpperCaseCsvDoesNotAppend() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("FILE.CSV", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("FILE.CSV"));
    assertFalse(path.endsWith(".CSV.csv"));
  }

  @Test
  public void testCsvExportWithMixedCaseCsvDoesNotAppend() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("file.CsV", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertFalse(path.endsWith(".CsV.csv"));
  }

  @Test
  public void testCsvExportNoExtensionAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("noextension", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("noextension.csv"));
  }

  @Test
  public void testCsvExportWithTxtExtensionAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("file.txt", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".txt.csv"));
  }

  @Test
  public void testCsvExportWithJsonExtensionAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("data.json", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".json.csv"));
  }

  @Test
  public void testCsvExportWithXmlExtensionAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("export.xml", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".xml.csv"));
  }

  @Test
  public void testCsvExportWithPdfExtensionAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("document.pdf", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".pdf.csv"));
  }

  @Test
  public void testCsvExportWithOnlyDotAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("file.", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".csv"));
  }

  @Test
  public void testCsvExportWithCsvInMiddleAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("csv.file.txt", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".txt.csv"));
  }

  @Test
  public void testCsvExportWithPathAndCsvDoesNotAppend() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("output/data/report.csv", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("report.csv"));
    assertFalse(path.endsWith(".csv.csv"));
  }

  @Test
  public void testCsvExportWithPathNoExtensionAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("output/data/myfile", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("myfile.csv"));
  }

  @Test
  public void testCsvExportWithMultipleDotsCsvDoesNotAppend() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("file.backup.csv", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("file.backup.csv"));
    assertFalse(path.endsWith(".csv.csv"));
  }

  @Test
  public void testCsvExportWithMultipleDotsNoRecognizedExtAppendsCsv() throws Exception {
    MockCsvExporter mockExporter = new MockCsvExporter();
    ExportCommand cmd = new ExportCommand("file.backup.old", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".old.csv"));
  }

  @Test
  public void testIcalExportWithIcalExtensionDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("file.ical", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("file.ical"));
    assertFalse(path.endsWith(".ical.csv"));
  }

  @Test
  public void testIcalExportWithIcsExtensionDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("file.ics", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("file.ics"));
    assertFalse(path.endsWith(".ics.csv"));
  }

  @Test
  public void testIcalExportWithUpperCaseIcalDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("FILE.ICAL", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("FILE.ICAL"));
    assertFalse(path.endsWith(".ICAL.csv"));
  }

  @Test
  public void testIcalExportWithUpperCaseIcsDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("FILE.ICS", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("FILE.ICS"));
    assertFalse(path.endsWith(".ICS.csv"));
  }

  @Test
  public void testIcalExportWithMixedCaseIcalDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("file.IcAl", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertFalse(path.endsWith(".IcAl.csv"));
  }

  @Test
  public void testIcalExportWithMixedCaseIcsDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("file.IcS", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertFalse(path.endsWith(".IcS.csv"));
  }

  @Test
  public void testIcalExportWithIcalInMiddleNotAtEnd() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("ical.file.txt", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".txt.csv"));
  }

  @Test
  public void testIcalExportWithIcsInMiddleNotAtEnd() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("ics.file.txt", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith(".txt.csv"));
  }

  @Test
  public void testIcalExportWithPathAndIcalDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("output/calendar.ical", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("calendar.ical"));
    assertFalse(path.endsWith(".ical.csv"));
  }

  @Test
  public void testIcalExportWithPathAndIcsDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("calendars/2025.ics", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("2025.ics"));
    assertFalse(path.endsWith(".ics.csv"));
  }

  @Test
  public void testIcalExportWithMultipleDotsIcalDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("calendar.v2.ical", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("calendar.v2.ical"));
    assertFalse(path.endsWith(".ical.csv"));
  }

  @Test
  public void testExportWithoutCustomExporter() throws Exception {
    ExportCommand cmd = new ExportCommand("test.csv");
    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.getMockCalendar().getAllEventsCallCount);
    assertEquals(1, mockView.displayExportSuccessCallCount);
  }

  @Test
  public void testIcalExportWithMultipleDotsIcsDoesNotAppend() throws Exception {
    MockIcalExporter mockExporter = new MockIcalExporter();
    ExportCommand cmd = new ExportCommand("cal.2025.ics", mockExporter);
    cmd.execute(mockManager, mockView);

    String path = mockExporter.lastFilePath.toString();
    assertTrue(path.endsWith("cal.2025.ics"));
    assertFalse(path.endsWith(".ics.csv"));
  }
}