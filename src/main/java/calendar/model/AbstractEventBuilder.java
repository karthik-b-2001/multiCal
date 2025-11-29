package calendar.model;

import java.time.LocalDateTime;

/**
 * Abstract builder class for constructing Event objects.
 * Uses the builder pattern with method chaining to set event properties.
 *
 * @param <T> the type of the concrete builder subclass
 */
public abstract class AbstractEventBuilder<T extends AbstractEventBuilder<T>> {
  protected String subject;
  protected LocalDateTime startDateTime;
  protected LocalDateTime endDateTime;
  protected String description;
  protected String location;
  protected boolean isAllDay;
  protected EventStatus status;
  protected String seriesId;

  /**
   * Constructs a new AbstractEventBuilder with default values.
   * Sets isAllDay to false and status to PUBLIC.
   */
  public AbstractEventBuilder() {
    this.isAllDay = false;
    this.status = EventStatus.PUBLIC;
  }

  /**
   * Builds and returns the Event object.
   *
   * @return the constructed Event
   * @throws IllegalArgumentException if required fields are missing or invalid
   */
  abstract Event build() throws IllegalArgumentException;

  /**
   * Returns the concrete builder instance for method chaining.
   *
   * @return this builder instance
   */
  abstract T returnBuilder();

  /**
   * Sets the subject of the event.
   *
   * @param subject the event subject
   * @return this builder for method chaining
   */
  public T setSubject(String subject) {
    this.subject = subject;
    return returnBuilder();
  }

  /**
   * Sets the start date and time of the event.
   *
   * @param startDateTime the start date and time
   * @return this builder for method chaining
   */
  public T setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
    return returnBuilder();
  }

  /**
   * Sets the end date and time of the event.
   *
   * @param endDateTime the end date and time
   * @return this builder for method chaining
   */
  public T setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
    return returnBuilder();
  }

  /**
   * Sets the description of the event.
   *
   * @param description the event description
   * @return this builder for method chaining
   */
  public T setDescription(String description) {
    this.description = description;
    return returnBuilder();
  }

  /**
   * Sets the location of the event.
   *
   * @param location the event location
   * @return this builder for method chaining
   */
  public T setLocation(String location) {
    this.location = location;
    return returnBuilder();
  }

  /**
   * Sets whether the event is an all-day event.
   *
   * @param isAllDay true if all-day event, false otherwise
   * @return this builder for method chaining
   */
  public T setIsAllDay(boolean isAllDay) {
    this.isAllDay = isAllDay;
    return returnBuilder();
  }

  /**
   * Sets the status of the event (PUBLIC or PRIVATE).
   *
   * @param status the event status
   * @return this builder for method chaining
   */
  public T setStatus(EventStatus status) {
    this.status = status;
    return returnBuilder();
  }

  /**
   * Sets the series ID for recurring events.
   *
   * @param seriesId the series identifier
   * @return this builder for method chaining
   */
  public T setSeriesId(String seriesId) {
    this.seriesId = seriesId;
    return returnBuilder();
  }
}