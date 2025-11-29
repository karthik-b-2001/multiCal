package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import calendar.controller.commands.CopyEventCommand;
import calendar.controller.commands.CopyEventsBetweenCommand;
import calendar.controller.commands.CopyEventsOnDateCommand;
import calendar.controller.commands.CreateCalendarCommand;
import calendar.controller.commands.EditCalendarCommand;
import calendar.controller.commands.UseCalendarCommand;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for new calendar management commands.
 */
public class NewCommandsTest {

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
  public void testCreateCalendarCommand() throws Exception {
    CreateCalendarCommand cmd = new CreateCalendarCommand("Personal",
        ZoneId.of("America/New_York"));
    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.createCalendarCallCount);
    assertEquals(1, mockView.displayMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Calendar created: Personal"));
  }

  @Test
  public void testCreateCalendarMultipleTimes() throws Exception {
    CreateCalendarCommand cmd1 = new CreateCalendarCommand("Personal",
        ZoneId.of("America/New_York"));
    CreateCalendarCommand cmd2 = new CreateCalendarCommand("Work",
        ZoneId.of("America/Chicago"));

    cmd1.execute(mockManager, mockView);
    cmd2.execute(mockManager, mockView);

    assertEquals(2, mockManager.createCalendarCallCount);
  }

  @Test
  public void testUseCalendarCommand() throws Exception {
    UseCalendarCommand cmd = new UseCalendarCommand("Personal");
    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.useCalendarCallCount);
    assertEquals(1, mockView.displayMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Now using calendar: Personal"));
  }

  @Test
  public void testEditCalendarNameCommand() throws Exception {
    EditCalendarCommand cmd = new EditCalendarCommand("OldName", "name", "NewName");
    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.editCalendarCallCount);
    assertEquals(1, mockView.displayMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Calendar edited: OldName"));
  }

  @Test
  public void testEditCalendarTimezoneCommand() throws Exception {
    EditCalendarCommand cmd = new EditCalendarCommand("Personal", "timezone",
        ZoneId.of("Europe/Paris"));
    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.editCalendarCallCount);
  }

  @Test
  public void testCopyEventCommand() throws Exception {
    CopyEventCommand cmd = new CopyEventCommand("Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "Work",
        LocalDateTime.of(2025, 5, 10, 14, 0));

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.copyEventCallCount);
    assertEquals(1, mockView.displayMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Event copied successfully"));
  }

  @Test
  public void testCopyEventsOnDateCommand() throws Exception {
    CopyEventsOnDateCommand cmd = new CopyEventsOnDateCommand(
        LocalDate.of(2025, 5, 5),
        "Work",
        LocalDate.of(2025, 5, 10));

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.copyEventsOnDateCallCount);
    assertEquals(1, mockView.displayMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Events copied successfully"));
  }

  @Test
  public void testCopyEventsBetweenCommand() throws Exception {
    CopyEventsBetweenCommand cmd = new CopyEventsBetweenCommand(
        LocalDate.of(2025, 5, 1),
        LocalDate.of(2025, 5, 31),
        "Work",
        LocalDate.of(2025, 6, 1));

    cmd.execute(mockManager, mockView);

    assertEquals(1, mockManager.copyEventsBetweenCallCount);
    assertEquals(1, mockView.displayMessageCallCount);
    assertTrue(mockView.messages.get(0).contains("Events copied successfully"));
  }

  @Test(expected = IllegalStateException.class)
  public void testCopyEventNoActiveCalendar() throws Exception {
    mockManager.mockCalendar = null;

    CopyEventCommand cmd = new CopyEventCommand("Meeting",
        LocalDateTime.of(2025, 5, 5, 10, 0),
        "Work",
        LocalDateTime.of(2025, 5, 10, 14, 0));

    cmd.execute(mockManager, mockView);
  }

  @Test(expected = IllegalStateException.class)
  public void testCopyEventsOnDateNoActiveCalendar() throws Exception {
    mockManager.mockCalendar = null;

    CopyEventsOnDateCommand cmd = new CopyEventsOnDateCommand(
        LocalDate.of(2025, 5, 5),
        "Work",
        LocalDate.of(2025, 5, 10));

    cmd.execute(mockManager, mockView);
  }

  @Test(expected = IllegalStateException.class)
  public void testCopyEventsBetweenNoActiveCalendar() throws Exception {
    mockManager.mockCalendar = null;

    CopyEventsBetweenCommand cmd = new CopyEventsBetweenCommand(
        LocalDate.of(2025, 5, 1),
        LocalDate.of(2025, 5, 31),
        "Work",
        LocalDate.of(2025, 6, 1));

    cmd.execute(mockManager, mockView);
  }

  @Test
  public void testMultipleCalendarOperations() throws Exception {
    CreateCalendarCommand create1 = new CreateCalendarCommand("Cal1",
        ZoneId.of("America/New_York"));
    CreateCalendarCommand create2 = new CreateCalendarCommand("Cal2",
        ZoneId.of("America/Chicago"));
    UseCalendarCommand use1 = new UseCalendarCommand("Cal1");

    create1.execute(mockManager, mockView);
    create2.execute(mockManager, mockView);

    UseCalendarCommand use2 = new UseCalendarCommand("Cal2");
    use1.execute(mockManager, mockView);
    use2.execute(mockManager, mockView);

    assertEquals(2, mockManager.createCalendarCallCount);
    assertEquals(2, mockManager.useCalendarCallCount);
    assertEquals(4, mockView.displayMessageCallCount);
  }
}