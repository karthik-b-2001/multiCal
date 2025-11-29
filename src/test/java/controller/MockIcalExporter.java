package controller;

import calendar.controller.utils.IcalExporter;
import calendar.model.Calendar;
import calendar.model.Event;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;

/**
 * Mock iCal exporter for testing.
 */
public class MockIcalExporter extends IcalExporter {

  public int exportCallCount = 0;
  public List<Event> lastEvents;
  public Path lastFilePath;
  public String lastCalendarName;
  public ZoneId lastTimeZone;

  @Override
  public String export(List<Event> events, Path filePath, Calendar calendar)
      throws IOException {
    exportCallCount++;
    lastEvents = events;
    lastFilePath = filePath;
    lastCalendarName = calendar.getCalendarName();
    lastTimeZone = calendar.getTimeZone();

    return filePath.toAbsolutePath().toString();
  }

  /**
   * Resets the mock exporter's state.
   */
  public void reset() {
    exportCallCount = 0;
    lastEvents = null;
    lastFilePath = null;
    lastCalendarName = null;
    lastTimeZone = null;
  }
}