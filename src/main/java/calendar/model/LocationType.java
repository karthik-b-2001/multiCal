package calendar.model;

/**
 * Enum representing the type of location for an event.
 */
public enum LocationType {
  NONE(""), PHYSICAL("Physical"), ONLINE("Online");

  private final String displayValue;

  LocationType(String displayValue) {
    this.displayValue = displayValue;
  }

  /**
   * Gets the display string for this location type.
   *
   * @return the string representation
   */
  public String getDisplayValue() {
    return displayValue;
  }

  /**
   * Converts a display string to LocationType.
   *
   * @param value the display value
   * @return the corresponding LocationType
   */
  public static LocationType fromDisplayValue(String value) {
    for (LocationType type : values()) {
      if (type.displayValue.equalsIgnoreCase(value)) {
        return type;
      }
    }
    return NONE;
  }
}