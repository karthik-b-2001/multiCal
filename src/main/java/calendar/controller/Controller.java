package calendar.controller;

/**
 * Interface for the calendar application controller.
 * The controller handles user interaction and coordinates between the model and view.
 */
public interface Controller {

  /**
   * Starts the controller and begins processing commands.
   * This method should run until an exit command is received or an error occurs.
   */
  void run();
}