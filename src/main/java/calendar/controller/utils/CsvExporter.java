package calendar.controller.utils;

import calendar.model.Calendar;
import calendar.model.Event;
import calendar.model.LocationType;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting calendar events to CSV format compatible with Google Calendar.
 */
public class CsvExporter implements Exporter {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("hh:mm a");

  @Override
  public String export(List<Event> events, Path filePath, Calendar calendar)
      throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
      writer.write("Subject,Start Date,Start Time,End Date,End Time,All Day Event,"
          + "Description,Location,Private\n");

      for (Event event : events) {
        writer.write(formatEventAsCsv(event));
        writer.write("\n");
      }
    }

    return filePath.toAbsolutePath().toString();
  }

  /**
   * Formats a single event as a CSV row.
   */
  private String formatEventAsCsv(Event event) {
    StringBuilder sb = new StringBuilder();

    sb.append(escapeCsv(event.getSubject())).append(",");

    sb.append(event.getStartDateTime().format(DATE_FORMATTER)).append(",");

    if (event.isAllDayEvent()) {
      sb.append(",");
    } else {
      sb.append(event.getStartDateTime().format(TIME_FORMATTER)).append(",");
    }

    sb.append(event.getEndDateTime().format(DATE_FORMATTER)).append(",");

    if (event.isAllDayEvent()) {
      sb.append(",");
    } else {
      sb.append(event.getEndDateTime().format(TIME_FORMATTER)).append(",");
    }

    sb.append(event.isAllDayEvent() ? "True" : "False").append(",");

    sb.append(escapeCsv(event.getDescription().orElse(""))).append(",");

    sb.append(escapeCsv(event.getLocation().getDisplayValue())).append(",");

    sb.append(event.getStatus().toString().equals("PRIVATE") ? "True" : "False");

    return sb.toString();
  }

  /**
   * Escapes a string for CSV format by wrapping in quotes if necessary.
   */
  private String escapeCsv(String value) {
    if (value.isEmpty()) {
      return "";
    }

    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      value = value.replace("\"", "\"\"");
      return "\"" + value + "\"";
    }

    return value;
  }
}