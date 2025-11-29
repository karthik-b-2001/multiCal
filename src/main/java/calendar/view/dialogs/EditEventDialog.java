package calendar.view.dialogs;

import calendar.controller.GuiController;
import calendar.model.EditSettings;
import calendar.model.Event;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for editing an existing event.
 */
public class EditEventDialog extends JDialog {

  private final Event event;
  private final GuiController controller;

  private JTextField subjectField;
  private JComboBox<Integer> startHourCombo;
  private JComboBox<Integer> startMinuteCombo;
  private JComboBox<Integer> endHourCombo;
  private JComboBox<Integer> endMinuteCombo;
  private JTextField locationField;
  private JTextArea descriptionArea;
  private JComboBox<String> scopeCombo;

  /**
   * Constructs the EditEventDialog.
   *
   * @param parent     the parent frame
   * @param event      the event to edit
   * @param controller the GUI controller
   */
  public EditEventDialog(JFrame parent, Event event, GuiController controller) {
    super(parent, "Edit Event", true);
    this.event = event;
    this.controller = controller;
    initializeDialog();
  }

  private void initializeDialog() {
    setSize(500, 500);
    setLocationRelativeTo(getParent());
    setLayout(new BorderLayout(10, 10));

    add(createContentPanel(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel createContentPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

    panel.add(createLabelFieldPair("Event Name:",
        subjectField = new JTextField(event.getSubject())));

    if (!event.isAllDayEvent()) {
      panel.add(createTimePanel("Start Time:", true));
      panel.add(createTimePanel("End Time:", false));
    } else {
      JLabel allDayLabel = new JLabel("(All Day Event)");
      allDayLabel.setFont(new Font("Arial", Font.ITALIC, 14));
      panel.add(allDayLabel);
      panel.add(Box.createVerticalStrut(10));
    }

    panel.add(createLabelFieldPair("Location:",
        locationField = new JTextField(event.getLocation().orElse(""))));

    JLabel descLabel = new JLabel("Description:");
    panel.add(descLabel);
    descriptionArea = new JTextArea(event.getDescription().orElse(""), 4, 30);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    JScrollPane descScroll = new JScrollPane(descriptionArea);
    descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    panel.add(descScroll);

    panel.add(Box.createVerticalStrut(10));

    if (event.isInSeries()) {
      panel.add(new JLabel("Edit Scope:"));
      scopeCombo = new JComboBox<>(
          new String[]{"This event only", "This and future events", "All events in series"});
      panel.add(scopeCombo);
    }

    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

    JButton saveBtn = new JButton("Save Changes");
    saveBtn.setPreferredSize(new Dimension(130, 35));
    saveBtn.addActionListener(e -> handleSave());
    panel.add(saveBtn);

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 35));
    cancelBtn.addActionListener(e -> dispose());
    panel.add(cancelBtn);

    return panel;
  }

  private JPanel createLabelFieldPair(String labelText, JTextField field) {
    JPanel pair = new JPanel(new BorderLayout(5, 5));
    pair.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    JLabel label = new JLabel(labelText);
    label.setPreferredSize(new Dimension(120, 25));
    pair.add(label, BorderLayout.WEST);
    pair.add(field, BorderLayout.CENTER);

    return pair;
  }

  private JPanel createTimePanel(String label, boolean isStart) {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    JLabel timeLabel = new JLabel(label);
    timeLabel.setPreferredSize(new Dimension(120, 25));
    panel.add(timeLabel, BorderLayout.WEST);

    JComboBox<Integer> hourCombo = new JComboBox<>();
    for (int i = 0; i < 24; i++) {
      hourCombo.addItem(i);
    }

    JComboBox<Integer> minuteCombo = new JComboBox<>();
    for (int i = 0; i < 60; i += 15) {
      minuteCombo.addItem(i);
    }

    LocalTime time =
        isStart ? event.getStartDateTime().toLocalTime() : event.getEndDateTime().toLocalTime();
    hourCombo.setSelectedItem(time.getHour());

    int minute = time.getMinute();
    int closestMinute = (minute / 15) * 15;
    minuteCombo.setSelectedItem(closestMinute);

    if (isStart) {
      startHourCombo = hourCombo;
      startMinuteCombo = minuteCombo;
    } else {
      endHourCombo = hourCombo;
      endMinuteCombo = minuteCombo;
    }

    JPanel timeInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    timeInputs.add(hourCombo);
    timeInputs.add(new JLabel(":"));
    timeInputs.add(minuteCombo);
    panel.add(timeInputs, BorderLayout.CENTER);

    return panel;
  }

  private void handleSave() {
    if (!validateInput()) {
      return;
    }

    EditSettings scope = getEditScope();
    boolean success = true;
    String currentSubject = event.getSubject();
    LocalDateTime currentStartDateTime = event.getStartDateTime();

    if (success && !event.isAllDayEvent() && startHourCombo != null && endHourCombo != null) {
      LocalTime newStart = LocalTime.of(
          (Integer) startHourCombo.getSelectedItem(),
          (Integer) startMinuteCombo.getSelectedItem());
      LocalTime newEnd = LocalTime.of(
          (Integer) endHourCombo.getSelectedItem(),
          (Integer) endMinuteCombo.getSelectedItem());

      LocalTime originalStart = event.getStartDateTime().toLocalTime();
      LocalTime originalEnd = event.getEndDateTime().toLocalTime();

      if (!newEnd.equals(originalEnd)) {
        LocalDateTime newEndDateTime = event.getEndDateTime().with(newEnd);
        if (!editEventProperty(currentSubject, currentStartDateTime, "end",
            newEndDateTime, scope)) {
          success = false;
        }
      }

      if (success && !newStart.equals(originalStart)) {
        LocalDateTime newStartDateTime = event.getStartDateTime().with(newStart);
        if (!editEventProperty(currentSubject, currentStartDateTime, "start",
            newStartDateTime, scope)) {
          success = false;
        } else {
          currentStartDateTime = newStartDateTime;
        }
      }
    }

    String newSubject = subjectField.getText().trim();
    if (success && !newSubject.equals(event.getSubject()) && !newSubject.isEmpty()) {
      if (!editEventProperty(currentSubject, currentStartDateTime, "subject",
          newSubject, scope)) {
        success = false;
      } else {
        currentSubject = newSubject;
      }
    }

    if (success) {
      String newLocation = locationField.getText().trim();
      String originalLocation = event.getLocation().orElse("");
      if (!newLocation.equals(originalLocation)) {
        if (!editEventProperty(currentSubject, currentStartDateTime, "location",
            newLocation, scope)) {
          success = false;
        }
      }
    }

    if (success) {
      String newDescription = descriptionArea.getText().trim();
      String originalDescription = event.getDescription().orElse("");
      if (!newDescription.equals(originalDescription)) {
        if (!editEventProperty(currentSubject, currentStartDateTime, "description",
            newDescription, scope)) {
          success = false;
        }
      }
    }

    if (success) {
      dispose();
    }
  }

  private boolean editEventProperty(String subject, LocalDateTime startDateTime,
                                    String property, Object newValue, EditSettings scope) {
    try {
      controller.editEvent(subject, startDateTime, property, newValue, scope);
      return true;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Failed to edit event: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  private boolean validateInput() {
    if (subjectField.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Event name cannot be empty",
          "Validation Error",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (!event.isAllDayEvent() && startHourCombo != null && endHourCombo != null) {
      LocalTime newStart = LocalTime.of(
          (Integer) startHourCombo.getSelectedItem(),
          (Integer) startMinuteCombo.getSelectedItem());
      LocalTime newEnd = LocalTime.of(
          (Integer) endHourCombo.getSelectedItem(),
          (Integer) endMinuteCombo.getSelectedItem());

      if (!newStart.isBefore(newEnd)) {
        JOptionPane.showMessageDialog(this,
            "Start time must be before end time",
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }

    return true;
  }

  private EditSettings getEditScope() {
    if (!event.isInSeries() || scopeCombo == null) {
      return EditSettings.SINGLE;
    }

    int index = scopeCombo.getSelectedIndex();
    switch (index) {
      case 1:
        return EditSettings.FORWARD;
      case 2:
        return EditSettings.ALL_EVENTS;
      default:
        return EditSettings.SINGLE;
    }
  }
}