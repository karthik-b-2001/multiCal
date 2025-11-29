package controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.controller.CalendarController;
import calendar.controller.Controller;
import calendar.model.EditSettings;
import java.time.LocalDateTime;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CalendarController.
 */
public class ControllerTest {

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
  public void testControllerCallsAddEventOnCreate() {
    String commands = "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.addEventCallCount);
    assertNotNull(mockCal.lastAddedEvent);
    Assert.assertEquals("Test", mockCal.lastAddedEvent.getSubject());

    Assert.assertEquals(1, mockView.displayEventCreatedCallCount);
    Assert.assertEquals("Test", mockView.lastEventCreated);
  }

  @Test
  public void testControllerCallsCreateEventSeries() {
    String commands = "create event Daily from 2025-05-05T09:00 to 2025-05-05T09:30 "
        + "repeats MWF for 5 times\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockManager.getMockCalendar().createEventSeriesCallCount);
    Assert.assertEquals(1, mockView.displayEventCreatedCallCount);
  }

  @Test
  public void testControllerCallsCreateEventSeriesTill() {
    String commands = "create event Weekly from 2025-05-05T10:00 to 2025-05-05T11:00 "
        + "repeats M until 2025-05-31\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockManager.getMockCalendar().createEventSeriesTillCallCount);
  }

  @Test
  public void testControllerCallsCreateAllDayEventSeries() {
    String commands = "create event Holiday on 2025-05-05 repeats F for 4 times\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockManager.getMockCalendar().createAllDayEventSeriesCallCount);
  }

  @Test
  public void testControllerCallsCreateAllDayEventSeriesTill() {
    String commands = "create event Weekend on 2025-05-03 repeats SU until 2025-05-31\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockManager.getMockCalendar().createAllDayEventSeriesTillCallCount);
  }

  @Test
  public void testControllerCallsEditEventWithCorrectParameters() {
    String commands = "edit event subject Meeting from 2025-05-05T10:00 with Updated\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.editEventCallCount);
    Assert.assertEquals("Meeting", mockCal.lastEditedSubject);
    Assert.assertEquals(LocalDateTime.of(2025, 5, 5, 10, 0), mockCal.lastEditedStartDateTime);
    Assert.assertEquals("subject", mockCal.lastEditedProperty);
    Assert.assertEquals("Updated", mockCal.lastEditedNewValue);
    Assert.assertEquals(EditSettings.SINGLE, mockCal.lastEditScope);

    Assert.assertEquals(1, mockView.displayEventEditedCallCount);
    Assert.assertEquals("Meeting", mockView.lastEventEdited);
  }

  @Test
  public void testControllerCallsEditEventsForward() {
    String commands = "edit events subject Meeting from 2025-05-05T10:00 with Updated\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(EditSettings.FORWARD, mockManager.getMockCalendar().lastEditScope);
  }

  @Test
  public void testControllerCallsEditSeriesAll() {
    String commands = "edit series subject Meeting from 2025-05-05T10:00 with Updated\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(EditSettings.ALL_EVENTS, mockManager.getMockCalendar().lastEditScope);
  }

  @Test
  public void testControllerCallsGetEventOnDate() {
    String commands = "print events on 2025-05-05\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.getEventOnDateCallCount);
    Assert.assertEquals(1, mockView.displayEventsOnDateCallCount);
  }

  @Test
  public void testControllerCallsGetEventsInRange() {
    String commands = "print events from 2025-05-05T00:00 to 2025-05-10T23:59\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.getEventsInRangeCallCount);
    Assert.assertEquals(1, mockView.displayEventsInRangeCallCount);
  }

  @Test
  public void testControllerCallsIsBusy() {
    String commands = "show status on 2025-05-05T10:30\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.isBusyCallCount);
    Assert.assertEquals(1, mockView.displayBusyStatusCallCount);
  }

  @Test
  public void testControllerDisplaysErrorOnDuplicateEvent() {
    mockManager.getMockCalendar().shouldThrowDuplicateException = true;

    String commands = "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockView.displayErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Error executing command"));
  }

  @Test
  public void testControllerDisplaysErrorOnEventNotFound() {
    mockManager.getMockCalendar().shouldThrowNotFoundException = true;

    String commands = "edit event subject Meeting from 2025-05-05T10:00 with Updated\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockView.displayErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Error executing command"));
  }

  @Test
  public void testControllerDisplaysErrorOnInvalidCommand() {
    String commands = "invalid command here\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockView.displayErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Invalid command"));
  }

  @Test
  public void testControllerDisplaysWelcomeInInteractiveMode() {
    String commands = "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, true);
    controller.run();

    Assert.assertEquals(2, mockView.displayMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Calendar application started"));
  }

  @Test
  public void testControllerNoWelcomeInHeadlessMode() {
    String commands = "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    for (String msg : mockView.messages) {
      assertFalse(msg.contains("Calendar application started"));
    }
  }

  @Test
  public void testControllerDisplaysExitMessageInInteractiveMode() {
    String commands = "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, true);
    controller.run();

    assertTrue(mockView.messages.stream()
        .anyMatch(msg -> msg.contains("Exiting calendar application")));
  }

  @Test
  public void testControllerNoExitMessageInHeadlessMode() {
    String commands = "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    for (String msg : mockView.messages) {
      assertFalse(msg.contains("Exiting calendar application"));
    }
  }

  @Test
  public void testControllerErrorOnHeadlessModeWithoutExit() {
    String commands = "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockView.displayErrorCallCount);
    assertTrue(mockView.errors.get(0).contains("Headless mode file must end with 'exit'"));
  }

  @Test
  public void testControllerProcessesMultipleCommands() {
    String commands = "create event Event1 from 2025-05-05T10:00 to 2025-05-05T11:00\n"
        + "create event Event2 from 2025-05-05T14:00 to 2025-05-05T15:00\n"
        + "print events on 2025-05-05\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(2, mockCal.addEventCallCount);
    Assert.assertEquals(1, mockCal.getEventOnDateCallCount);
    Assert.assertEquals(2, mockView.displayEventCreatedCallCount);
  }

  @Test
  public void testControllerSkipsEmptyLines() {
    String commands =
        "\n\n\ncreate event Test from 2025-05-05T10:00 to 2025-05-05T11:00\n\n\nexit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockManager.getMockCalendar().addEventCallCount);
  }

  @Test
  public void testControllerPassesBusyStatusToView() {
    mockManager.getMockCalendar().isBusyReturnValue = true;

    String commands = "show status on 2025-05-05T10:30\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    MockCalendar mockCal = mockManager.getMockCalendar();
    Assert.assertEquals(1, mockCal.isBusyCallCount);
    assertTrue(mockView.lastBusyStatus);
  }

  @Test
  public void testControllerPassesAvailableStatusToView() {
    mockManager.getMockCalendar().isBusyReturnValue = false;

    String commands = "show status on 2025-05-05T10:30\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    assertFalse(mockView.lastBusyStatus);
  }

  @Test
  public void testControllerContinuesAfterError() {
    String commands = "invalid command\n"
        + "create event Test from 2025-05-05T10:00 to 2025-05-05T11:00\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(1, mockView.displayErrorCallCount);
    Assert.assertEquals(1, mockManager.getMockCalendar().addEventCallCount);
  }

  @Test
  public void testControllerHandlesMultipleErrors() {
    String commands = "bad1\n"
        + "bad2\n"
        + "bad3\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(3, mockView.displayErrorCallCount);
  }

  @Test
  public void testControllerEditCallsCorrectScope() {
    String commands = "edit event subject M from 2025-05-05T10:00 with U\n"
        + "edit events subject M from 2025-05-05T10:00 with U\n"
        + "edit series subject M from 2025-05-05T10:00 with U\n"
        + "exit\n";

    Scanner scanner = new Scanner(commands);
    Controller controller = new CalendarController(mockManager, mockView, scanner, false);
    controller.run();

    Assert.assertEquals(3, mockManager.getMockCalendar().editEventCallCount);
  }
}