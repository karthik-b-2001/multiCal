package calendar.controller.commands;

import calendar.controller.utils.Exporter;
import calendar.controller.utils.ExporterFactory;
import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.Event;
import calendar.view.View;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Command to export the calendar to a CSV or iCal file.
 */
public class ExportCommand implements Command {
  private final String fileName;
  private final Exporter customExporter;

  /** Creates an ExportCommand with the specified file name.
   *
   * @param fileName the name of the file to export to
   */
  public ExportCommand(String fileName) {
    this.fileName = fileName;
    this.customExporter = null;
  }

  /**
   * Creates an ExportCommand with the specified file name and custom exporter.
   *
   * @param fileName the name of the file to export to
   * @param exporter the custom exporter to use
   */
  public ExportCommand(String fileName, Exporter exporter) {
    this.fileName = fileName;
    this.customExporter = exporter;
  }

  @Override
  public void execute(CalendarManager manager, View view) throws Exception {
    Calendar calendar = manager.getActiveCalendar();
    if (calendar == null) {
      throw new IllegalStateException(
          "No active calendar selected. Use 'use calendar' command first.");
    }

    List<Event> events = calendar.getAllEvents();

    String finalFileName = fileName;
    if (!fileName.toLowerCase().endsWith(".csv") && !fileName.toLowerCase().endsWith(".ical")
        && !fileName.toLowerCase().endsWith(".ics")) {
      finalFileName = fileName + ".csv";
    }

    Path filePath = Paths.get(finalFileName);

    Exporter exporter =
        customExporter != null ? customExporter : ExporterFactory.createExporter(finalFileName);


    String absolutePath = exporter.export(events, filePath, calendar);

    view.displayExportSuccess(absolutePath);
  }
}