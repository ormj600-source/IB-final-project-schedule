import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Locale;

public class AddSchedule1 extends JPanel {

    private enum Step {
        CLASSES,
        LEVELS,
        CURRENT_GRADES,
        TARGET_GRADES,
        UPCOMING_TESTS,
        DIFFICULTY,
        STUDY_TIME,
        SUMMARY
    }

    private final Allmenu allmenu;
    private Step currentStep = Step.CLASSES;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JLabel stepTitle = new JLabel("", SwingConstants.LEFT);
    private final JLabel stepSubtitle = new JLabel("", SwingConstants.LEFT);

    private final JTextField[] classFields = new JTextField[6];
    private final JComboBox<String>[] levelCombos = createComboArray(6);
    private final JComboBox<String>[] currentGradeCombos = createComboArray(6);
    private final JComboBox<String>[] targetGradeCombos = createComboArray(6);
    private final JComboBox<String>[] testCombos = createComboArray(6);
    private final JComboBox<String>[] difficultyCombos = createComboArray(6);
    private final JTextField[] studyTimeFields = new JTextField[7];

    private final JButton backButton = new JButton("Back");
    private final JButton nextButton = new JButton("Next");
    private final JButton cancelButton = new JButton("Cancel");

    private final String[][] classValues = new String[6][6];
    private final double[] minutesPerDay = new double[7];

    private final DefaultTableModel summaryModel = new DefaultTableModel(ScheduleCalculator.COLUMN_NAMES, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JLabel summaryHint = new JLabel("", SwingConstants.LEFT);

    public AddSchedule1(Allmenu allmenu) {
        this.allmenu = allmenu;
        setLayout(new BorderLayout(24, 24));
        setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));
        setBackground(new Color(248, 249, 252));

        stepTitle.setFont(stepTitle.getFont().deriveFont(Font.BOLD, 28f));
        stepTitle.setForeground(new Color(40, 44, 63));

        stepSubtitle.setFont(stepSubtitle.getFont().deriveFont(Font.PLAIN, 16f));
        stepSubtitle.setForeground(new Color(103, 108, 122));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(stepTitle);
        header.add(Box.createVerticalStrut(6));
        header.add(stepSubtitle);

        cardPanel.setOpaque(false);

        add(header, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        cardPanel.add(buildClassPanel(), Step.CLASSES.name());
        cardPanel.add(buildLevelPanel(), Step.LEVELS.name());
        cardPanel.add(buildCurrentGradePanel(), Step.CURRENT_GRADES.name());
        cardPanel.add(buildTargetGradePanel(), Step.TARGET_GRADES.name());
        cardPanel.add(buildTestPanel(), Step.UPCOMING_TESTS.name());
        cardPanel.add(buildDifficultyPanel(), Step.DIFFICULTY.name());
        cardPanel.add(buildStudyTimePanel(), Step.STUDY_TIME.name());
        cardPanel.add(buildSummaryPanel(), Step.SUMMARY.name());

        configureButtonActions();
        reset();
    }

    void reset() {
        currentStep = Step.CLASSES;
        for (int i = 0; i < classFields.length; i++) {
            classFields[i].setText("");
        }
        for (String[] row : classValues) {
            Arrays.fill(row, "");
        }
        Arrays.fill(minutesPerDay, 0.0);

        populateComboBoxes(levelCombos, new String[]{"HL", "SL"}, 0);
        String[] gradeOptions = {"1", "2", "3", "4", "5", "6", "7"};
        populateComboBoxes(currentGradeCombos, gradeOptions, 3);
        populateComboBoxes(targetGradeCombos, gradeOptions, 5);
        populateComboBoxes(testCombos, new String[]{"No", "Yes"}, 0);
        populateComboBoxes(difficultyCombos, new String[]{"Very Easy", "Easy", "Average", "Hard", "Very Hard"}, 2);

        for (JTextField field : studyTimeFields) {
            field.setText("");
        }

        summaryModel.setRowCount(0);
        summaryHint.setText("");
        updateStepUi();
    }

    void loadSchedule(ScheduleData schedule) {
        if (schedule == null) {
            reset();
            return;
        }

        populateComboBoxes(levelCombos, new String[]{"HL", "SL"}, 0);
        String[] gradeOptions = {"1", "2", "3", "4", "5", "6", "7"};
        populateComboBoxes(currentGradeCombos, gradeOptions, 3);
        populateComboBoxes(targetGradeCombos, gradeOptions, 5);
        populateComboBoxes(testCombos, new String[]{"No", "Yes"}, 0);
        populateComboBoxes(difficultyCombos, new String[]{"Very Easy", "Easy", "Average", "Hard", "Very Hard"}, 2);

        for (String[] row : classValues) {
            Arrays.fill(row, "");
        }
        Arrays.fill(minutesPerDay, 0.0);

        String[][] storedValues = schedule.getClassValues();
        for (int i = 0; i < classFields.length; i++) {
            String[] storedRow = storedValues.length > i && storedValues[i] != null ? storedValues[i] : new String[6];
            String className = storedRow.length > 0 && storedRow[0] != null ? storedRow[0] : "";
            classFields[i].setText(className);
            for (int j = 0; j < Math.min(storedRow.length, classValues[i].length); j++) {
                classValues[i][j] = storedRow[j] == null ? "" : storedRow[j];
            }
            setComboSelection(levelCombos[i], classValues[i][1]);
            setComboSelection(currentGradeCombos[i], classValues[i][2]);
            setComboSelection(targetGradeCombos[i], classValues[i][3]);
            setComboSelection(testCombos[i], classValues[i][4]);
            setComboSelection(difficultyCombos[i], classValues[i][5]);
        }

        double[] storedMinutes = schedule.getMinutesPerDay();
        for (int day = 0; day < studyTimeFields.length; day++) {
            double minutes = storedMinutes.length > day ? storedMinutes[day] : 0.0;
            minutesPerDay[day] = minutes;
            double hours = minutes / 60.0;
            studyTimeFields[day].setText(formatHoursForInput(hours));
        }

        summaryModel.setRowCount(0);
        summaryHint.setText("");
        currentStep = Step.CLASSES;
        updateStepUi();
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.PLAIN, 15f));
        cancelButton.setForeground(new Color(150, 75, 75));
        cancelButton.setBackground(new Color(255, 240, 240));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        left.add(cancelButton);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        styleSecondary(backButton);
        stylePrimary(nextButton);

        right.add(backButton);
        right.add(nextButton);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private void configureButtonActions() {
        backButton.addActionListener(e -> handleBack());
        nextButton.addActionListener(e -> handleNext());
        cancelButton.addActionListener(e -> handleCancel());
    }

    private void handleBack() {
        if (currentStep == Step.CLASSES) {
            return;
        }
        currentStep = Step.values()[currentStep.ordinal() - 1];
        updateStepUi();
    }

    private void handleNext() {
        switch (currentStep) {
            case CLASSES:
                if (!captureClassNames()) {
                    return;
                }
                break;
            case LEVELS:
                captureComboValues(levelCombos, 1);
                break;
            case CURRENT_GRADES:
                captureComboValues(currentGradeCombos, 2);
                break;
            case TARGET_GRADES:
                captureComboValues(targetGradeCombos, 3);
                break;
            case UPCOMING_TESTS:
                captureComboValues(testCombos, 4);
                break;
            case DIFFICULTY:
                captureComboValues(difficultyCombos, 5);
                break;
            case STUDY_TIME:
                if (!captureStudyTimes()) {
                    return;
                }
                populateSummary();
                break;
            case SUMMARY:
                populateSummary();
                allmenu.saveSchedule(buildScheduleData());
                allmenu.showScheduleView();
                return;
            default:
                break;
        }

        if (currentStep != Step.SUMMARY) {
            currentStep = Step.values()[currentStep.ordinal() + 1];
            updateStepUi();
        } else {
            populateSummary();
            updateStepUi();
        }
    }

    private void handleCancel() {
        int response = JOptionPane.showConfirmDialog(
                allmenu.getFrame(),
                "Cancel setup and return to the menu?",
                "Cancel schedule",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            allmenu.showMenu();
        }
    }

    private boolean captureClassNames() {
        for (int i = 0; i < classFields.length; i++) {
            String value = classFields[i].getText().trim();
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(
                        allmenu.getFrame(),
                        "Please enter all six class names before continuing.",
                        "Missing information",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            classValues[i][0] = value;
        }
        return true;
    }

    private void captureComboValues(JComboBox<String>[] comboBoxes, int column) {
        for (int i = 0; i < comboBoxes.length; i++) {
            Object selected = comboBoxes[i].getSelectedItem();
            classValues[i][column] = selected == null ? "" : selected.toString();
        }
    }

    private boolean captureStudyTimes() {
        String[] days = getDayNames();
        boolean allowSleepDeprivation = false;
        boolean allowWorkLifeImbalance = false;

        for (int i = 0; i < studyTimeFields.length; i++) {
            String input = studyTimeFields[i].getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(
                        allmenu.getFrame(),
                        "Please enter study hours for " + days[i] + ".",
                        "Missing information",
                        JOptionPane.ERROR_MESSAGE);
                studyTimeFields[i].requestFocusInWindow();
                return false;
            }
            double hours;
            try {
                hours = Double.parseDouble(input);
                if (hours < 0) {
                    throw new NumberFormatException("Negative hours");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        allmenu.getFrame(),
                        "Use numbers only for hours on " + days[i] + ".",
                        "Invalid number",
                        JOptionPane.ERROR_MESSAGE);
                studyTimeFields[i].requestFocusInWindow();
                return false;
            }

            if (hours > 24.0) {
                JOptionPane.showMessageDialog(
                        allmenu.getFrame(),
                        String.format(Locale.US, "%s is set to %.2f hours of study. That is not physically possible. Please adjust it.", days[i], hours),
                        "Invalid hours",
                        JOptionPane.ERROR_MESSAGE);
                studyTimeFields[i].requestFocusInWindow();
                return false;
            }

            if (hours >= 16.0) {
                if (!allowSleepDeprivation) {
                    int choice = JOptionPane.showConfirmDialog(
                            allmenu.getFrame(),
                            String.format(Locale.US, "%s is set to %.2f hours of study. That leaves almost no time for sleep. Continue anyway?", days[i], hours),
                            "Confirm intense schedule",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        studyTimeFields[i].requestFocusInWindow();
                        return false;
                    }
                    allowSleepDeprivation = true;
                }
            } else if (hours >= 8.0) {
                if (!allowWorkLifeImbalance) {
                    int choice = JOptionPane.showConfirmDialog(
                            allmenu.getFrame(),
                            String.format(Locale.US, "%s is set to %.2f hours of study. That's not a proper work-life balance. Continue?", days[i], hours),
                            "Check your balance",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        studyTimeFields[i].requestFocusInWindow();
                        return false;
                    }
                    allowWorkLifeImbalance = true;
                }
            }

            minutesPerDay[i] = hours * 60.0;
        }
        return true;
    }

    private void populateSummary() {
        ScheduleCalculator.ScheduleResult result = ScheduleCalculator.calculate(classValues, minutesPerDay);
        summaryModel.setRowCount(0);
        Object[][] rows = result.getTableData();
        for (Object[] row : rows) {
            summaryModel.addRow(row);
        }
        summaryHint.setText(result.getSummaryText());
    }

    private ScheduleData buildScheduleData() {
        return new ScheduleData(classValues, minutesPerDay);
    }

    private JPanel buildClassPanel() {
        JPanel panel = createStepPanel(
                "What classes are you taking?",
                "Enter each subject so we can ensure your schedule is accurate.");

        JPanel grid = new JPanel(new GridLayout(6, 1, 10, 10));
        grid.setOpaque(false);
        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            JLabel label = new JLabel("Class " + (i + 1));
            label.setPreferredSize(new Dimension(120, 28));
            JTextField field = new JTextField();
            styleTextField(field);
            classFields[i] = field;
            row.add(label, BorderLayout.WEST);
            row.add(field, BorderLayout.CENTER);
            grid.add(row);
        }
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLevelPanel() {
        JPanel panel = createStepPanel(
                "Higher level or standard level?",
                "Make sure to select correctly as it affects weighting.");

        String[] options = {"HL", "SL"};
        for (int i = 0; i < levelCombos.length; i++) {
            levelCombos[i] = createCombo(options);
        }
        panel.add(createComboGrid(levelCombos, "Class"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCurrentGradePanel() {
        JPanel panel = createStepPanel(
                "How are you doing right now?",
                "Pick your current grade for each subject.");

        String[] gradeOptions = {"1", "2", "3", "4", "5", "6", "7"};
        for (int i = 0; i < currentGradeCombos.length; i++) {
            currentGradeCombos[i] = createCombo(gradeOptions);
        }
        panel.add(createComboGrid(currentGradeCombos, "Class"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTargetGradePanel() {
        JPanel panel = createStepPanel(
                "What grade are you aiming for?",
                "This helps us prioritize the right subjects.");

        String[] gradeOptions = {"1", "2", "3", "4", "5", "6", "7"};
        for (int i = 0; i < targetGradeCombos.length; i++) {
            targetGradeCombos[i] = createCombo(gradeOptions);
        }
        panel.add(createComboGrid(targetGradeCombos, "Class"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTestPanel() {
        JPanel panel = createStepPanel(
                "Upcoming assessments",
                "Let us know if you have any upcoming assessments within the next 2 weeks.");

        String[] options = {"No", "Yes"};
        for (int i = 0; i < testCombos.length; i++) {
            testCombos[i] = createCombo(options);
        }
        panel.add(createComboGrid(testCombos, "Class"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDifficultyPanel() {
        JPanel panel = createStepPanel(
                "How tough does each class feel?",
                "Classes with harder difficulty will be prioritized.");

        String[] options = {"Very Easy", "Easy", "Average", "Hard", "Very Hard"};
        for (int i = 0; i < difficultyCombos.length; i++) {
            difficultyCombos[i] = createCombo(options);
        }
        panel.add(createComboGrid(difficultyCombos, "Class"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStudyTimePanel() {
        JPanel panel = createStepPanel(
                "Study hours available",
                "Roughly how many hours can you dedicate each day? Use decimals for partial hours.");

        JPanel grid = new JPanel(new GridLayout(7, 1, 10, 10));
        grid.setOpaque(false);
        String[] days = getDayNames();
        for (int i = 0; i < days.length; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            JLabel label = new JLabel(days[i]);
            label.setPreferredSize(new Dimension(120, 28));
            JTextField field = new JTextField();
            styleTextField(field);
            studyTimeFields[i] = field;
            row.add(label, BorderLayout.WEST);
            row.add(field, BorderLayout.CENTER);
            row.add(new JLabel("hours"), BorderLayout.EAST);
            grid.add(row);
        }
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = createStepPanel(
                "Your weekly study blueprint",
                "We split your study hours based on the priorities you set.");

        JTable summaryTable = new JTable(summaryModel);
        ScheduleTableUtil.configure(summaryTable);

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 233)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        panel.add(scrollPane, BorderLayout.CENTER);
        summaryHint.setForeground(new Color(90, 96, 110));
        summaryHint.setFont(summaryHint.getFont().deriveFont(Font.PLAIN, 15f));
        panel.add(summaryHint, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStepPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 236)),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));

        JLabel heading = new JLabel(title);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 20f));
        heading.setForeground(new Color(45, 49, 67));

        JLabel subheading = new JLabel(description);
        subheading.setFont(subheading.getFont().deriveFont(Font.PLAIN, 15f));
        subheading.setForeground(new Color(110, 115, 128));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(heading);
        top.add(Box.createVerticalStrut(6));
        top.add(subheading);

        panel.add(top, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createComboGrid(JComboBox<String>[] combos, String labelPrefix) {
        JPanel grid = new JPanel(new GridLayout(combos.length, 1, 10, 10));
        grid.setOpaque(false);
        for (int i = 0; i < combos.length; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            JLabel label = new JLabel(labelPrefix + " " + (i + 1));
            label.setPreferredSize(new Dimension(120, 28));
            row.add(label, BorderLayout.WEST);
            row.add(combos[i], BorderLayout.CENTER);
            grid.add(row);
        }
        return grid;
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 15f));
    }

    private void stylePrimary(JButton button) {
        button.setFont(button.getFont().deriveFont(Font.BOLD, 16f));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(76, 110, 245));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondary(JButton button) {
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 16f));
        button.setForeground(new Color(60, 63, 78));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(208, 213, 221)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void updateStepUi() {
        stepTitle.setText(getStepTitle(currentStep));
        stepSubtitle.setText(getStepSubtitle(currentStep));
        cardLayout.show(cardPanel, currentStep.name());

        backButton.setEnabled(currentStep != Step.CLASSES);
        nextButton.setText(currentStep == Step.SUMMARY ? "Finish" : "Next");
    }

    private String getStepTitle(Step step) {
        switch (step) {
            case CLASSES:
                return "Start your schedule";
            case LEVELS:
                return "Class difficulty tier";
            case CURRENT_GRADES:
                return "Current performance";
            case TARGET_GRADES:
                return "Target performance";
            case UPCOMING_TESTS:
                return "Assessment timeline";
            case DIFFICULTY:
                return "Perceived difficulty";
            case STUDY_TIME:
                return "Available time";
            case SUMMARY:
                return "Review plan";
            default:
                return "";
        }
    }

    private String getStepSubtitle(Step step) {
        switch (step) {
            case CLASSES:
                return "We will ask a few quick questions to tailor your study plan.";
            case LEVELS:
                return "Identify which subjects are higher level to weight them appropriately.";
            case CURRENT_GRADES:
                return "Selecting your current grade helps us spot where improvement is needed.";
            case TARGET_GRADES:
                return "Set your goals so the plan targets the right gains.";
            case UPCOMING_TESTS:
                return "Prioritise subjects with nearby exams or internal assessments.";
            case DIFFICULTY:
                return "Tell us how each class feels so we can pace your week.";
            case STUDY_TIME:
                return "Give us your available hours for each day of the week.";
            case SUMMARY:
                return "Here is your suggested weekly breakdown. You can tweak it anytime.";
            default:
                return "";
        }
    }

    @SuppressWarnings("unchecked")
    private static JComboBox<String>[] createComboArray(int size) {
        return new JComboBox[size];
    }

    private void populateComboBoxes(JComboBox<String>[] comboBoxes, String[] options, int defaultIndex) {
        for (int i = 0; i < comboBoxes.length; i++) {
            if (comboBoxes[i] == null) {
                comboBoxes[i] = createCombo(options);
            } else {
                comboBoxes[i].removeAllItems();
                for (String option : options) {
                    comboBoxes[i].addItem(option);
                }
            }
            int index = Math.max(0, Math.min(defaultIndex, options.length - 1));
            comboBoxes[i].setSelectedIndex(index);
        }
    }

    private JComboBox<String> createCombo(String[] options) {
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setFont(combo.getFont().deriveFont(Font.PLAIN, 15f));
        combo.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)));
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        return combo;
    }

    private void setComboSelection(JComboBox<String> comboBox, String value) {
        if (comboBox == null || value == null || value.isEmpty()) {
            return;
        }
        comboBox.setSelectedItem(value);
    }

    private String formatHoursForInput(double hours) {
        String formatted = String.format(Locale.US, "%.2f", hours);
        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return formatted;
    }

    private String[] getDayNames() {
        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }
}