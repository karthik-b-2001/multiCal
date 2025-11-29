package calendar.view;

import calendar.controller.GuiController;
import calendar.model.Event;
import calendar.view.dialogs.CreateCalendarDialog;
import calendar.view.dialogs.CreateEventDialog;
import calendar.view.dialogs.ViewEventsDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Implementation of the GUI view for the calendar application.
 */
public class GuiViewImpl extends JFrame implements GuiView {

  private YearMonth currentMonth;
  private final GuiController controller;
  private JComboBox<String> calendarSelector;
  private JLabel timezoneLabel;
  private JLabel monthLabel;
  private JPanel calendarGrid;
  private final JButton[][] dayButtons;
  private JLabel activeCalendarLabel;
  private JPanel calendarBannerPanel;

  /**
   * Constructs the GuiViewImpl with the specified controller.
   *
   * @param controller the GUI controller
   */
  public GuiViewImpl(GuiController controller) {
    this.controller = controller;
    this.currentMonth = YearMonth.now();
    this.dayButtons = new JButton[6][7];
    setupWindow();
    refreshCalendar();
  }

  private void setupWindow() {
    setTitle("Calendar Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(900, 700);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(10, 10));
    add(createTopPanel(), BorderLayout.NORTH);
    add(createCalendarPanel(), BorderLayout.CENTER);
    add(createBottomPanel(), BorderLayout.SOUTH);
  }

  private JPanel createTopPanel() {
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    calendarBannerPanel = new JPanel(new BorderLayout());
    calendarBannerPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
        BorderFactory.createEmptyBorder(6, 15, 6, 15)
    ));

    activeCalendarLabel = new JLabel();
    activeCalendarLabel.setFont(new Font("Arial", Font.BOLD, 16));
    activeCalendarLabel.setHorizontalAlignment(SwingConstants.CENTER);
    calendarBannerPanel.add(activeCalendarLabel, BorderLayout.CENTER);

    timezoneLabel = new JLabel();
    timezoneLabel.setFont(new Font("Arial", Font.ITALIC, 11));
    timezoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    calendarBannerPanel.add(timezoneLabel, BorderLayout.EAST);

    mainPanel.add(calendarBannerPanel);
    mainPanel.add(Box.createVerticalStrut(8));

    JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
    controlsPanel.setBackground(new Color(245, 245, 245));
    controlsPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));

    controlsPanel.add(new JLabel("Switch Calendar:"));
    calendarSelector = new JComboBox<>();
    calendarSelector.setPreferredSize(new Dimension(180, 28));
    calendarSelector.setFont(new Font("Arial", Font.PLAIN, 13));

    calendarSelector.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null) {
          String calName = value.toString();
          Color calColor = controller.getCalendarColor(calName);

          setText(">> " + calName);

          if (isSelected) {
            setBackground(calColor);
            setForeground(getContrastColor(calColor));
          } else {
            setBackground(Color.WHITE);
            setForeground(calColor.darker());
          }
        }
        return this;
      }
    });

    calendarSelector.addActionListener(e -> {
      String selected = (String) calendarSelector.getSelectedItem();
      if (selected != null) {
        controller.switchCalendar(selected);
      }
    });
    controlsPanel.add(calendarSelector);

    controlsPanel.add(Box.createHorizontalStrut(15));

    JButton newCalBtn = new JButton("+ New Calendar");
    newCalBtn.setFont(new Font("Arial", Font.BOLD, 12));
    newCalBtn.setPreferredSize(new Dimension(130, 28));
    newCalBtn.addActionListener(e -> showCreateCalendarDialog());
    controlsPanel.add(newCalBtn);

    mainPanel.add(controlsPanel);

    return mainPanel;
  }

  private Color getContrastColor(Color background) {
    double brightness = (background.getRed() * 299.0
        + background.getGreen() * 587.0
        + background.getBlue() * 114.0) / 1000.0;
    return brightness > 128 ? Color.BLACK : Color.WHITE;
  }

  private void showCreateCalendarDialog() {
    CreateCalendarDialog dialog = new CreateCalendarDialog(this, controller);
    dialog.setVisible(true);
  }

  private JPanel createCalendarPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    JButton prevBtn = new JButton("< Previous");
    prevBtn.setFont(new Font("Arial", Font.BOLD, 12));
    prevBtn.setPreferredSize(new Dimension(100, 35));
    prevBtn.addActionListener(e -> {
      currentMonth = currentMonth.minusMonths(1);
      refreshCalendar();
    });

    monthLabel = new JLabel("", SwingConstants.CENTER);
    monthLabel.setFont(new Font("Arial", Font.BOLD, 24));

    JButton nextBtn = new JButton("Next >");
    nextBtn.setFont(new Font("Arial", Font.BOLD, 12));
    nextBtn.setPreferredSize(new Dimension(100, 35));
    nextBtn.addActionListener(e -> {
      currentMonth = currentMonth.plusMonths(1);
      refreshCalendar();
    });

    JPanel navPanel = new JPanel(new BorderLayout());
    navPanel.add(prevBtn, BorderLayout.WEST);
    navPanel.add(monthLabel, BorderLayout.CENTER);
    navPanel.add(nextBtn, BorderLayout.EAST);

    panel.add(navPanel, BorderLayout.NORTH);

    calendarGrid = new JPanel(new GridLayout(7, 7, 5, 5));
    calendarGrid.setBackground(Color.WHITE);
    calendarGrid.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

    String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String day : dayNames) {
      JLabel label = new JLabel(day, SwingConstants.CENTER);
      label.setFont(new Font("Arial", Font.BOLD, 14));
      label.setOpaque(true);
      label.setBackground(new Color(70, 70, 70));
      label.setForeground(Color.WHITE);
      label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
      calendarGrid.add(label);
    }

    for (int row = 0; row < 6; row++) {
      for (int col = 0; col < 7; col++) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(100, 80));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        dayButtons[row][col] = button;
        calendarGrid.add(button);
      }
    }

    panel.add(calendarGrid, BorderLayout.CENTER);
    return panel;
  }

  private JPanel createBottomPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.setBackground(new Color(245, 245, 245));

    JButton createEventBtn = new JButton("+ Create Event");
    createEventBtn.setPreferredSize(new Dimension(150, 40));
    createEventBtn.setFont(new Font("Arial", Font.BOLD, 14));
    createEventBtn.addActionListener(e -> showCreateEventDialog(currentMonth.atDay(1)));

    panel.add(createEventBtn);

    return panel;
  }

  private void showCreateEventDialog(LocalDate localDate) {
    CreateEventDialog dialog = new CreateEventDialog(this, controller, localDate);
    dialog.setVisible(true);
  }

  @Override
  public void refreshCalendar() {
    String activeCalName = controller.getActiveCalendarName();
    Color calColor = activeCalName != null
        ? controller.getCalendarColor(activeCalName)
        : new Color(173, 216, 230);

    calendarBannerPanel.setBackground(calColor);
    activeCalendarLabel.setText("Current Calendar: " + activeCalName);
    activeCalendarLabel.setForeground(getContrastColor(calColor));

    timezoneLabel.setText("Timezone: " + controller.getCurrentTimezone() + "  ");
    timezoneLabel.setForeground(getContrastColor(calColor));

    String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    monthLabel.setText(monthName + " " + currentMonth.getYear());

    LocalDate firstOfMonth = currentMonth.atDay(1);
    int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
    int daysInMonth = currentMonth.lengthOfMonth();

    int day = 1;
    for (int row = 0; row < 6; row++) {
      for (int col = 0; col < 7; col++) {
        JButton btn = dayButtons[row][col];

        for (var listener : btn.getActionListeners()) {
          btn.removeActionListener(listener);
        }

        int position = row * 7 + col;
        if (position < firstDayOfWeek || day > daysInMonth) {
          btn.setText("");
          btn.setEnabled(false);
          btn.setBackground(new Color(240, 240, 240));
          btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        } else {
          final int currentDay = day;
          LocalDate date = currentMonth.atDay(currentDay);
          List<Event> events = controller.getEventsForDate(date);

          String text = String.valueOf(currentDay);
          if (!events.isEmpty()) {
            text = "<html><center>" + currentDay
                + "<br><small>(" + events.size() + ")</small></center></html>";
          }
          btn.setText(text);
          btn.setEnabled(true);

          if (!events.isEmpty()) {
            btn.setBackground(calColor);
            btn.setForeground(getContrastColor(calColor));
          } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
          }

          if (date.equals(LocalDate.now())) {
            btn.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 200), 3));
          } else {
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
          }

          btn.addActionListener(e -> handleDayClick(date));
          day++;
        }
      }
    }

    calendarGrid.revalidate();
    calendarGrid.repaint();
  }

  private void handleDayClick(LocalDate date) {
    List<Event> events = controller.getEventsForDate(date);
    if (events.isEmpty()) {
      int choice = JOptionPane.showConfirmDialog(
          this,
          "No events on " + date + ".\nWould you like to create a new event?",
          "No Events",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE
      );

      if (choice == JOptionPane.YES_OPTION) {
        showCreateEventDialog(date);
      }
    } else {
      showViewEventsDialog(date, events);
    }
  }

  private void showViewEventsDialog(LocalDate date, List<Event> events) {
    ViewEventsDialog dialog = new ViewEventsDialog(this, date, events, controller);
    dialog.setVisible(true);
  }

  @Override
  public void showError(String error) {
    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void showMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void updateCalendarList(List<String> allCalendarNames) {
    String selected = (String) calendarSelector.getSelectedItem();
    calendarSelector.removeAllItems();
    for (String name : allCalendarNames) {
      calendarSelector.addItem(name);
    }
    if (selected != null && allCalendarNames.contains(selected)) {
      calendarSelector.setSelectedItem(selected);
    }
  }

  @Override
  public void display() {
    setVisible(true);
  }
}