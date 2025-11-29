package calendar.model;

/**
 * Enum representing the edit settings for recurring events.
 * SINGLE: Edit only the selected occurrence.
 * FORWARD: Edit the selected occurrence and all future occurrences.
 * ALL_EVENTS: Edit all occurrences in the series.
 */
public enum EditSettings {
  SINGLE,
  FORWARD,
  ALL_EVENTS
}
