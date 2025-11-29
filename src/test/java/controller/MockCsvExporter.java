package controller;

import calendar.controller.utils.CsvExporter;
import calendar.model.Calendar;
import calendar.model.Event;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Mock CSV exporter for testing.
 * Tracks calls and provides controllable behavior without file I/O.
 */
public class MockCsvExporter extends CsvExporter {

  public int exportCallCount = 0;
  public List<Event> lastEvents;
  public Path lastFilePath;
  public boolean shouldThrowIoException = false;

  @Override
  public String export(List<Event> events, Path filePath, Calendar calendar)
      throws IOException {
    exportCallCount++;
    lastEvents = events;
    lastFilePath = filePath;

    if (shouldThrowIoException) {
      throw new IOException("Mock IO exception");
    }

    return filePath.toAbsolutePath().toString();
  }

  /**
   * Resets all counters and stored values.
   */
  public void reset() {
    exportCallCount = 0;
    lastEvents = null;
    lastFilePath = null;
    shouldThrowIoException = false;
  }
}