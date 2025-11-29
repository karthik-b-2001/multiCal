package calendar.view.dialogs;

import calendar.controller.GuiController;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Dialog for creating a new calendar.
 */
public class CreateCalendarDialog extends JDialog {

  private final GuiController controller;
  private JTextField nameField;
  private JComboBox<String> timezoneCombo;
  private final List<String> allTimezones;

  /**
   * Constructs the CreateCalendarDialog.
   *
   * @param parent     the parent frame
   * @param controller the GUI controller
   */
  public CreateCalendarDialog(JFrame parent, GuiController controller) {
    super(parent, "Create New Calendar", true);
    this.controller = controller;
    this.allTimezones = new ArrayList<>(ZoneId.getAvailableZoneIds());
    this.allTimezones.sort(String::compareTo);
    initializeDialog();
  }

  private void initializeDialog() {
    setSize(500, 200);
    setLocationRelativeTo(getParent());
    setLayout(new BorderLayout(10, 10));

    add(createContentPanel(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel createContentPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

    JPanel namePanel = new JPanel(new BorderLayout(10, 0));
    namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    JLabel nameLabel = new JLabel("Calendar Name:");
    nameLabel.setPreferredSize(new Dimension(120, 25));
    nameField = new JTextField();
    nameField.setFont(new Font("Arial", Font.PLAIN, 14));
    namePanel.add(nameLabel, BorderLayout.WEST);
    namePanel.add(nameField, BorderLayout.CENTER);
    panel.add(namePanel);

    panel.add(Box.createVerticalStrut(15));

    JPanel tzPanel = new JPanel(new BorderLayout(10, 0));
    tzPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    JLabel tzLabel = new JLabel("Timezone:");
    tzLabel.setPreferredSize(new Dimension(120, 25));
    timezoneCombo = new JComboBox<>(allTimezones.toArray(new String[0]));
    timezoneCombo.setFont(new Font("Arial", Font.PLAIN, 14));
    timezoneCombo.setEditable(true);
    timezoneCombo.setSelectedItem(ZoneId.systemDefault().getId());

    JTextField editorField = (JTextField) timezoneCombo.getEditor().getEditorComponent();
    editorField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER
            || e.getKeyCode() == KeyEvent.VK_UP
            || e.getKeyCode() == KeyEvent.VK_DOWN) {
          return;
        }
        SwingUtilities.invokeLater(() -> filterTimezones(editorField.getText()));
      }
    });

    tzPanel.add(tzLabel, BorderLayout.WEST);
    tzPanel.add(timezoneCombo, BorderLayout.CENTER);
    panel.add(tzPanel);

    panel.add(Box.createVerticalStrut(5));

    JLabel hintLabel = new JLabel("Type to search timezones...");
    hintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
    hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(hintLabel);

    return panel;
  }

  private void filterTimezones(String searchText) {
    String currentText = searchText.toLowerCase();
    List<String> filtered = new ArrayList<>();

    for (String tz : allTimezones) {
      if (tz.toLowerCase().contains(currentText)) {
        filtered.add(tz);
      }
    }

    DefaultComboBoxModel<String> model =
        new DefaultComboBoxModel<>(filtered.toArray(new String[0]));
    timezoneCombo.setModel(model);
    timezoneCombo.getEditor().setItem(searchText);
    timezoneCombo.showPopup();
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

    JButton createBtn = new JButton("Create");
    createBtn.setPreferredSize(new Dimension(100, 35));
    createBtn.addActionListener(e -> handleCreate());
    panel.add(createBtn);

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 35));
    cancelBtn.addActionListener(e -> dispose());
    panel.add(cancelBtn);

    return panel;
  }

  private void handleCreate() {
    String name = nameField.getText().trim();
    if (name.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Calendar name cannot be empty",
          "Validation Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    String zoneIdStr = (String) timezoneCombo.getEditor().getItem();
    if (zoneIdStr == null || zoneIdStr.trim().isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Please select a timezone",
          "Validation Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      ZoneId zoneId = ZoneId.of(zoneIdStr.trim());
      controller.createCalendar(name, zoneId);
      dispose();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Invalid timezone: " + zoneIdStr,
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}