package calendar.controller.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating appropriate Exporter instances based on file extension.
 * Implements the Factory Method design pattern.
 */
public class ExporterFactory {

  private static final Map<String, Supplier<Exporter>> EXPORTER_MAP = new HashMap<>();

  static {
    EXPORTER_MAP.put(".ical", IcalExporter::new);
    EXPORTER_MAP.put(".ics", IcalExporter::new);
    EXPORTER_MAP.put(".csv", CsvExporter::new);
  }

  /**
   * Creates an appropriate exporter based on the file extension.
   *
   * @param fileName the name of the file to export to
   * @return an Exporter instance for the given file type
   * @throws IllegalArgumentException if the file extension is not supported
   */
  public static Exporter createExporter(String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      throw new IllegalArgumentException("File name cannot be null or empty");
    }

    String lowerCaseFileName = fileName.toLowerCase();

    for (Map.Entry<String, Supplier<Exporter>> entry : EXPORTER_MAP.entrySet()) {
      if (lowerCaseFileName.endsWith(entry.getKey())) {
        return entry.getValue().get();
      }
    }

    return new CsvExporter();
  }

  /**
   * Checks if a file extension is supported.
   *
   * @param fileName the file name to check
   * @return true if the extension is supported, false otherwise
   */
  public static boolean isSupportedFormat(String fileName) {
    if (fileName == null) {
      return false;
    }

    String lowerCase = fileName.toLowerCase();
    return lowerCase.endsWith(".ical") || lowerCase.endsWith(".ics") || lowerCase.endsWith(".csv");
  }
}