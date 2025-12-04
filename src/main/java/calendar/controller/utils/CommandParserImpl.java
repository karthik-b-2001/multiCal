package calendar.controller.utils;

import calendar.controller.commands.Command;
import calendar.controller.commands.CopyEventCommand;
import calendar.controller.commands.CopyEventsBetweenCommand;
import calendar.controller.commands.CopyEventsOnDateCommand;
import calendar.controller.commands.CreateCalendarCommand;
import calendar.controller.commands.CreateEventCommand;
import calendar.controller.commands.EditCalendarCommand;
import calendar.controller.commands.EditEventCommand;
import calendar.controller.commands.ExportCommand;
import calendar.controller.commands.PrintEventsCommand;
import calendar.controller.commands.ShowStatusCommand;
import calendar.controller.commands.UseCalendarCommand;
import calendar.model.EditSettings;
import calendar.model.EventStatus;
import calendar.model.LocationType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Parses command strings and creates corresponding Command objects.
 */
public class CommandParserImpl implements CommandParser {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final Map<String, Function<String, Command>> commandParsers;

  /**
   * Constructs a CommandParser and initializes the command routing map.
   */
  public CommandParserImpl() {
    this.commandParsers = new HashMap<>();
    initializeCommandParsers();
  }

  /**
   * Parses a command string and returns the corresponding Command object.
   *
   * @param commandLine the command string to parse
   * @return the parsed Command
   * @throws IllegalArgumentException if the command is invalid
   */
  @Override
  public Command parse(String commandLine) throws IllegalArgumentException {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty");
    }

    String trimmed = commandLine.trim();

    if (trimmed.equals("exit")) {
      return null;
    }

    for (Map.Entry<String, Function<String, Command>> entry : commandParsers.entrySet()) {
      if (trimmed.startsWith(entry.getKey())) {
        return entry.getValue().apply(trimmed);
      }
    }

    throw new IllegalArgumentException("Unknown command: " + trimmed);
  }

  /**
   * Initializes the commandParsers map with command keywords and their parsing functions.
   */
  private void initializeCommandParsers() {
    commandParsers.put("create calendar", this::parseCreateCalendar);
    commandParsers.put("create event", this::parseCreateCommand);

    commandParsers.put("edit calendar", this::parseEditCalendar);
    commandParsers.put("edit series ", this::parseEditCommand);
    commandParsers.put("edit events ", this::parseEditCommand);
    commandParsers.put("edit event ", this::parseEditCommand);

    commandParsers.put("use calendar", this::parseUseCalendar);

    commandParsers.put("copy events between", this::parseCopyEventsBetween);
    commandParsers.put("copy events on", this::parseCopyEventsOnDate);
    commandParsers.put("copy event ", this::parseCopyEvent);

    commandParsers.put("print events on", this::parsePrintOnDateCommand);
    commandParsers.put("print events from", this::parsePrintRangeCommand);

    commandParsers.put("export cal", this::parseExportCommand);

    commandParsers.put("show status on", this::parseShowStatusCommand);
  }



  /**
   * Parses create calendar command.
   * Format: create calendar --name CalendarName --timezone Area/Location
   */
  private Command parseCreateCalendar(String command) {
    String remaining = command.substring("create calendar".length()).trim();

    if (!remaining.startsWith("--name")) {
      throw new IllegalArgumentException("Expected --name after 'create calendar'");
    }

    remaining = remaining.substring("--name".length()).trim();

    String[] parts = remaining.split("--timezone");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Missing --timezone in create calendar command");
    }

    String name = parts[0].trim();
    String timezoneStr = parts[1].trim();

    try {
      ZoneId timezone = ZoneId.of(timezoneStr);
      return new CreateCalendarCommand(name, timezone);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid timezone: " + timezoneStr);
    }
  }

  /**
   * Parses use calendar command.
   * Format: use calendar --name CalendarName
   */
  private Command parseUseCalendar(String command) {
    String remaining = command.substring("use calendar".length()).trim();

    if (!remaining.startsWith("--name")) {
      throw new IllegalArgumentException("Expected --name after 'use calendar'");
    }

    String name = remaining.substring("--name".length()).trim();
    return new UseCalendarCommand(name);
  }

  /**
   * Parses edit calendar command.
   * Format: edit calendar --name CalendarName --property propertyName newValue
   */
  private Command parseEditCalendar(String command) {
    String remaining = command.substring("edit calendar".length()).trim();

    if (!remaining.startsWith("--name")) {
      throw new IllegalArgumentException("Expected --name");
    }

    remaining = remaining.substring("--name".length()).trim();
    String[] parts = remaining.split("--property");

    if (parts.length != 2) {
      throw new IllegalArgumentException("Missing --property");
    }

    String calendarName = parts[0].trim();
    String propertyPart = parts[1].trim();

    String[] propertyTokens = propertyPart.split(" ", 2);
    if (propertyTokens.length < 2) {
      throw new IllegalArgumentException("Missing property value");
    }

    String property = propertyTokens[0].trim();
    String value = propertyTokens[1].trim();

    Object newValue;
    if (property.equalsIgnoreCase("timezone")) {
      try {
        newValue = ZoneId.of(value);
      } catch (Exception e) {
        throw new IllegalArgumentException("Invalid timezone: " + value);
      }
    } else {
      newValue = value;
    }

    return new EditCalendarCommand(calendarName, property, newValue);
  }

  /**
   * Parses copy event command.
   * Format: copy event EventName on YYYY-MM-DDTHH:MM --target TargetCal to YYYY-MM-DDTHH:MM
   */
  private Command parseCopyEvent(String command) {
    String remaining = command.substring("copy event".length()).trim();

    String[] onParts = remaining.split(" on ");
    if (onParts.length < 2) {
      throw new IllegalArgumentException("Missing 'on' in copy event command");
    }

    String eventName = onParts[0].trim();

    if (eventName.startsWith("\"") && eventName.endsWith("\"")) {
      eventName = eventName.substring(1, eventName.length() - 1);
    }

    String[] targetParts = onParts[1].split(" --target ");
    if (targetParts.length < 2) {
      throw new IllegalArgumentException("Missing '--target' in copy event command");
    }

    LocalDateTime sourceDateTime = LocalDateTime.parse(targetParts[0].trim(), DATE_TIME_FORMATTER);

    String[] toParts = targetParts[1].split(" to ");
    if (toParts.length < 2) {
      throw new IllegalArgumentException("Missing 'to' in copy event command");
    }

    String targetCalendar = toParts[0].trim();
    LocalDateTime targetDateTime = LocalDateTime.parse(toParts[1].trim(), DATE_TIME_FORMATTER);

    return new CopyEventCommand(eventName, sourceDateTime, targetCalendar, targetDateTime);
  }

  /**
   * Parses copy events on date command.
   * Format: copy events on YYYY-MM-DD --target TargetCal to YYYY-MM-DD
   */
  private Command parseCopyEventsOnDate(String command) {
    String remaining = command.substring("copy events on".length()).trim();

    String[] targetParts = remaining.split(" --target ");
    if (targetParts.length < 2) {
      throw new IllegalArgumentException("Missing '--target'");
    }

    LocalDate sourceDate = LocalDate.parse(targetParts[0].trim(), DATE_FORMATTER);

    String[] toParts = targetParts[1].split(" to ");
    if (toParts.length < 2) {
      throw new IllegalArgumentException("Missing 'to'");
    }

    String targetCalendar = toParts[0].trim();
    LocalDate targetDate = LocalDate.parse(toParts[1].trim(), DATE_FORMATTER);

    return new CopyEventsOnDateCommand(sourceDate, targetCalendar, targetDate);
  }

  /**
   * Parses copy events between dates command.
   * Format: copy events between YYYY-MM-DD and YYYY-MM-DD --target TargetCal to YYYY-MM-DD
   */
  private Command parseCopyEventsBetween(String command) {
    String remaining = command.substring("copy events between".length()).trim();

    String[] andParts = remaining.split(" and ");
    if (andParts.length < 2) {
      throw new IllegalArgumentException("Missing 'and'");
    }

    LocalDate startDate = LocalDate.parse(andParts[0].trim(), DATE_FORMATTER);

    String[] targetParts = andParts[1].split(" --target ");
    if (targetParts.length < 2) {
      throw new IllegalArgumentException("Missing '--target'");
    }

    LocalDate endDate = LocalDate.parse(targetParts[0].trim(), DATE_FORMATTER);

    String[] toParts = targetParts[1].split(" to ");
    if (toParts.length < 2) {
      throw new IllegalArgumentException("Missing 'to'");
    }

    String targetCalendar = toParts[0].trim();
    LocalDate targetStartDate = LocalDate.parse(toParts[1].trim(), DATE_FORMATTER);

    return new CopyEventsBetweenCommand(startDate, endDate, targetCalendar, targetStartDate);
  }

  /**
   * Parses create event commands.
   */
  private Command parseCreateCommand(String command) {
    String remaining = command.substring("create event ".length()).trim();

    String subject;
    int subjectEnd;

    if (remaining.startsWith("\"")) {
      int closingQuote = remaining.indexOf("\"", 1);
      if (closingQuote == -1) {
        throw new IllegalArgumentException("Unclosed quote in subject");
      }
      subject = remaining.substring(1, closingQuote);
      subjectEnd = closingQuote + 1;
    } else {
      int spaceIdx = remaining.indexOf(" ");
      if (spaceIdx == -1) {
        throw new IllegalArgumentException("Invalid create command format");
      }
      subject = remaining.substring(0, spaceIdx);
      subjectEnd = spaceIdx;
    }

    remaining = remaining.substring(subjectEnd).trim();

    if (remaining.startsWith("on ")) {
      return parseAllDayCreate(subject, remaining.substring(3));
    } else if (remaining.startsWith("from ")) {
      return parseTimedCreate(subject, remaining.substring(5));
    } else {
      throw new IllegalArgumentException("Expected 'on' or 'from' after subject");
    }
  }

  /**
   * Parses all-day event creation.
   */
  private Command parseAllDayCreate(String subject, String remaining) {
    String[] parts = remaining.split(" repeats ");

    LocalDate startDate = LocalDate.parse(parts[0].trim(), DATE_FORMATTER);

    if (parts.length == 1) {
      return new CreateEventCommand(subject, startDate);
    } else {
      String repeatPart = parts[1].trim();
      Set<DayOfWeek> weekdays = parseWeekdays(repeatPart.split(" ")[0]);

      if (repeatPart.contains(" for ")) {
        String[] forParts = repeatPart.split(" for ");
        int occurrences = Integer.parseInt(forParts[1].replace(" times", "").trim());
        return new CreateEventCommand(subject, startDate, weekdays, occurrences);
      } else if (repeatPart.contains(" until ")) {
        String[] untilParts = repeatPart.split(" until ");
        LocalDate endDate = LocalDate.parse(untilParts[1].trim(), DATE_FORMATTER);
        return new CreateEventCommand(subject, startDate, weekdays, endDate);
      } else {
        throw new IllegalArgumentException("Invalid repeat format");
      }
    }
  }

  /**
   * Parses timed event creation.
   */
  private Command parseTimedCreate(String subject, String remaining) {
    String[] parts = remaining.split(" to ");
    if (parts.length < 2) {
      throw new IllegalArgumentException("Missing 'to' in timed event");
    }

    LocalDateTime startDateTime = LocalDateTime.parse(parts[0].trim(), DATE_TIME_FORMATTER);

    String secondPart = parts[1].trim();
    String[] repeatSplit = secondPart.split(" repeats ");

    LocalDateTime endDateTime = LocalDateTime.parse(repeatSplit[0].trim(), DATE_TIME_FORMATTER);

    if (repeatSplit.length == 1) {
      return new CreateEventCommand(subject, startDateTime, endDateTime);
    } else {
      String repeatPart = repeatSplit[1].trim();
      Set<DayOfWeek> weekdays = parseWeekdays(repeatPart.split(" ")[0]);

      if (repeatPart.contains(" for ")) {
        String[] forParts = repeatPart.split(" for ");
        int occurrences = Integer.parseInt(forParts[1].replace(" times", "").trim());
        return new CreateEventCommand(subject, startDateTime, endDateTime, weekdays, occurrences);
      } else if (repeatPart.contains(" until ")) {
        String[] untilParts = repeatPart.split(" until ");
        LocalDate endDate = LocalDate.parse(untilParts[1].trim(), DATE_FORMATTER);
        return new CreateEventCommand(subject, startDateTime, endDateTime, weekdays, endDate);
      } else {
        throw new IllegalArgumentException("Invalid repeat format");
      }
    }
  }

  /**
   * Parses edit event commands.
   */
  private Command parseEditCommand(String command) {
    EditSettings scope;
    String remaining;

    if (command.startsWith("edit event ")) {
      scope = EditSettings.SINGLE;
      remaining = command.substring("edit event ".length());
    } else if (command.startsWith("edit events ")) {
      scope = EditSettings.FORWARD;
      remaining = command.substring("edit events ".length());
    } else {
      scope = EditSettings.ALL_EVENTS;
      remaining = command.substring("edit series ".length());
    }

    String[] parts = remaining.split(" with ");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Missing 'with' in edit command");
    }

    String beforeWith = parts[0].trim();

    String[] tokens = beforeWith.split(" ", 2);
    String rest = tokens[1];

    String subject;
    String afterSubject;

    if (rest.startsWith("\"")) {
      int closingQuote = rest.indexOf("\"", 1);
      if (closingQuote == -1) {
        throw new IllegalArgumentException("Unclosed quote in subject");
      }
      subject = rest.substring(1, closingQuote);
      afterSubject = rest.substring(closingQuote + 1).trim();
    } else {
      int fromIdx = rest.indexOf(" from ");
      if (fromIdx == -1) {
        throw new IllegalArgumentException("Missing 'from' in edit command");
      }
      subject = rest.substring(0, fromIdx).trim();
      afterSubject = rest.substring(fromIdx).trim();
    }

    if (!afterSubject.startsWith("from ")) {
      throw new IllegalArgumentException("Missing 'from' in edit command");
    }

    afterSubject = afterSubject.substring(5).trim();

    LocalDateTime startDateTime;

    if (afterSubject.contains(" to ")) {
      String[] dateParts = afterSubject.split(" to ");
      startDateTime = LocalDateTime.parse(dateParts[0].trim(), DATE_TIME_FORMATTER);
    } else {
      startDateTime = LocalDateTime.parse(afterSubject.trim(), DATE_TIME_FORMATTER);
    }

    String newValueStr = parts[1].trim();
    String property = tokens[0];

    Object newValue = parsePropertyValue(property, newValueStr);

    return new EditEventCommand(subject, startDateTime, property, newValue, scope);
  }

  /**
   * Parses the new value for a property based on its type.
   */
  private Object parsePropertyValue(String property, String valueStr) {
    switch (property.toLowerCase()) {
      case "subject":
      case "description":
        return valueStr;
      case "location":
        return parseLocationType(valueStr);
      case "start":
      case "end":
        return LocalDateTime.parse(valueStr, DATE_TIME_FORMATTER);
      case "status":
        return EventStatus.valueOf(valueStr.toUpperCase());
      default:
        throw new IllegalArgumentException("Unknown property: " + property);
    }
  }

  /**
   * Parses location string to LocationType enum.
   * Accepts: "physical", "online", "none", or empty string.
   *
   * @param value the location string
   * @return the corresponding LocationType
   */
  private LocationType parseLocationType(String value) {
    if (value.equalsIgnoreCase("physical")) {
      return LocationType.PHYSICAL;
    } else if (value.equalsIgnoreCase("online")) {
      return LocationType.ONLINE;
    }
    return LocationType.NONE;
  }


  /**
   * Parses print events on date command.
   */
  private Command parsePrintOnDateCommand(String command) {
    String dateStr = command.substring("print events on ".length()).trim();
    LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
    return new PrintEventsCommand(date);
  }

  /**
   * Parses print events in range command.
   */
  private Command parsePrintRangeCommand(String command) {
    String remaining = command.substring("print events from ".length()).trim();
    String[] parts = remaining.split(" to ");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid range format");
    }

    LocalDateTime start = LocalDateTime.parse(parts[0].trim(), DATE_TIME_FORMATTER);
    LocalDateTime end = LocalDateTime.parse(parts[1].trim(), DATE_TIME_FORMATTER);
    return new PrintEventsCommand(start, end);
  }

  /**
   * Parses export command.
   */
  private Command parseExportCommand(String command) {
    String fileName = command.substring("export cal ".length()).trim();
    return new ExportCommand(fileName);
  }

  /**
   * Parses show status command.
   */
  private Command parseShowStatusCommand(String command) {
    String dateTimeStr = command.substring("show status on ".length()).trim();
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    return new ShowStatusCommand(dateTime);
  }

  /**
   * Parses weekday abbreviations into a set of DayOfWeek.
   * M=Monday, T=Tuesday, W=Wednesday, R=Thursday, F=Friday, S=Saturday, U=Sunday
   */
  private Set<DayOfWeek> parseWeekdays(String weekdayStr) {
    Set<DayOfWeek> weekdays = new HashSet<>();
    for (char c : weekdayStr.toCharArray()) {
      switch (c) {
        case 'M':
          weekdays.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          weekdays.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          weekdays.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          weekdays.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          weekdays.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          weekdays.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          weekdays.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid weekday character: " + c);
      }
    }
    return weekdays;
  }
}