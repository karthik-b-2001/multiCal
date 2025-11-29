## Design Changes
###  GUI Controller and View Interfaces
- **Added:** `GuiController` interface and `GuiControllerImpl` implementation
- **Added:** `GuiView` interface and `GuiViewImpl` implementation
- this separates GUI logic from text-based controller, allows different view implementations without changing the controller logic.
- The calendar runner can switch between GUI and text-based views seamlessly based on how the jar file is run.

### Refactoring the command parser.
- Replaced if-else ladder with HashMap-based command routing in `CommandParserImpl`.
- Introduced `CommandParser` interface to ensure dependency inversion.
- Follows the open-closed principle, allowing easy addition of new commands without modifying existing code.

### Refactoring the export functionality.
- Using `ExportFactory` to create appropriate exporter instances based on the requested format.
- Added `IcalExporter` and `CsvExporter` classes implementing the `Exporter` interface.
- Makes adding new export formats straightforward with minimal change in existing code.

## Features

### Working Features
- Create new calendar with any timezone
- Switch between multiple calendars
- Visual indication of active calendar (colored banner)
- Default "Personal" calendar created automatically
- Month view with navigation (previous/next month)
- View events on a specific day
- Create single events
- Create recurring events with weekday selection
- Recurring events with number of occurrences
- Recurring events with end date
- Edit single event
- Edit series events (this event only, this and future, all in series)
- Headless mode with script file
- Interactive text mode
- GUI mode

### Non-Working Features
- None. All required features are implemented.

## Additional Notes
- The GUI uses color coding to distinguish calendars
- Days with events are highlighted with the calendar's color
- Today's date is highlighted with a blue border
- All user inputs use dropdowns, spinners, and checkboxes for user-friendly interaction.
- The `setTimeZone` method is updated to change the start and end times of existing events when the calendar's timezone is changed.
- Error messages are displayed via dialog boxes without exposing implementation details.