package calendar.model;

import calendar.model.exceptions.DuplicateEventException;
import calendar.model.exceptions.EventNotFoundException;
import calendar.model.exceptions.UnclearEventException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the Calendar interface.
 */
public class CalendarImpl implements Calendar {

  private final Set<Event> eventSet;
  private String name;
  private ZoneId timeZone;
  private int seriesCounter;


  /**
   * Constructor with specified name and time zone.
   *
   * @param name     the name of the calendar
   * @param timeZone the time zone of the calendar
   * @throws IllegalArgumentException if name is null/blank or timeZone is null
   */
  public CalendarImpl(String name, ZoneId timeZone) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or blank");
    }

    if (timeZone == null) {
      throw new IllegalArgumentException("Time zone cannot be null");
    }
    this.name = name;
    this.timeZone = timeZone;
    this.eventSet = new HashSet<>();
    this.seriesCounter = 0;
  }

  @Override
  public void addEvent(Event event) throws DuplicateEventException {
    if (event == null) {
      throw new IllegalArgumentException("Event cannot be null");
    }

    addEventHelper(event);
  }

  @Override
  public void createEventSeries(String subject, LocalDate startDate, LocalTime startTime,
                                LocalTime endTime, Set<DayOfWeek> weekDays, int occurrences)
      throws IllegalArgumentException, DuplicateEventException {

    if (occurrences <= 0) {
      throw new IllegalArgumentException("Occurrences cannot be 0 or less then 0");
    }

    if (weekDays.isEmpty()) {
      throw new IllegalArgumentException("One Weekday must be specified");
    }

    if (endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("Endtime cannot be before startTime");
    }

    String seriesId = generateSeriesId();
    int count = 0;
    LocalDate currentDate = startDate;

    while (count < occurrences) {
      if (weekDays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime start = LocalDateTime.of(currentDate, startTime);
        LocalDateTime end = LocalDateTime.of(currentDate, endTime);
        Event e = new EventImpl.EventBuilder().setSeriesId(seriesId).setSubject(subject)
            .setStartDateTime(start).setEndDateTime(end).build();

        addEventHelper(e);
        count++;

      }
      currentDate = currentDate.plusDays(1);
    }


  }


  @Override
  public void createEventSeriesTill(String subject, LocalDate startDate, LocalTime startTime,
                                    LocalTime endTime, Set<DayOfWeek> weekDays, LocalDate endDate)
      throws IllegalArgumentException, DuplicateEventException {

    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("Endtime cannot be before startTime");
    }

    if (weekDays.isEmpty()) {
      throw new IllegalArgumentException("Must have one week day");
    }

    if (!startTime.isBefore(endTime)) {
      throw new IllegalArgumentException("start time cannot be after end time");
    }

    String seriesId = generateSeriesId();

    LocalDate current = startDate;

    while (!current.isAfter(endDate)) {
      if (weekDays.contains(current.getDayOfWeek())) {
        LocalDateTime start = LocalDateTime.of(current, startTime);
        LocalDateTime end = LocalDateTime.of(current, endTime);

        Event e = new EventImpl.EventBuilder().setSubject(subject).setStartDateTime(start)
            .setEndDateTime(end).setSeriesId(seriesId).build();

        addEventHelper(e);
      }
      current = current.plusDays(1);
    }
  }

  @Override
  public void createAndAddEvent(String subject, LocalDateTime startDateTime,
                                LocalDateTime endDateTime, boolean isAllDay)
      throws DuplicateEventException {
    Event event = new EventImpl.EventBuilder().setSubject(subject).setStartDateTime(startDateTime)
        .setEndDateTime(endDateTime).setIsAllDay(isAllDay).build();
    addEventHelper(event);
  }



  @Override
  public void createAllDayEventSeries(String subject, LocalDate startDate, Set<DayOfWeek> weekDays,
                                      int occurrences)
      throws IllegalArgumentException, DuplicateEventException {

    if (occurrences <= 0) {
      throw new IllegalArgumentException("occurrences cannot be 0 or less than 0");
    }

    if (weekDays.isEmpty()) {
      throw new IllegalArgumentException("Must have a week day");
    }

    String seriesId = generateSeriesId();

    int count = 0;
    LocalDate current = startDate;

    while (count < occurrences) {
      if (weekDays.contains(current.getDayOfWeek())) {
        Event e = new EventImpl.EventBuilder().setSubject(subject)
            .setStartDateTime(LocalDateTime.of(current, LocalTime.of(8, 0))).setIsAllDay(true)
            .setSeriesId(seriesId).build();

        addEventHelper(e);
        count++;
      }
      current = current.plusDays(1);
    }
  }

  @Override
  public void createAllDayEventSeriesTill(String subject, LocalDate startDate,
                                          Set<DayOfWeek> weekDays, LocalDate endDate)
      throws IllegalArgumentException, DuplicateEventException {

    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("Endtime cannot be before startTime");
    }

    if (weekDays.isEmpty()) {
      throw new IllegalArgumentException("Must have one week day");
    }


    String seriesId = generateSeriesId();
    LocalDate current = startDate;

    while (!current.isAfter(endDate)) {
      if (weekDays.contains(current.getDayOfWeek())) {
        Event e = new EventImpl.EventBuilder().setSubject(subject)
            .setStartDateTime(LocalDateTime.of(current, LocalTime.of(8, 0))).setIsAllDay(true)
            .setSeriesId(seriesId).build();

        addEventHelper(e);
      }
      current = current.plusDays(1);
    }
  }

  @Override
  public void editEvent(String subject, LocalDateTime startDateTime, String property,
                        Object newValue, EditSettings scope)
      throws EventNotFoundException, UnclearEventException, DuplicateEventException {

    Event e = findUniqueEventHelper(subject, startDateTime);

    if (scope == EditSettings.SINGLE) {
      editSingleEvent(e, property, newValue);
    } else if (scope == EditSettings.FORWARD) {
      editForwardEvents(e, property, newValue);
    } else {
      editAllEventsInSeries(e, property, newValue);
    }
  }

  private Event findUniqueEventHelper(String subject, LocalDateTime startDateTime)
      throws EventNotFoundException, UnclearEventException {
    List<Event> existingEvents = eventSet.stream()
        .filter(e -> e.getSubject().equals(subject) && e.getStartDateTime().equals(startDateTime))
        .collect(Collectors.toList());

    if (existingEvents.isEmpty()) {
      throw new EventNotFoundException("Event not found unable to edit");
    }

    if (existingEvents.size() > 1) {
      throw new UnclearEventException("Event repeating, unable to edit");
    }

    return existingEvents.get(0);
  }

  private void editSingleEvent(Event e, String property, Object newValue)
      throws DuplicateEventException {
    Event res = createModifiedEvent(e, property, newValue);

    if (property.equalsIgnoreCase("start") || property.equalsIgnoreCase("end")) {
      res = constructEventWithoutSeries(res);
    }


    eventSet.remove(e);
    addEventHelper(res);
  }

  private void editForwardEvents(Event e, String property, Object newValue)
      throws DuplicateEventException {
    if (e.isInSeries()) {
      String sid = e.getSeriesId().get();
      List<Event> eventInSeries = getEventsInSeries(sid);
      List<Event> eventsToMutate =
          eventInSeries.stream().filter(o -> !o.getStartDateTime().isBefore(e.getStartDateTime()))
              .collect(Collectors.toList());

      if (property.equalsIgnoreCase("start") || property.equalsIgnoreCase("end")) {
        editTimeForwardInSeries(eventsToMutate, property, newValue);
      } else {
        editPropertyForwardInSeries(eventsToMutate, property, newValue);
      }
    } else {
      editSingleEvent(e, property, newValue);
    }
  }

  private void editTimeForwardInSeries(List<Event> eventsToMutate, String property, Object newValue)
      throws DuplicateEventException {
    String newSeriesId = generateSeriesId();

    for (Event event : eventsToMutate) {
      Event res = createModifiedEvent(event, property, newValue);
      res = replaceEventSeriesId(res, newSeriesId);


      eventSet.remove(event);
      addEventHelper(res);
    }
  }

  private void editPropertyForwardInSeries(List<Event> eventsToMutate, String property,
                                           Object newValue) throws DuplicateEventException {
    for (Event event : eventsToMutate) {
      Event res = createModifiedEvent(event, property, newValue);

      eventSet.remove(event);
      addEventHelper(res);
    }
  }

  private void editAllEventsInSeries(Event e, String property, Object newValue)
      throws DuplicateEventException {
    if (e.isInSeries()) {
      String id = e.getSeriesId().get();
      List<Event> eventInSeries = getEventsInSeries(id);

      for (Event event : eventInSeries) {
        Event res = createModifiedEvent(event, property, newValue);


        eventSet.remove(event);
        addEventHelper(res);
      }
    } else {
      editSingleEvent(e, property, newValue);
    }
  }

  @Override
  public List<Event> getEventOnDate(LocalDate date) {
    List<Event> res;

    res = eventSet.stream().filter(e -> doesOverlap(e, date))
        .sorted(Comparator.comparing(Event::getStartDateTime)).collect(
            Collectors.toList());
    return Collections.unmodifiableList(res);
  }

  @Override
  public List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return eventSet.stream()
        .filter(e -> isInRange(e.getStartDateTime(), e.getEndDateTime(), start, end))
        .sorted(Comparator.comparing(Event::getStartDateTime))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    return eventSet.stream().anyMatch(e ->
        (dateTime.isAfter(e.getStartDateTime()) || dateTime.equals(e.getStartDateTime()))
            && dateTime.isBefore(e.getEndDateTime()));
  }

  @Override
  public List<Event> getAllEvents() {
    return List.copyOf(eventSet);
  }

  @Override
  public String getCalendarName() {
    return this.name;
  }

  @Override
  public void setCalendarName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or blank");
    }
    this.name = name;
  }

  @Override
  public ZoneId getTimeZone() {
    return this.timeZone;
  }

  @Override
  public void setTimeZone(ZoneId z) {
    if (z == null) {
      throw new IllegalArgumentException("Time zone cannot be null");
    }
    if (this.timeZone.equals(z)) {
      return;
    }

    ZoneId tempZone = this.timeZone;
    Set<Event> updatedEvents =
        eventSet.parallelStream().map(event -> convertEventToTimezone(event, tempZone, z))
            .collect(Collectors.toSet());

    this.eventSet.clear();
    this.eventSet.addAll(updatedEvents);
    this.timeZone = z;
  }

  @Override
  public Event findEvent(String subject, LocalDateTime startDateTime)
      throws EventNotFoundException, UnclearEventException {

    return findUniqueEventHelper(subject, startDateTime);
  }

  private Event convertEventToTimezone(Event event, ZoneId fromZone, ZoneId toZone) {
    ZonedDateTime oldStart = event.getStartDateTime().atZone(fromZone);
    ZonedDateTime oldEnd = event.getEndDateTime().atZone(fromZone);

    ZonedDateTime newStart = oldStart.withZoneSameInstant(toZone);
    ZonedDateTime newEnd = oldEnd.withZoneSameInstant(toZone);

    return event.copyWithNewTimes(newStart.toLocalDateTime(), newEnd.toLocalDateTime());
  }


  /**
   * Retrieves all events belonging to a specific series.
   *
   * @param sid the series ID to search for
   * @return list of events in the series, empty if none found
   */
  private List<Event> getEventsInSeries(String sid) {
    return eventSet.stream()
        .filter(e -> e.getSeriesId().isPresent() && sid.equals(e.getSeriesId().get()))
        .collect(Collectors.toList());
  }

  /**
   * Creates a modified copy of an event with one property changed.
   *
   * @param e        the original event to modify
   * @param property the property name to change(subject, start, end, description, location, status)
   * @param newValue the new value for the property
   * @return a new event with the modified property
   * @throws IllegalArgumentException if property name is invalid or newValue type is incorrect
   */
  private Event createModifiedEvent(Event e, String property, Object newValue) {
    EventImpl.EventBuilder b = new EventImpl.EventBuilder().setSubject(e.getSubject())
        .setStartDateTime(e.getStartDateTime()).setEndDateTime(e.getEndDateTime())
        .setIsAllDay(e.isAllDayEvent());

    if (e.getDescription().isPresent()) {
      b.setDescription(e.getDescription().get());
    }

    b.setLocation(e.getLocation());

    if (e.getSeriesId().isPresent()) {
      b.setSeriesId(e.getSeriesId().get());
    }

    if (property.equalsIgnoreCase("subject")) {
      b.setSubject(newValue.toString());

    } else if (property.equalsIgnoreCase("start")) {
      LocalDate originalDate = e.getStartDateTime().toLocalDate();
      LocalTime newTime = ((LocalDateTime) newValue).toLocalTime();
      b.setStartDateTime(LocalDateTime.of(originalDate, newTime));

    } else if (property.equalsIgnoreCase("end")) {
      LocalDate originalDate = e.getEndDateTime().toLocalDate();
      LocalTime newTime = ((LocalDateTime) newValue).toLocalTime();
      b.setEndDateTime(LocalDateTime.of(originalDate, newTime));

    } else if (property.equalsIgnoreCase("description")) {
      b.setDescription(newValue.toString());

    } else if (property.equalsIgnoreCase("location")) {
      if (newValue instanceof LocationType) {
        b.setLocation((LocationType) newValue);
      } else {
        throw new IllegalArgumentException("Invalid location type");
      }

    } else if (property.equalsIgnoreCase("status")) {
      if (newValue instanceof EventStatus) {
        b.setStatus((EventStatus) newValue);

      } else {
        throw new IllegalArgumentException("Invalid Status");

      }
    } else {
      throw new IllegalArgumentException("Invalid property entered");

    }

    return b.build();
  }

  /**
   * Creates a copy of an event with a new series ID.
   *
   * @param e   the event to copy
   * @param sid the new series ID to assign
   * @return a new event with the updated series ID
   */
  private Event replaceEventSeriesId(Event e, String sid) {
    EventImpl.EventBuilder b = new EventImpl.EventBuilder().setSubject(e.getSubject())
        .setStartDateTime(e.getStartDateTime()).setEndDateTime(e.getEndDateTime())
        .setIsAllDay(e.isAllDayEvent()).setSeriesId(sid);

    b.setDescription(e.getDescription().orElse(null));

    b.setLocation(e.getLocation());

    b.setStatus(e.getStatus());

    return b.build();
  }

  /**
   * Creates a copy of an event without any series association.
   * Used when editing start/end time of a series event to break it from the series.
   *
   * @param e the event to copy
   * @return a new event without a series ID
   */
  private Event constructEventWithoutSeries(Event e) {
    EventImpl.EventBuilder b = new EventImpl.EventBuilder().setSubject(e.getSubject())
        .setStartDateTime(e.getStartDateTime()).setEndDateTime(e.getEndDateTime())
        .setIsAllDay(e.isAllDayEvent()).setStatus(e.getStatus());

    b.setDescription(e.getDescription().orElse(null));
    b.setLocation(e.getLocation());
    return b.build();
  }

  /**
   * Checks if an event overlaps with a given date.
   * An event overlaps if the date falls on its start date, end date, or anywhere in between.
   *
   * @param e the event to check
   * @param d the date to check against
   * @return true if the event overlaps the date, false otherwise
   */
  private boolean doesOverlap(Event e, LocalDate d) {
    LocalDate start = e.getStartDateTime().toLocalDate();
    LocalDate end = e.getEndDateTime().toLocalDate();
    return d.equals(start) || d.equals(end) || (d.isAfter(start) && d.isBefore(end));
  }

  /**
   * Checks if two time ranges overlap.
   * Ranges overlap if the first starts before the second ends AND the second starts before
   * the first ends.
   *
   * @param start1 start of first range
   * @param end1 end of first range
   * @param start2 start of second range
   * @param end2 end of second range
   * @return true if ranges overlap, false otherwise
   */
  private boolean isInRange(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2,
                            LocalDateTime end2) {

    return (start1.isBefore(end2) && start2.isBefore(end1));
  }

  /**
   * Generates a unique series ID for event series.
   *
   * @return a new unique series ID
   */
  private String generateSeriesId() {
    return "SID_" + (++seriesCounter);
  }

  private void addEventHelper(Event event) throws DuplicateEventException {
    if (eventSet.contains(event)) {
      throw new DuplicateEventException("Event already exists in set");
    }

    eventSet.add(event);

  }

}
