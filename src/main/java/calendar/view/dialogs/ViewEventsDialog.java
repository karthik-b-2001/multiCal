package calendar.view.dialogs;

import calendar.controller.GuiController;
import calendar.model.Event;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Dialog for viewing events on a specific date.
 */
public class ViewEventsDialog extends JDialog {

  private final LocalDate date;
  private final List<Event> events;
  private final GuiController controller;

  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Constructs the ViewEventsDialog.
   *
   * @param parent     the parent frame
   * @param date       the date to view events for
   * @param events     the list of events on that date
   * @param controller the GUI controller
   */
  public ViewEventsDialog(JFrame parent, LocalDate date, List<Event> events,
                          GuiController controller) {
    super(parent, "Events on " + date, true);
    this.date = date;
    this.events = events;
    this.controller = controller;
    initializeDialog();
  }

  private void initializeDialog() {
    setSize(600, 400);
    setLocationRelativeTo(getParent());
    setLayout(new BorderLayout(10, 10));

    add(createContentPanel(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel createContentPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel titleLabel = new JLabel("Events on " + date);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(titleLabel, BorderLayout.NORTH);

    JPanel eventsList = new JPanel();
    eventsList.setLayout(new BoxLayout(eventsList, BoxLayout.Y_AXIS));

    for (Event event : events) {
      eventsList.add(createEventCard(event));
      eventsList.add(Box.createVerticalStrut(10));
    }

    JScrollPane scrollPane = new JScrollPane(eventsList);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

    JButton createBtn = new JButton("+ Create Event");
    createBtn.setPreferredSize(new Dimension(140, 35));
    createBtn.addActionListener(e -> openCreateDialog());
    panel.add(createBtn);

    JButton closeBtn = new JButton("Close");
    closeBtn.setPreferredSize(new Dimension(100, 35));
    closeBtn.addActionListener(e -> dispose());
    panel.add(closeBtn);

    return panel;
  }

  private void openCreateDialog() {
    CreateEventDialog dialog =
        new CreateEventDialog((JFrame) SwingUtilities.getWindowAncestor(this), controller, date);
    dialog.setVisible(true);
    dispose();
  }

  private JPanel createEventCard(Event event) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    card.setBackground(Color.WHITE);

    JLabel subjectLabel = new JLabel(event.getSubject());
    subjectLabel.setFont(new Font("Arial", Font.BOLD, 16));
    card.add(subjectLabel);

    card.add(Box.createVerticalStrut(5));

    String timeText;
    if (event.isAllDayEvent()) {
      timeText = "All Day";
    } else {
      timeText = event.getStartDateTime().format(TIME_FORMATTER) + " - "
          + event.getEndDateTime().format(TIME_FORMATTER);
    }
    JLabel timeLabel = new JLabel("Time: " + timeText);
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    card.add(timeLabel);

    if (event.getLocation().isPresent()) {
      card.add(Box.createVerticalStrut(5));
      JLabel locationLabel = new JLabel("Location: " + event.getLocation().get());
      locationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
      card.add(locationLabel);
    }

    if (event.getDescription().isPresent()) {
      card.add(Box.createVerticalStrut(5));
      JLabel descLabel = new JLabel("Description: " + event.getDescription().get());
      descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
      card.add(descLabel);
    }

    if (event.isInSeries()) {
      card.add(Box.createVerticalStrut(5));
      JLabel seriesLabel = new JLabel("(Part of recurring series)");
      seriesLabel.setFont(new Font("Arial", Font.ITALIC, 12));
      seriesLabel.setForeground(Color.GRAY);
      card.add(seriesLabel);
    }

    card.add(Box.createVerticalStrut(10));
    JButton editBtn = new JButton("Edit This Event");
    editBtn.addActionListener(e -> openEditDialog(event));
    card.add(editBtn);

    return card;
  }

  private void openEditDialog(Event event) {
    EditEventDialog dialog =
        new EditEventDialog((JFrame) SwingUtilities.getWindowAncestor(this), event, controller);
    dialog.setVisible(true);
    dispose();
  }
}