package calendar.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the Event interface representing an immutable calendar event.
 * Events have a subject, start and end date-times, and optional properties such as
 * description, location, and status. Events can be part of a recurring series.
 */
public class EventImpl implements Event {

  private final String subject;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final String description;
  private final String location;
  private final EventStatus status;
  private final String seriesId;
  private final boolean isAllDay;

  /**
   * Constructs an EventImpl instance.
   *
   * @param subject       the subject (title) of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @param description   the optional description of the event
   * @param location      the optional location of the event
   * @param status        the status of the event (PUBLIC or PRIVATE)
   * @param seriesId      the optional series ID if the event is part of a recurring series
   * @param isAllDay      flag indicating if this is an all-day event
   */
  EventImpl(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
            String description, String location, EventStatus status, String seriesId,
            boolean isAllDay) {
    this.subject = subject;
    this.status = status;
    this.description = description;
    this.location = location;
    this.seriesId = seriesId;
    this.isAllDay = isAllDay;

    if (this.isAllDay) {
      this.startDateTime = startDateTime
          .withHour(8)
          .withMinute(0)
          .withSecond(0)
          .withNano(0);
      this.endDateTime = startDateTime
          .withHour(17)
          .withMinute(0)
          .withSecond(0)
          .withNano(0);
    } else {
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
    }
  }


  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return this.startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  @Override
  public Optional<String> getLocation() {
    return Optional.ofNullable(location);
  }

  @Override
  public EventStatus getStatus() {
    return status;
  }

  @Override
  public boolean isAllDayEvent() {
    return isAllDay;
  }

  @Override
  public boolean isInSeries() {
    return seriesId != null;
  }

  @Override
  public Optional<String> getSeriesId() {
    return Optional.ofNullable(seriesId);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Event)) {
      return false;
    }
    Event other = (Event) o;
    return this.subject.equals(other.getSubject())
        && this.startDateTime.equals(other.getStartDateTime())
        && this.endDateTime.equals(other.getEndDateTime());
  }

  @Override
  public Event copyWithNewTimes(LocalDateTime newStart, LocalDateTime newEnd) {
    EventBuilder b = new EventBuilder().setSubject(this.subject).setStartDateTime(newStart)
        .setEndDateTime(newEnd).setDescription(this.description).setLocation(this.location)
        .setStatus(this.status).setSeriesId(this.seriesId).setIsAllDay(this.isAllDay);

    return b.build();
  }

  @Override
  public Event copyWithSeriesId(String newSeriesId) {
    return new EventImpl.EventBuilder().setSubject(this.subject)
        .setStartDateTime(this.startDateTime).setEndDateTime(this.endDateTime)
        .setDescription(this.description).setLocation(this.location).setStatus(this.status)
        .setSeriesId(newSeriesId)  // NEW series ID (can be null)
        .setIsAllDay(this.isAllDay).build();
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, startDateTime, endDateTime);
  }

  /**
   * Builder class for constructing EventImpl instances.
   */

  protected static class EventBuilder extends AbstractEventBuilder<EventBuilder> {

    @Override
    public Event build() throws IllegalArgumentException {
      if (this.subject == null || this.subject.trim().isEmpty()) {
        throw new IllegalArgumentException("Subject cannot be null or empty.");
      }

      if (this.startDateTime == null) {
        throw new IllegalArgumentException("Start DateTime cannot be null.");
      }

      if (!this.isAllDay && this.endDateTime == null) {
        throw new IllegalArgumentException("End DateTime is required for timed events.");
      }

      if (!this.isAllDay && !this.startDateTime.isBefore(this.endDateTime)) {
        throw new IllegalArgumentException("Start DateTime must be before End DateTime.");
      }

      return new EventImpl(subject, startDateTime, endDateTime, description, location, status,
          seriesId, isAllDay);
    }

    @Override
    protected EventBuilder returnBuilder() {
      return this;
    }
  }
}
