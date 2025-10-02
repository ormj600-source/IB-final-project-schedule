import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Menu {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Allmenu allmenu = new Allmenu();
            allmenu.show();
        });
    }
}

class Allmenu {

    private final JFrame frame = new JFrame("IB Subject Scheduler");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private AddSchedule1 scheduleWizard;
    private ScheduleViewerPanel scheduleViewer;
    private ScheduleData savedSchedule;

    Allmenu() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 720);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(cardPanel);
        cardPanel.add(createMenuPanel(), "menu");
    }

    void show() {
        frame.setVisible(true);
    }

    void showMenu() {
        cardLayout.show(cardPanel, "menu");
        frame.revalidate();
        frame.repaint();
    }

    void showScheduleWizard() {
        showScheduleWizard(null);
    }

    void showScheduleWizard(ScheduleData scheduleToEdit) {
        if (scheduleWizard == null) {
            scheduleWizard = new AddSchedule1(this);
            cardPanel.add(scheduleWizard, "wizard");
        }
        if (scheduleToEdit == null) {
            scheduleWizard.reset();
        } else {
            scheduleWizard.loadSchedule(scheduleToEdit);
        }
        cardLayout.show(cardPanel, "wizard");
        frame.revalidate();
        frame.repaint();
    }

    void showScheduleView() {
        if (savedSchedule == null) {
            JOptionPane.showMessageDialog(frame,
                    "Create a schedule first so there is something to view.",
                    "No schedule yet",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (scheduleViewer == null) {
            scheduleViewer = new ScheduleViewerPanel();
            cardPanel.add(scheduleViewer, "view");
        }
        scheduleViewer.updateSchedule(savedSchedule);
        cardLayout.show(cardPanel, "view");
        frame.revalidate();
        frame.repaint();
    }

    void saveSchedule(ScheduleData schedule) {
        savedSchedule = schedule;
        if (scheduleViewer != null) {
            scheduleViewer.updateSchedule(schedule);
        }
    }

    JFrame getFrame() {
        return frame;
    }

    ScheduleData getSavedSchedule() {
        return savedSchedule;
    }

    private JPanel createMenuPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        root.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("IB Subject Scheduler", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));
        title.setForeground(new Color(40, 40, 60));

        JLabel subtitle = new JLabel("Plan, track and optimise your study time in one place.");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 18f));
        subtitle.setForeground(new Color(90, 96, 110));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 18));

        JButton createScheduleButton = primaryButton("Create new schedule");
        createScheduleButton.addActionListener(e -> showScheduleWizard());

        JButton viewScheduleButton = secondaryButton("View current schedule");
        viewScheduleButton.addActionListener(e -> showScheduleView());

        JButton updateInfoButton = secondaryButton("Update information");
        updateInfoButton.addActionListener(e -> {
            if (savedSchedule == null) {
                JOptionPane.showMessageDialog(frame,
                        "Create a schedule first so there is information to update.",
                        "No schedule yet",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                showScheduleWizard(savedSchedule);
            }
        });

        JButton exitButton = secondaryButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(createScheduleButton);
        buttonPanel.add(viewScheduleButton);
        buttonPanel.add(updateInfoButton);
        buttonPanel.add(exitButton);

        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.add(buttonPanel, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(header, BorderLayout.NORTH);
        content.add(Box.createVerticalStrut(30), BorderLayout.CENTER);
        content.add(card, BorderLayout.SOUTH);

        root.add(content, BorderLayout.CENTER);

        return root;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 18f));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(76, 110, 245));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 17f));
        button.setForeground(new Color(50, 54, 66));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(208, 213, 221)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private class ScheduleViewerPanel extends JPanel {

        private final DefaultTableModel tableModel = new DefaultTableModel(ScheduleCalculator.COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        private final JLabel summaryLabel = new JLabel("", SwingConstants.LEFT);

        ScheduleViewerPanel() {
            setLayout(new BorderLayout(24, 24));
            setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));
            setBackground(new Color(248, 249, 252));

            JLabel heading = new JLabel("Current schedule", SwingConstants.LEFT);
            heading.setFont(heading.getFont().deriveFont(Font.BOLD, 28f));
            heading.setForeground(new Color(40, 44, 63));

            JLabel subheading = new JLabel("Review or refine your study plan anytime.");
            subheading.setFont(subheading.getFont().deriveFont(Font.PLAIN, 16f));
            subheading.setForeground(new Color(103, 108, 122));

            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
            header.add(heading);
            header.add(Box.createVerticalStrut(6));
            header.add(subheading);

            JTable table = new JTable(tableModel);
            ScheduleTableUtil.configure(table);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 224, 233)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));

            JPanel footer = new JPanel(new BorderLayout());
            footer.setOpaque(false);

            summaryLabel.setForeground(new Color(90, 96, 110));
            summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.PLAIN, 15f));
            footer.add(summaryLabel, BorderLayout.WEST);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            buttons.setOpaque(false);

            JButton backButton = secondaryButton("Back to menu");
            backButton.addActionListener(e -> showMenu());

            JButton editButton = primaryButton("Update information");
            editButton.addActionListener(e -> showScheduleWizard(savedSchedule));

            buttons.add(backButton);
            buttons.add(editButton);
            footer.add(buttons, BorderLayout.EAST);

            add(header, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(footer, BorderLayout.SOUTH);
        }

        void updateSchedule(ScheduleData schedule) {
            if (schedule == null) {
                tableModel.setRowCount(0);
                summaryLabel.setText("No schedule saved yet.");
                return;
            }
            ScheduleCalculator.ScheduleResult result = ScheduleCalculator.calculate(schedule.getClassValues(), schedule.getMinutesPerDay());
            tableModel.setRowCount(0);
            Object[][] rows = result.getTableData();
            for (Object[] row : rows) {
                tableModel.addRow(row);
            }
            summaryLabel.setText(result.getSummaryText());
        }
    }
}