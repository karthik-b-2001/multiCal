package calendar.view.dialogs;

import calendar.controller.GuiController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Abstract base class for dialogs in the calendar application.
 */
public abstract class AbstractDialog extends JDialog {

  protected GuiController controller;

  /**
   * Constructs the AbstractDialog.
   *
   * @param parent     the parent frame
   * @param title      the dialog title
   * @param controller the GUI controller
   */
  public AbstractDialog(JFrame parent, String title, GuiController controller) {
    super(parent, title, true);
    this.controller = controller;
  }

  /**
   * Initializes the dialog with standard layout and components.
   *
   * @param width  the dialog width
   * @param height the dialog height
   */
  protected void initializeDialog(int width, int height) {
    setSize(width, height);
    setLocationRelativeTo(getParent());
    setLayout(new BorderLayout(10, 10));

    add(createContentPanel(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
  }

  /**
   * Creates the main content panel for the dialog.
   *
   * @return the content panel
   */
  protected abstract JPanel createContentPanel();

  /**
   * Creates the button panel with action buttons.
   *
   * @return the button panel
   */
  protected JPanel createButtonPanel() {
    JButton okBtn = new JButton(getOkButtonText());
    okBtn.setPreferredSize(new Dimension(getOkButtonWidth(), 35));
    okBtn.addActionListener(e -> handleOk());

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 35));
    cancelBtn.addActionListener(e -> dispose());

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    panel.add(okBtn);
    panel.add(cancelBtn);

    return panel;
  }

  /**
   * Gets the text for the OK button.
   *
   * @return the button text
   */
  protected abstract String getOkButtonText();

  /**
   * Gets the width for the OK button.
   *
   * @return the button width
   */
  protected abstract int getOkButtonWidth();

  /**
   * Creates a standard label-field pair panel.
   *
   * @param labelText the label text
   * @param field     the input component
   * @return the panel containing label and field
   */
  protected JPanel createLabelFieldPair(String labelText, JComponent field) {
    JPanel pair = new JPanel(new BorderLayout(5, 5));
    pair.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    JLabel label = new JLabel(labelText);
    label.setPreferredSize(new Dimension(120, 25));
    pair.add(label, BorderLayout.WEST);
    pair.add(field, BorderLayout.CENTER);

    return pair;
  }

  /**
   * Shows a validation error message.
   *
   * @param message the error message to display
   */
  protected void showValidationError(String message) {
    JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Handles the OK button action.
   */
  protected abstract void handleOk();
}