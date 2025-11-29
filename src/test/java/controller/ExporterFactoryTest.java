package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import calendar.controller.utils.CsvExporter;
import calendar.controller.utils.Exporter;
import calendar.controller.utils.ExporterFactory;
import calendar.controller.utils.IcalExporter;
import org.junit.Test;

/**
 * Test class for ExporterFactory.
 */
public class ExporterFactoryTest {

  @Test
  public void testCreateCsvExporter() {
    Exporter exporter = ExporterFactory.createExporter("test.csv");
    assertNotNull(exporter);
    assertTrue(exporter instanceof CsvExporter);
  }

  @Test
  public void testNewExporterFactoryNotNull() {
    ExporterFactory factory = new ExporterFactory();
    assertNotNull(factory);
  }

  @Test
  public void testCreateIcalExporter() {
    Exporter exporter = ExporterFactory.createExporter("test.ical");
    assertNotNull(exporter);
    assertTrue(exporter instanceof IcalExporter);
  }

  @Test
  public void testCreateIcsExporter() {
    Exporter exporter = ExporterFactory.createExporter("test.ics");
    assertNotNull(exporter);
    assertTrue(exporter instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterCaseInsensitive() {
    Exporter exporter1 = ExporterFactory.createExporter("TEST.CSV");
    Exporter exporter2 = ExporterFactory.createExporter("TEST.ICAL");
    Exporter exporter3 = ExporterFactory.createExporter("test.ICS");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
    assertTrue(exporter3 instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterMixedCase() {
    Exporter exporter1 = ExporterFactory.createExporter("file.CsV");
    Exporter exporter2 = ExporterFactory.createExporter("file.IcAl");
    Exporter exporter3 = ExporterFactory.createExporter("file.IcS");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
    assertTrue(exporter3 instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterWithPath() {
    Exporter exporter1 = ExporterFactory.createExporter("output/data/file.csv");
    Exporter exporter2 = ExporterFactory.createExporter("output/data/file.ical");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterWithoutExtension() {
    Exporter exporter = ExporterFactory.createExporter("filename");
    assertNotNull(exporter);
    assertTrue(exporter instanceof CsvExporter);
  }

  @Test
  public void testCreateExporterWithUnknownExtension() {
    Exporter exporter1 = ExporterFactory.createExporter("file.txt");
    Exporter exporter2 = ExporterFactory.createExporter("file.json");
    Exporter exporter3 = ExporterFactory.createExporter("file.xml");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof CsvExporter);
    assertTrue(exporter3 instanceof CsvExporter);
  }

  @Test
  public void testCreateExporterWithMultipleDots() {
    Exporter exporter1 = ExporterFactory.createExporter("file.backup.csv");
    Exporter exporter2 = ExporterFactory.createExporter("file.v2.ical");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterWithOnlyExtension() {
    Exporter exporter1 = ExporterFactory.createExporter(".csv");
    Exporter exporter2 = ExporterFactory.createExporter(".ical");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateExporterNullFileName() {
    ExporterFactory.createExporter(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateExporterEmptyFileName() {
    ExporterFactory.createExporter("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateExporterWhitespaceOnly() {
    ExporterFactory.createExporter("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateExporterTabsOnly() {
    ExporterFactory.createExporter("\t\t\t");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateExporterNewlinesOnly() {
    ExporterFactory.createExporter("\n\n");
  }

  @Test
  public void testIsSupportedFormatCsv() {
    assertTrue(ExporterFactory.isSupportedFormat("file.csv"));
    assertTrue(ExporterFactory.isSupportedFormat("FILE.CSV"));
    assertTrue(ExporterFactory.isSupportedFormat("file.CsV"));
  }

  @Test
  public void testIsSupportedFormatIcal() {
    assertTrue(ExporterFactory.isSupportedFormat("file.ical"));
    assertTrue(ExporterFactory.isSupportedFormat("FILE.ICAL"));
    assertTrue(ExporterFactory.isSupportedFormat("file.IcAl"));
  }

  @Test
  public void testIsSupportedFormatIcs() {
    assertTrue(ExporterFactory.isSupportedFormat("file.ics"));
    assertTrue(ExporterFactory.isSupportedFormat("FILE.ICS"));
    assertTrue(ExporterFactory.isSupportedFormat("file.IcS"));
  }

  @Test
  public void testIsSupportedFormatWithPath() {
    assertTrue(ExporterFactory.isSupportedFormat("output/data/file.csv"));
    assertTrue(ExporterFactory.isSupportedFormat("output/data/file.ical"));
    assertTrue(ExporterFactory.isSupportedFormat("output/data/file.ics"));
  }

  @Test
  public void testIsSupportedFormatUnsupported() {
    assertFalse(ExporterFactory.isSupportedFormat("file.txt"));
    assertFalse(ExporterFactory.isSupportedFormat("file.json"));
    assertFalse(ExporterFactory.isSupportedFormat("file.xml"));
    assertFalse(ExporterFactory.isSupportedFormat("filename"));
  }

  @Test
  public void testIsSupportedFormatNull() {
    assertFalse(ExporterFactory.isSupportedFormat(null));
  }

  @Test
  public void testIsSupportedFormatEmpty() {
    assertFalse(ExporterFactory.isSupportedFormat(""));
  }

  @Test
  public void testCreateExporterReturnsNewInstances() {
    Exporter exporter1 = ExporterFactory.createExporter("file1.csv");
    Exporter exporter2 = ExporterFactory.createExporter("file2.csv");

    assertNotNull(exporter1);
    assertNotNull(exporter2);
    assertNotSame(exporter1, exporter2);
  }

  @Test
  public void testCreateExporterConsistentTypes() {
    Exporter csv1 = ExporterFactory.createExporter("file1.csv");
    Exporter csv2 = ExporterFactory.createExporter("file2.csv");

    assertEquals(csv1.getClass(), csv2.getClass());
  }

  @Test
  public void testCreateExporterWithLeadingWhitespace() {
    Exporter exporter = ExporterFactory.createExporter("  file.csv");
    assertNotNull(exporter);
    assertTrue(exporter instanceof CsvExporter);
  }

  @Test
  public void testCreateExporterWithTrailingWhitespace() {
    Exporter exporter = ExporterFactory.createExporter("file.csv  ");
    assertNotNull(exporter);
    assertTrue(exporter instanceof CsvExporter);
  }

  @Test
  public void testAllSupportedExtensions() {
    String[] csvFiles = {"test.csv", "TEST.CSV", "file.CsV"};
    String[] icalFiles = {"test.ical", "TEST.ICAL", "file.IcAl"};
    String[] icsFiles = {"test.ics", "TEST.ICS", "file.IcS"};

    for (String file : csvFiles) {
      Exporter exporter = ExporterFactory.createExporter(file);
      assertTrue("Failed for: " + file, exporter instanceof CsvExporter);
    }

    for (String file : icalFiles) {
      Exporter exporter = ExporterFactory.createExporter(file);
      assertTrue("Failed for: " + file, exporter instanceof IcalExporter);
    }

    for (String file : icsFiles) {
      Exporter exporter = ExporterFactory.createExporter(file);
      assertTrue("Failed for: " + file, exporter instanceof IcalExporter);
    }
  }

  @Test
  public void testIsSupportedFormatAllExtensions() {
    String[] supported = {
        "file.csv", "file.CSV", "file.CsV",
        "file.ical", "file.ICAL", "file.IcAl",
        "file.ics", "file.ICS", "file.IcS"
    };

    for (String file : supported) {
      assertTrue("Failed for: " + file, ExporterFactory.isSupportedFormat(file));
    }
  }

  @Test
  public void testIsSupportedFormatAllUnsupported() {
    String[] unsupported = {
        "file.txt", "file.json", "file.xml",
        "file.pdf", "file.doc", "filename"
    };

    for (String file : unsupported) {
      assertFalse("Failed for: " + file, ExporterFactory.isSupportedFormat(file));
    }
  }

  @Test
  public void testCreateExporterWithSpecialCharacters() {
    Exporter exporter1 = ExporterFactory.createExporter("file-name.csv");
    Exporter exporter2 = ExporterFactory.createExporter("file_name.ical");
    Exporter exporter3 = ExporterFactory.createExporter("file (1).ics");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
    assertTrue(exporter3 instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterWithNumbers() {
    Exporter exporter1 = ExporterFactory.createExporter("file123.csv");
    Exporter exporter2 = ExporterFactory.createExporter("2024-report.ical");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterLongFilename() {
    String longFilename = "very_long_filename_with_many_characters_and_underscores.csv";
    Exporter exporter = ExporterFactory.createExporter(longFilename);

    assertNotNull(exporter);
    assertTrue(exporter instanceof CsvExporter);
  }

  @Test
  public void testCreateExporterWithAbsolutePath() {
    Exporter exporter1 = ExporterFactory.createExporter("/home/user/documents/file.csv");
    Exporter exporter2 = ExporterFactory.createExporter("C:\\Users\\Documents\\file.ical");

    assertTrue(exporter1 instanceof CsvExporter);
    assertTrue(exporter2 instanceof IcalExporter);
  }

  @Test
  public void testCreateExporterExtensionAtStart() {
    Exporter exporter = ExporterFactory.createExporter("csv.file.txt");
    assertTrue(exporter instanceof CsvExporter);
  }

  @Test
  public void testCreateExporterExtensionInMiddle() {
    Exporter exporter = ExporterFactory.createExporter("file.csv.backup");
    assertTrue(exporter instanceof CsvExporter);
  }

  @Test
  public void testIsSupportedFormatCaseSensitivity() {
    String[] variations = {"csv", "CSV", "Csv", "CsV", "cSv", "csV"};

    for (String ext : variations) {
      assertTrue("Failed for: ." + ext,
          ExporterFactory.isSupportedFormat("file." + ext));
    }
  }

  @Test
  public void testFactoryProducesFunctionalExporters() {
    Exporter csvExporter = ExporterFactory.createExporter("test.csv");
    Exporter icalExporter = ExporterFactory.createExporter("test.ical");

    assertNotNull(csvExporter);
    assertNotNull(icalExporter);
    assertTrue(csvExporter instanceof CsvExporter);
    assertTrue(icalExporter instanceof IcalExporter);
  }

  @Test
  public void testMultipleCallsSameExtension() {
    for (int i = 0; i < 10; i++) {
      Exporter exporter = ExporterFactory.createExporter("file" + i + ".csv");
      assertTrue(exporter instanceof CsvExporter);
    }
  }

  @Test
  public void testAlternatingExtensions() {
    for (int i = 0; i < 10; i++) {
      String ext = (i % 2 == 0) ? ".csv" : ".ical";
      Exporter exporter = ExporterFactory.createExporter("file" + i + ext);

      if (i % 2 == 0) {
        assertTrue(exporter instanceof CsvExporter);
      } else {
        assertTrue(exporter instanceof IcalExporter);
      }
    }
  }
}