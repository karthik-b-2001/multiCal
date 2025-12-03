package calendar.controller;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.EditSettings;
import calendar.model.Event;
import calendar.view.GuiView;
import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the GuiController interface for managing calendar operations.
 */
public class GuiControllerImpl implements GuiController {

  private final CalendarManager manager;
  private GuiView view;
  private final Map<String, Color> calendarColors;
  private final List<String> calendarNames;
  private int colorIndex = 0;

  private static final Color[] COLORS = {new Color(173, 216, 230),
      new Color(255, 182, 193),
      new Color(144, 238, 144),
      new Color(255, 218, 185),
      new Color(221, 160, 221)
  };

  /**
   * Constructs a GuiControllerImpl with the specified CalendarManager.
   *
   * @param manager the CalendarManager to use
   */
  public GuiControllerImpl(CalendarManager manager) {
    this.manager = manager;
    this.calendarColors = new HashMap<>();
    this.calendarNames = new ArrayList<>();
    createDefaultCalendar();
  }

  @Override
  public void setView(GuiView view) {
    this.view = view;
    view.updateCalendarList(getAllCalendarNames());
  }


  @Override
  public void createCalendar(String name, ZoneId timezone) {
    try {
      manager.createCalendar(name, timezone);
      calendarNames.add(name);
      calendarColors.put(name, COLORS[colorIndex++ % COLORS.length]);
      manager.useCalendar(name);
      view.updateCalendarList(getAllCalendarNames());
      view.showMessage(
          "Calendar created: " + name + System.lineSeparator() + "Calendar Timezone: " + timezone);
      view.refreshCalendar();
    } catch (Exception e) {
      view.showError("Failed to create calendar: " + e.getMessage());
    }

  }

  @Override
  public void switchCalendar(String calendarName) {
    try {
      manager.useCalendar(calendarName);
      view.refreshCalendar();
    } catch (Exception e) {
      view.showError("Failed to switch calendar: " + e.getMessage());
    }


  }

  @Override
  public void createSingleEvent(String subject, LocalDate date, LocalTime startTime,
                                LocalTime endTime, boolean isAllDay) {
    try {
      Calendar calendar = manager.getActiveCalendar();

      LocalDateTime start = LocalDateTime.of(date, isAllDay ? LocalTime.of(8, 0) : startTime);
      LocalDateTime end = LocalDateTime.of(date, isAllDay ? LocalTime.of(17, 0) : endTime);

      calendar.createAndAddEvent(subject, start, end, isAllDay);
      view.refreshCalendar();
      view.showMessage("Event created: " + subject);
    } catch (Exception e) {
      view.showError("Failed to create event: " + e.getMessage());
    }


  }

  @Override
  public void createRecurringEvent(String subject, LocalDate startDate, LocalTime startTime,
                                   LocalTime endTime, Set<DayOfWeek> weekdays, Integer occurrences,
                                   LocalDate endDate, boolean isAllDay) {

    try {
      Calendar calendar = manager.getActiveCalendar();

      if (isAllDay) {
        if (occurrences != null) {
          calendar.createAllDayEventSeries(subject, startDate, weekdays, occurrences);
        } else {
          calendar.createAllDayEventSeriesTill(subject, startDate, weekdays, endDate);
        }
      } else {
        if (occurrences != null) {
          calendar.createEventSeries(subject, startDate, startTime, endTime, weekdays, occurrences);
        } else {
          calendar.createEventSeriesTill(subject, startDate, startTime, endTime, weekdays, endDate);
        }
      }

      view.refreshCalendar();
      view.showMessage("Recurring event created: " + subject);

    } catch (Exception e) {
      view.showError("Failed to create recurring event: " + e.getMessage());
    }
  }

  @Override
  public void editEvent(String subject, LocalDateTime startDateTime, String property,
                        Object newValue, EditSettings scope) {
    try {
      Calendar calendar = manager.getActiveCalendar();

      calendar.editEvent(subject, startDateTime, property, newValue, scope);
      view.refreshCalendar();
    } catch (Exception e) {
      view.showError("Failed to edit event: " + e.getMessage());
    }

  }

  @Override
  public List<Event> getEventsForDate(LocalDate date) {
    Calendar calendar = manager.getActiveCalendar();
    return calendar.getEventOnDate(date);
  }

  @Override
  public List<String> getAllCalendarNames() {
    return new ArrayList<>(calendarNames);
  }

  @Override
  public Color getCalendarColor(String calendarName) {
    return calendarColors.getOrDefault(calendarName, COLORS[0]);
  }

  @Override
  public String getActiveCalendarName() {
    Calendar activeCalendar = manager.getActiveCalendar();
    return activeCalendar.getCalendarName();
  }

  @Override
  public ZoneId getCurrentTimezone() {
    Calendar activeCalendar = manager.getActiveCalendar();
    return activeCalendar.getTimeZone();
  }

  private void createDefaultCalendar() {
    String defaultName = "Personal";
    ZoneId defaultZone = ZoneId.systemDefault();
    manager.createCalendar(defaultName, defaultZone);
    manager.useCalendar(defaultName);
    calendarNames.add(defaultName);
    calendarColors.put(defaultName, COLORS[colorIndex++ % COLORS.length]);

  }

  @Override
  public Calendar getActiveCalendar() {
    return manager.getActiveCalendar();
  }

}
