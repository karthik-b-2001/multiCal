package calendar.controller.utils;

import calendar.model.Calendar;
import calendar.model.Event;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface for exporting calendar events to different file formats.
 */
public interface Exporter {

  /**
   * Exports the given list of events to the specified file path.
   *
   * @param events   the list of events to export
   * @param filePath the path of the file to export to
   * @param calendar the calendar from which events are exported
   * @return the absolute path of the exported file as a string
   * @throws IOException if an I/O error occurs during export
   */
  String export(List<Event> events, Path filePath, Calendar calendar) throws IOException;
}