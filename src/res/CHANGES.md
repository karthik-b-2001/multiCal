# Changes done overall

## This file tracks the changes made to the design for this and the future assignment.

### Model Changes
1. Identified a few concrete class dependencies and ensured dependency inversion is followed.
2. Changed public methods, making them less dependent on each other.
3. Added a Facade, CalendarManager to manage multiple calenders.
4. Added methods to the event and calendar interface to extend functionality.

### Controller Changes
1. Added new command to create and use multiple calendars.
2. Added new command to edit calendar properties.
3. Added new command to copy events between different calendars.
4. Updated existing commands to support multiple calendars.
5. Refactored command parsing logic to handle new commands and options.
6. Made changes to export functionality to support ical and csv exports from multiple calendars.
7. Initially we used Calendar, but now we are using CalendarManager to manage multiple calendars.

### View Changes
1. A graphical user interface (GUI) has been implemented using Java Swing.
2. The GUI supports a month view of the calendar, allowing users to navigate between months.
3. Users can create, edit, and view events through the GUI.
4. The GUI allows users to create and manage multiple calendars in different timezones.
5. Error handling has been implemented to provide user-friendly messages for invalid input.


A more detailed list of changes is available in the Misc.md file in the res folder.