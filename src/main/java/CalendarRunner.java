import calendar.controller.CalendarController;
import calendar.controller.Controller;
import calendar.controller.GuiController;
import calendar.controller.GuiControllerImpl;
import calendar.model.CalendarManager;
import calendar.model.CalendarManagerImpl;
import calendar.view.ConsoleView;
import calendar.view.GuiView;
import calendar.view.GuiViewImpl;
import calendar.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.SwingUtilities;

/**
 * Main class for running the calendar application.
 * Supports both interactive and headless modes.
 */
public class CalendarRunner {

  /**
   * Main entry point for the calendar application.
   *
   * @param args command line arguments: --mode interactive OR --mode headless.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      launchGui();
      return;
    }
    if (args.length < 2) {
      System.err.println("Usage: java CalendarRunner --mode interactive");
      System.err.println("   OR: java CalendarRunner --mode headless <filename>");
      System.exit(1);
    }

    String modeFlag = args[0];
    String mode = args[1];

    if (!modeFlag.equalsIgnoreCase("--mode")) {
      System.err.println("Error: First argument must be --mode");
      System.exit(1);
    }

    CalendarManager manager = new CalendarManagerImpl();
    View view = new ConsoleView();
    Scanner scanner;
    boolean isInteractive;

    if (mode.equalsIgnoreCase("interactive")) {
      scanner = new Scanner(System.in);
      isInteractive = true;

    } else if (mode.equalsIgnoreCase("headless")) {
      if (args.length < 3) {
        System.err.println("Error: Headless mode requires a filename");
        System.exit(1);
      }

      String fileName = args[2];

      try {
        File file = new File(fileName);
        scanner = new Scanner(file);
        isInteractive = false;

      } catch (FileNotFoundException e) {
        System.err.println("Error: File not found: " + fileName);
        System.exit(1);
        return;
      }

    } else {
      System.err.println("Error: Mode must be 'interactive' or 'headless'");
      System.exit(1);
      return;
    }

    Controller controller = new CalendarController(manager, view, scanner, isInteractive);
    controller.run();

    scanner.close();
  }

  private static void launchGui() {
    SwingUtilities.invokeLater(() -> {
      CalendarManager manager = new CalendarManagerImpl();
      GuiController controller = new GuiControllerImpl(manager);
      GuiView view = new GuiViewImpl(controller);
      controller.setView(view);
      view.display();
    });
  }
}