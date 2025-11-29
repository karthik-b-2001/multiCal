package controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.controller.CalendarController;
import calendar.controller.commands.Command;
import calendar.controller.commands.CopyEventCommand;
import calendar.controller.commands.CopyEventsBetweenCommand;
import calendar.controller.commands.CopyEventsOnDateCommand;
import calendar.controller.commands.CreateCalendarCommand;
import calendar.controller.commands.CreateEventCommand;
import calendar.controller.commands.EditCalendarCommand;
import calendar.controller.commands.EditEventCommand;
import calendar.controller.commands.ExportCommand;
import calendar.controller.commands.PrintEventsCommand;
import calendar.controller.commands.ShowStatusCommand;
import calendar.controller.commands.UseCalendarCommand;
import calendar.controller.utils.CommandParserImpl;
import calendar.model.EventStatus;
import java.time.LocalDateTime;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CommandParser.
 */
public class CommandParserTest {

  private CommandParserImpl parser;

  /**
   * Sets up the CommandParser before each test.
   */
  @Before
  public void setUp() {
    parser = new CommandParserImpl();
  }

  @Test
  public void testParseCreateSingleTimedEvent() {
    Command cmd = parser.parse(
        "create event Meeting from 2025-05-05T10:00 to 2025-05-05T11:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateSingleTimedEventWithQuotes() {
    Command cmd = parser.parse(
        "create event \"Team Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateSingleWordSubject() {
    Command cmd = parser.parse(
        "create event Lunch from 2025-05-05T12:00 to 2025-05-05T13:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateAllDayEvent() {
    Command cmd = parser.parse("create event Holiday on 2025-05-05");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateAllDayEventWithQuotes() {
    Command cmd = parser.parse("create event \"Memorial Day\" on 2025-05-05");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateSeriesWithOccurrences() {
    Command cmd = parser.parse(
        "create event Standup from 2025-05-05T09:00 to 2025-05-05T09:30 "
            + "repeats MWF for 10 times");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateSeriesUntilDate() {
    Command cmd = parser.parse(
        "create event Review from 2025-05-05T14:00 to 2025-05-05T15:00 "
            + "repeats MW until 2025-06-30");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateSeriesWithQuotedSubject() {
    Command cmd = parser.parse(
        "create event \"Daily Standup\" from 2025-05-05T09:00 to 2025-05-05T09:30 "
            + "repeats MTWRF for 20 times");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateAllDaySeriesWithOccurrences() {
    Command cmd = parser.parse(
        "create event Weekend on 2025-05-03 repeats SU for 5 times");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateAllDaySeriesUntilDate() {
    Command cmd = parser.parse(
        "create event Gym on 2025-05-05 repeats MWF until 2025-05-31");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseAllWeekdays() {
    Command cmd = parser.parse(
        "create event Daily from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats MTWRFSU for 7 times");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseMonday() {
    Command cmd = parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats M for 1 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseTuesday() {
    Command cmd = parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats T for 1 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseWednesday() {
    Command cmd = parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats W for 1 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseThursday() {
    Command cmd = parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats R for 1 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseFriday() {
    Command cmd = parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats F for 1 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseSaturday() {
    Command cmd = parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats S for 1 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseSunday() {
    Command cmd = parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats U for 1 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseEditEventSingle() {
    Command cmd = parser.parse(
        "edit event subject Meeting from 2025-05-05T10:00 with \"Team Sync\"");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditEventWithToParameter() {
    Command cmd = parser.parse(
        "edit event subject Meeting from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "with \"Team Sync\"");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditEventsForward() {
    Command cmd = parser.parse(
        "edit events start Standup from 2025-05-07T09:00 with 2025-05-07T09:30");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditSeriesAll() {
    Command cmd = parser.parse(
        "edit series location Review from 2025-05-05T14:00 with \"Room 101\"");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditQuotedSubject() {
    Command cmd = parser.parse(
        "edit event subject \"Team Meeting\" from 2025-05-05T10:00 "
            + "with \"New Subject\"");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditUnquotedSubject() {
    Command cmd = parser.parse(
        "edit event subject Meeting from 2025-05-05T10:00 with Updated");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditDescription() {
    Command cmd = parser.parse(
        "edit event description Meeting from 2025-05-05T10:00 "
            + "with \"Important discussion\"");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditLocation() {
    Command cmd = parser.parse(
        "edit series location Meeting from 2025-05-05T10:00 with \"Room 202\"");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditStatus() {
    Command cmd = parser.parse(
        "edit event status Meeting from 2025-05-05T10:00 with PRIVATE");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditStart() {
    Command cmd = parser.parse(
        "edit event start Meeting from 2025-05-05T10:00 with 2025-05-05T10:30");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditEnd() {
    Command cmd = parser.parse(
        "edit event end Meeting from 2025-05-05T10:00 with 2025-05-05T11:30");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParsePrintEventsOnDate() {
    Command cmd = parser.parse("print events on 2025-05-05");
    assertNotNull(cmd);
    assertTrue(cmd instanceof PrintEventsCommand);
  }

  @Test
  public void testParsePrintEventsInRange() {
    Command cmd = parser.parse(
        "print events from 2025-05-05T00:00 to 2025-05-10T23:59");
    assertNotNull(cmd);
    assertTrue(cmd instanceof PrintEventsCommand);
  }

  @Test
  public void testParseShowStatus() {
    Command cmd = parser.parse("show status on 2025-05-05T10:30");
    assertNotNull(cmd);
    assertTrue(cmd instanceof ShowStatusCommand);
  }

  @Test
  public void testParseShowStatusMidnight() {
    Command cmd = parser.parse("show status on 2025-05-05T00:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof ShowStatusCommand);
  }

  @Test
  public void testParseShowStatusEndOfDay() {
    Command cmd = parser.parse("show status on 2025-05-05T23:59");
    assertNotNull(cmd);
    assertTrue(cmd instanceof ShowStatusCommand);
  }

  @Test
  public void testParseExport() {
    Command cmd = parser.parse("export cal calendar.csv");
    assertNotNull(cmd);
    assertTrue(cmd instanceof ExportCommand);
  }

  @Test
  public void testParseExportNoExtension() {
    Command cmd = parser.parse("export cal myfile");
    assertNotNull(cmd);
    assertTrue(cmd instanceof ExportCommand);
  }

  @Test
  public void testParseExportWithPath() {
    Command cmd = parser.parse("export cal output/calendar.csv");
    assertNotNull(cmd);
    assertTrue(cmd instanceof ExportCommand);
  }

  @Test
  public void testParseExit() {
    Command cmd = parser.parse("exit");
    assertNull(cmd);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidCommand() {
    parser.parse("this is not a valid command");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEmptyCommand() {
    parser.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseNullCommand() {
    parser.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWhitespaceOnly() {
    parser.parse("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseUnknownCommand() {
    parser.parse("delete event Meeting");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateWithoutSubject() {
    parser.parse("create event from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateWithUnclosedQuote() {
    parser.parse(
        "create event \"Meeting from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateWithoutOnOrFrom() {
    parser.parse("create event Test 2025-05-05T10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateMissingSpace() {
    parser.parse("create event Test");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateTimedWithoutTo() {
    parser.parse("create event Test from 2025-05-05T10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateSeriesInvalidRepeatFormat() {
    parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MW");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateSeriesWithoutForOrUntil() {
    parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats MW something");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidWeekdayCharacter() {
    parser.parse(
        "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "repeats XYZ for 3 times");
  }

  @Test(expected = Exception.class)
  public void testParseCreateInvalidDateFormat() {
    parser.parse("create event Test on 2025-13-45");
  }

  @Test(expected = Exception.class)
  public void testParseCreateInvalidTimeFormat() {
    parser.parse("create event Test from 2025-05-05T25:00 to 2025-05-05T26:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditWithoutWith() {
    parser.parse("edit event subject Meeting from 2025-05-05T10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditWithoutFrom() {
    parser.parse("edit event subject Meeting with NewSubject");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditUnclosedQuote() {
    parser.parse(
        "edit event subject \"Meeting from 2025-05-05T10:00 with New");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditInvalidScope() {
    parser.parse(
        "edit something subject Meeting from 2025-05-05T10:00 with New");
  }

  @Test(expected = Exception.class)
  public void testParseEditInvalidDateFormat() {
    parser.parse(
        "edit event subject Meeting from invalid-date with New");
  }

  @Test(expected = Exception.class)
  public void testParseEditInvalidPropertyValueForStart() {
    parser.parse(
        "edit event start Meeting from 2025-05-05T10:00 with invalid-time");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditUnknownProperty() {
    parser.parse(
        "edit event unknown Meeting from 2025-05-05T10:00 with Value");
  }

  @Test(expected = Exception.class)
  public void testParsePrintInvalidDateFormat() {
    parser.parse("print events on 2025-99-99");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsePrintRangeMissingTo() {
    parser.parse("print events from 2025-05-05T00:00");
  }

  @Test(expected = Exception.class)
  public void testParsePrintRangeInvalidStartDate() {
    parser.parse("print events from invalid to 2025-05-10T23:59");
  }

  @Test(expected = Exception.class)
  public void testParsePrintRangeInvalidEndDate() {
    parser.parse("print events from 2025-05-05T00:00 to invalid");
  }

  @Test(expected = Exception.class)
  public void testParseShowStatusInvalidDateTime() {
    parser.parse("show status on invalid-datetime");
  }

  @Test(expected = Exception.class)
  public void testParseShowStatusInvalidTime() {
    parser.parse("show status on 2025-05-05T99:99");
  }

  @Test
  public void testParseSingleCharacterSubject() {
    Command cmd = parser.parse(
        "create event A from 2025-05-05T10:00 to 2025-05-05T11:00");
    assertNotNull(cmd);
  }

  @Test
  public void testParseSubjectWithSpacesInQuotes() {
    Command cmd = parser.parse(
        "create event \"Meeting with Bob\" from 2025-05-05T10:00 "
            + "to 2025-05-05T11:00");
    assertNotNull(cmd);
  }

  @Test
  public void testParseEditWithToAndWithout() {
    Command cmd1 = parser.parse(
        "edit event subject Meeting from 2025-05-05T10:00 with New");
    Command cmd2 = parser.parse(
        "edit event subject Meeting from 2025-05-05T10:00 to 2025-05-05T11:00 "
            + "with New");

    assertNotNull(cmd1);
    assertNotNull(cmd2);
  }

  @Test
  public void testParseLeadingTrailingWhitespace() {
    Command cmd = parser.parse(
        "   create event Test from 2025-05-05T10:00 to 2025-05-05T11:00   ");
    assertNotNull(cmd);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditMissingProperty() {
    parser.parse("edit event Meeting from 2025-05-05T10:00 with Value");
  }

  @Test
  public void testParseAllPropertyTypes() {
    Command cmd1 = parser.parse(
        "edit event subject M from 2025-05-05T10:00 with New");
    Command cmd2 = parser.parse(
        "edit event description M from 2025-05-05T10:00 with Desc");
    Command cmd3 = parser.parse(
        "edit event location M from 2025-05-05T10:00 with Loc");
    assertNotNull(cmd1);
    assertNotNull(cmd2);
    assertNotNull(cmd3);

    Command cmd4 = parser.parse(
        "edit event start M from 2025-05-05T10:00 with 2025-05-05T10:30");
    Command cmd5 = parser.parse(
        "edit event end M from 2025-05-05T10:00 with 2025-05-05T11:30");
    Command cmd6 = parser.parse(
        "edit event status M from 2025-05-05T10:00 with PRIVATE");
    assertNotNull(cmd4);
    assertNotNull(cmd5);
    assertNotNull(cmd6);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditStatusWithInvalidValue() {
    parser.parse(
        "edit event status Meeting from 2025-05-05T10:00 with INVALID_STATUS");
  }

  @Test
  public void testParseExportWithDifferentExtensions() {
    Command cmd1 = parser.parse("export cal file.csv");
    Command cmd2 = parser.parse("export cal file");
    Command cmd3 = parser.parse("export cal file.txt");

    assertNotNull(cmd1);
    assertNotNull(cmd2);
    assertNotNull(cmd3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseExitUpperCase() {
    parser.parse("EXIT");
  }

  @Test
  public void testParseCreateAllDaySeriesUntilDateBranch() {
    Command cmd = parser.parse(
        "create event Holiday on 2025-05-05 repeats MW until 2025-05-31");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateAllDaySeriesInvalidRepeatFormat() {
    parser.parse("create event Holiday on 2025-05-05 repeats MW something");
  }

  @Test
  public void testParseEditSeriesCommand() {
    Command cmd = parser.parse(
        "edit series subject Meeting from 2025-05-05T10:00 with Updated");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditInvalidCommandType() {
    parser.parse("edit something subject Meeting from 2025-05-05T10:00 with New");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditQuotedSubjectMissingFrom() {
    parser.parse("edit event subject \"Meeting\" with NewSubject");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditUnquotedSubjectMissingFrom() {
    parser.parse("edit event subject Meeting with NewSubject");
  }

  @Test
  public void testEditStartActualValueCheck() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "edit event start Meeting from 2025-05-05T10:00 "
        + "with 2025-05-05T11:30\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 11, 30),
        mockManager.getMockCalendar().lastEditedNewValue);
  }

  @Test
  public void testEditEndActualValueCheck() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "edit event end Meeting from 2025-05-05T10:00 "
        + "with 2025-05-05T12:00\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 12, 0),
        mockManager.getMockCalendar().lastEditedNewValue);
  }

  @Test
  public void testEditStatusPrivateActualValueCheck() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "edit event status Meeting from 2025-05-05T10:00 "
        + "with PRIVATE\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    Assert.assertEquals(EventStatus.PRIVATE,
        mockManager.getMockCalendar().lastEditedNewValue);
  }

  @Test
  public void testEditStatusPublicActualValueCheck() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "edit event status Meeting from 2025-05-05T10:00 "
        + "with PUBLIC\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    Assert.assertEquals(EventStatus.PUBLIC,
        mockManager.getMockCalendar().lastEditedNewValue);
  }

  @Test
  public void testWeekdayParsingThroughSeries() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
        + "repeats MTWRFSU for 7 times\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    Assert.assertEquals(1, mockManager.getMockCalendar().createEventSeriesCallCount);
  }

  @Test
  public void testWeekdayParsingMwf() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
        + "repeats MWF for 3 times\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    assertNotNull(mockCal.lastWeekdaysUsed);
    Assert.assertEquals(3, mockCal.lastWeekdaysUsed.size());
    assertTrue(mockCal.lastWeekdaysUsed.contains(java.time.DayOfWeek.MONDAY));
    assertTrue(mockCal.lastWeekdaysUsed.contains(java.time.DayOfWeek.WEDNESDAY));
    assertTrue(mockCal.lastWeekdaysUsed.contains(java.time.DayOfWeek.FRIDAY));
  }

  @Test
  public void testWeekdayParsingAllDays() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "create event Daily from 2025-05-05T10:00 to 2025-05-05T11:00 "
        + "repeats MTWRFSU for 7 times\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    Assert.assertEquals(7, mockManager.getMockCalendar().lastWeekdaysUsed.size());
  }

  @Test
  public void testWeekdayParsingSingleDay() {
    MockCalendarManager mockManager = new MockCalendarManager();
    MockView mockView = new MockView();

    String commands = "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00 "
        + "repeats R for 2 times\nexit\n";
    Scanner scanner = new Scanner(commands);

    new CalendarController(mockManager, mockView, scanner, false).run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.lastWeekdaysUsed.size());
    assertTrue(mockCal.lastWeekdaysUsed.contains(java.time.DayOfWeek.THURSDAY));
  }

  @Test
  public void testParseCreateCalendar() {
    Command cmd = parser.parse("create calendar --name Personal --timezone America/New_York");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CreateCalendarCommand);
  }

  @Test
  public void testParseCreateCalendarDifferentTimezones() {
    Command cmd1 = parser.parse("create calendar --name Cal1 --timezone Europe/Paris");
    Command cmd2 = parser.parse("create calendar --name Cal2 --timezone Asia/Tokyo");
    Command cmd3 = parser.parse("create calendar --name Cal3 --timezone Australia/Sydney");
    assertNotNull(cmd1);
    assertNotNull(cmd2);
    assertNotNull(cmd3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateCalendarMissingName() {
    parser.parse("create calendar --timezone America/New_York");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateCalendarMissingTimezone() {
    parser.parse("create calendar --name Personal");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCreateCalendarInvalidTimezone() {
    parser.parse("create calendar --name Personal --timezone Invalid/Timezone");
  }

  @Test
  public void testParseUseCalendar() {
    Command cmd = parser.parse("use calendar --name Personal");
    assertNotNull(cmd);
    assertTrue(cmd instanceof UseCalendarCommand);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseUseCalendarMissingName() {
    parser.parse("use calendar");
  }

  @Test
  public void testParseEditCalendarName() {
    Command cmd = parser.parse("edit calendar --name Personal --property name Work");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditCalendarCommand);
  }

  @Test
  public void testParseEditCalendarTimezone() {
    Command cmd = parser.parse("edit calendar --name Personal --property timezone Europe/Paris");
    assertNotNull(cmd);
    assertTrue(cmd instanceof EditCalendarCommand);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditCalendarMissingName() {
    parser.parse("edit calendar --property name NewName");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditCalendarMissingProperty() {
    parser.parse("edit calendar --name Personal");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditCalendarMissingValue() {
    parser.parse("edit calendar --name Personal --property name");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEditCalendarInvalidTimezone() {
    parser.parse("edit calendar --name Personal --property timezone Invalid/Zone");
  }

  @Test
  public void testParseCopyEvent() {
    Command cmd = parser.parse(
        "copy event Meeting on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventMissingOn() {
    parser.parse("copy event Meeting --target Work to 2025-05-10T14:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventMissingTarget() {
    parser.parse("copy event Meeting on 2025-05-05T10:00 to 2025-05-10T14:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventMissingTo() {
    parser.parse("copy event Meeting on 2025-05-05T10:00 --target Work");
  }

  @Test
  public void testParseCopyEventsOnDate() {
    Command cmd = parser.parse("copy events on 2025-05-05 --target Work to 2025-05-10");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventsOnDateCommand);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventsOnDateMissingTarget() {
    parser.parse("copy events on 2025-05-05 to 2025-05-10");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventsOnDateMissingTo() {
    parser.parse("copy events on 2025-05-05 --target Work");
  }

  @Test
  public void testParseCopyEventsBetween() {
    Command cmd = parser.parse(
        "copy events between 2025-05-01 and 2025-05-31 --target Work to 2025-06-01");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventsBetweenCommand);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventsBetweenMissingAnd() {
    parser.parse("copy events between 2025-05-01 --target Work to 2025-06-01");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventsBetweenMissingTarget() {
    parser.parse("copy events between 2025-05-01 and 2025-05-31 to 2025-06-01");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseCopyEventsBetweenMissingTo() {
    parser.parse("copy events between 2025-05-01 and 2025-05-31 --target Work");
  }

  @Test
  public void testParseExportIcal() {
    Command cmd = parser.parse("export cal calendar.ical");
    assertNotNull(cmd);
    assertTrue(cmd instanceof ExportCommand);
  }

  @Test
  public void testParseCopyEventWithQuotedName() {
    Command cmd = parser.parse(
        "copy event \"Team Meeting\" on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventWithoutQuotes() {
    Command cmd = parser.parse(
        "copy event Meeting on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventVsCopyEvents() {
    Command cmd1 = parser.parse(
        "copy event Single on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    Command cmd2 = parser.parse(
        "copy events on 2025-05-05 --target Work to 2025-05-10");

    assertTrue(cmd1 instanceof CopyEventCommand);
    assertTrue(cmd2 instanceof CopyEventsOnDateCommand);
  }

  @Test
  public void testParseCopyEventPartialMatch() {
    Command cmd = parser.parse(
        "copy event Test on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventWithQuotedEventName() {
    Command cmd = parser.parse(
        "copy event \"Team Meeting\" on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventWithQuotedNameWithSpaces() {
    Command cmd = parser.parse(
        "copy event \"Morning Stand Up\" on 2025-05-05T09:00 --target Work to 2025-05-10T09:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventQuotedNameWithSpecialChars() {
    Command cmd = parser.parse(
        "copy event \"Meeting: Q1 Review\" on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventUnquotedSingleWord() {
    Command cmd = parser.parse(
        "copy event Meeting on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventQuotedVsUnquoted() {
    Command cmd1 = parser.parse(
        "copy event \"Quoted Name\" on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    Command cmd2 = parser.parse(
        "copy event Unquoted on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");

    assertNotNull(cmd1);
    assertNotNull(cmd2);
    assertTrue(cmd1 instanceof CopyEventCommand);
    assertTrue(cmd2 instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventQuotedNameStartsWithQuote() {
    Command cmd = parser.parse(
        "copy event \"Meeting\" on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testParseCopyEventQuotedEmptyName() {
    Command cmd = parser.parse(
        "copy event \"\" on 2025-05-05T10:00 --target Work to 2025-05-10T14:00");
    assertNotNull(cmd);
    assertTrue(cmd instanceof CopyEventCommand);
  }

}