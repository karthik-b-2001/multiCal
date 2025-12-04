package calendar.controller.utils;

import calendar.model.Calendar;
import calendar.model.Event;
import calendar.model.EventStatus;
import calendar.model.LocationType;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting calendar events to iCal format.
 */
public class IcalExporter implements Exporter {

  private static final DateTimeFormatter ICAL_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");


  @Override
  public String export(List<Event> events, Path filePath, Calendar calendar)
      throws IOException {
    String calendarName = calendar.getCalendarName();
    ZoneId timeZone = calendar.getTimeZone();

    try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
      writer.write("BEGIN:VCALENDAR\n");
      writer.write("VERSION:2.0\n");
      writer.write("PRODID:-//Calendar Application//EN\n");
      writer.write("CALSCALE:GREGORIAN\n");
      writer.write("METHOD:PUBLISH\n");
      writer.write("X-WR-CALNAME:" + escapeText(calendarName) + "\n");
      writer.write("X-WR-TIMEZONE:" + timeZone.getId() + "\n");

      for (Event event : events) {
        writeEvent(writer, event, timeZone);
      }

      writer.write("END:VCALENDAR\n");
    }

    return filePath.toAbsolutePath().toString();
  }

  /**
   * Writes a single event to the iCal file.
   */
  private void writeEvent(BufferedWriter writer, Event event, ZoneId timeZone) throws IOException {
    writer.write("BEGIN:VEVENT\n");

    writer.write("UID:" + generateUid(event) + "\n");

    writer.write("DTSTAMP:" + formatDateTime(LocalDateTime.now(), timeZone) + "\n");

    writer.write("DTSTART:" + formatDateTime(event.getStartDateTime(), timeZone) + "\n");
    writer.write("DTEND:" + formatDateTime(event.getEndDateTime(), timeZone) + "\n");

    writer.write("SUMMARY:" + escapeText(event.getSubject()) + "\n");

    if (event.getDescription().isPresent()) {
      writer.write("DESCRIPTION:" + escapeText(event.getDescription().get()) + "\n");
    }

    if (event.getLocation() != LocationType.NONE) {
      writer.write("LOCATION:" + escapeText(event.getLocation().getDisplayValue()) + "\n");
    }

    writer.write("CLASS:" + (event.getStatus() == EventStatus.PRIVATE ? "PRIVATE" : "PUBLIC")
        + "\n");

    if (event.isAllDayEvent()) {
      writer.write("X-MICROSOFT-CDO-ALLDAYEVENT:TRUE\n");
    }

    writer.write("END:VEVENT\n");
  }

  /**
   * Formats a LocalDateTime to iCal format in UTC.
   */
  private String formatDateTime(LocalDateTime dateTime, ZoneId timeZone) {
    ZonedDateTime zonedDateTime = dateTime.atZone(timeZone);
    ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
    return utcDateTime.format(ICAL_FORMATTER);
  }

  /**
   * Generates a unique UID for an event.
   */
  private String generateUid(Event event) {
    return Math.abs(event.hashCode()) + "@calendar-app";
  }

  /**
   * Escapes special characters for iCal format.
   */
  private String escapeText(String text) {
    if (text.isEmpty()) {
      return "";
    }
    return text.replace("\\", "\\\\")
        .replace(",", "\\,")
        .replace(";", "\\;")
        .replace("\n", "\\n");
  }
}