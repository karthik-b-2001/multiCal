package calendar.view.dialogs;

import calendar.controller.GuiController;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

/**
 * Dialog for creating a new event.
 */
public class CreateEventDialog extends AbstractDialog {

  private final LocalDate defaultDate;

  private JTextField subjectField;
  private JSpinner dateSpinner;
  private JComboBox<Integer> startHourCombo;
  private JComboBox<Integer> startMinuteCombo;
  private JComboBox<Integer> endHourCombo;
  private JComboBox<Integer> endMinuteCombo;
  private JCheckBox allDayCheckbox;

  private JCheckBox recurringCheckbox;
  private JPanel recurringPanel;
  private JCheckBox[] weekdayCheckboxes;
  private JRadioButton occurrencesRadio;
  private JRadioButton endDateRadio;
  private JSpinner occurrencesSpinner;
  private JSpinner endDateSpinner;

  /**
   * Constructs the CreateEventDialog.
   *
   * @param parent     the parent frame
   * @param controller the GUI controller
   * @param date       the default date for the event
   */
  public CreateEventDialog(JFrame parent, GuiController controller, LocalDate date) {
    super(parent, "Create Event", controller);
    this.defaultDate = date;
    initializeDialog(500, 600);
  }

  @Override
  protected JPanel createContentPanel() {
    JPanel main = new JPanel();
    main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
    main.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

    main.add(createBasicInfoPanel());
    main.add(Box.createVerticalStrut(10));

    recurringPanel = createRecurringPanel();
    recurringPanel.setVisible(false);
    main.add(recurringPanel);

    return main;
  }

  private JPanel createBasicInfoPanel() {
    JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

    panel.add(new JLabel("Event Name:"));
    subjectField = new JTextField();
    panel.add(subjectField);

    panel.add(new JLabel("Date:"));
    SpinnerDateModel dateModel = new SpinnerDateModel();
    dateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    dateSpinner.setValue(Date.valueOf(defaultDate));
    panel.add(dateSpinner);

    panel.add(new JLabel("All Day Event:"));
    allDayCheckbox = new JCheckBox();
    allDayCheckbox.addActionListener(e -> toggleTimeFields());
    panel.add(allDayCheckbox);

    panel.add(new JLabel("Start Time:"));
    startHourCombo = new JComboBox<>();
    for (int i = 0; i < 24; i++) {
      startHourCombo.addItem(i);
    }
    startHourCombo.setSelectedItem(9);
    startMinuteCombo = new JComboBox<>();
    for (int i = 0; i < 60; i += 15) {
      startMinuteCombo.addItem(i);
    }
    JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    startMinuteCombo.setSelectedItem(0);
    startTimePanel.add(startHourCombo);
    startTimePanel.add(new JLabel(":"));
    startTimePanel.add(startMinuteCombo);
    panel.add(startTimePanel);

    panel.add(new JLabel("End Time:"));
    endHourCombo = new JComboBox<>();
    for (int i = 0; i < 24; i++) {
      endHourCombo.addItem(i);
    }
    endHourCombo.setSelectedItem(10);
    endMinuteCombo = new JComboBox<>();
    for (int i = 0; i < 60; i += 15) {
      endMinuteCombo.addItem(i);
    }
    endMinuteCombo.setSelectedItem(0);
    JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    endTimePanel.add(endHourCombo);
    endTimePanel.add(new JLabel(":"));
    endTimePanel.add(endMinuteCombo);
    panel.add(endTimePanel);

    panel.add(new JLabel("Recurring Event:"));
    recurringCheckbox = new JCheckBox();
    recurringCheckbox.addActionListener(e -> toggleRecurringPanel());
    panel.add(recurringCheckbox);

    return panel;
  }

  private JPanel createRecurringPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder("Recurrence Options"));

    JPanel weekdayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    weekdayPanel.add(new JLabel("Repeat on:"));
    String[] dayLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    weekdayCheckboxes = new JCheckBox[7];
    for (int i = 0; i < 7; i++) {
      weekdayCheckboxes[i] = new JCheckBox(dayLabels[i]);
      weekdayPanel.add(weekdayCheckboxes[i]);
    }
    panel.add(weekdayPanel);

    panel.add(Box.createVerticalStrut(10));

    ButtonGroup endGroup = new ButtonGroup();

    occurrencesRadio = new JRadioButton("Number of times:");
    occurrencesRadio.setSelected(true);
    endGroup.add(occurrencesRadio);
    JPanel endConditionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    endConditionPanel.add(occurrencesRadio);

    occurrencesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
    endConditionPanel.add(occurrencesSpinner);

    endDateRadio = new JRadioButton("Until date:");
    endGroup.add(endDateRadio);
    endConditionPanel.add(endDateRadio);

    SpinnerDateModel endDateModel = new SpinnerDateModel();
    endDateSpinner = new JSpinner(endDateModel);
    JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
    endDateSpinner.setEditor(endDateEditor);
    Calendar futureDate = Calendar.getInstance();
    futureDate.add(Calendar.MONTH, 1);
    endDateSpinner.setValue(futureDate.getTime());
    endConditionPanel.add(endDateSpinner);

    panel.add(endConditionPanel);

    return panel;
  }

  @Override
  protected void handleOk() {
    if (!validateInput()) {
      return;
    }

    String subject = subjectField.getText().trim();
    LocalDate date = getDateFromSpinner(dateSpinner);
    LocalTime startTime = LocalTime.of((Integer) startHourCombo.getSelectedItem(),
        (Integer) startMinuteCombo.getSelectedItem());
    LocalTime endTime = LocalTime.of((Integer) endHourCombo.getSelectedItem(),
        (Integer) endMinuteCombo.getSelectedItem());
    boolean isAllDay = allDayCheckbox.isSelected();
    boolean isRecurring = recurringCheckbox.isSelected();

    if (isRecurring) {
      Set<DayOfWeek> weekdays = getSelectedWeekdays();
      if (weekdays.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please select at least one day for recurring event",
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      Integer occurrences =
          occurrencesRadio.isSelected() ? (Integer) occurrencesSpinner.getValue() : null;
      LocalDate endDate = endDateRadio.isSelected() ? getDateFromSpinner(endDateSpinner) : null;

      controller.createRecurringEvent(subject, date, startTime, endTime, weekdays, occurrences,
          endDate, isAllDay);
    } else {
      controller.createSingleEvent(subject, date, startTime, endTime, isAllDay);
    }

    dispose();
  }

  private void toggleTimeFields() {
    boolean enabled = !allDayCheckbox.isSelected();
    startHourCombo.setEnabled(enabled);
    startMinuteCombo.setEnabled(enabled);
    endHourCombo.setEnabled(enabled);
    endMinuteCombo.setEnabled(enabled);
  }

  private void toggleRecurringPanel() {
    recurringPanel.setVisible(recurringCheckbox.isSelected());
    pack();
  }

  private Set<DayOfWeek> getSelectedWeekdays() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    DayOfWeek[] days = {
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    };

    for (int i = 0; i < 7; i++) {
      if (weekdayCheckboxes[i].isSelected()) {
        weekdays.add(days[i]);
      }
    }
    return weekdays;
  }

  private LocalDate getDateFromSpinner(JSpinner spinner) {
    java.util.Date date = (java.util.Date) spinner.getValue();
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  @Override
  protected String getOkButtonText() {
    return "Create Event";
  }

  @Override
  protected int getOkButtonWidth() {
    return 130;
  }

  private boolean validateInput() {
    if (subjectField.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Event name cannot be empty",
          "Validation Error",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (!allDayCheckbox.isSelected()) {
      LocalTime start = LocalTime.of((Integer) startHourCombo.getSelectedItem(),
          (Integer) startMinuteCombo.getSelectedItem());
      LocalTime end = LocalTime.of((Integer) endHourCombo.getSelectedItem(),
          (Integer) endMinuteCombo.getSelectedItem());

      if (!start.isBefore(end)) {
        JOptionPane.showMessageDialog(this,
            "Start time must be before end time",
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }

    if (recurringCheckbox.isSelected() && endDateRadio.isSelected()) {
      LocalDate startDate = getDateFromSpinner(dateSpinner);
      LocalDate endDate = getDateFromSpinner(endDateSpinner);

      if (!endDate.isAfter(startDate)) {
        JOptionPane.showMessageDialog(this,
            "End date must be after start date",
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }

    return true;
  }
}