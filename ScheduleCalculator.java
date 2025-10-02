import java.util.Arrays;
import java.util.Locale;

public final class ScheduleCalculator {

    public static final String[] COLUMN_NAMES = {
            "Class", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private static final int CLASS_COUNT = 6;
    private static final int DAY_COUNT = 7;

    private static final double BASE_PRIORITY = 1.0;
    private static final double HL_WEIGHT = 0.7;
    private static final double SL_WEIGHT = 0.2;
    private static final double TEST_WEIGHT = 0.8;
    private static final double UNKNOWN_GRADE_WEIGHT = 0.25;
    private static final double MAINTAIN_GRADE_WEIGHT = 0.15;
    private static final double GRADE_GAP_MULTIPLIER = 0.35;

    private static final double DIFFICULTY_VERY_HARD = 1.0;
    private static final double DIFFICULTY_HARD = 0.7;
    private static final double DIFFICULTY_AVERAGE = 0.45;
    private static final double DIFFICULTY_EASY = 0.25;
    private static final double DIFFICULTY_DEFAULT = 0.1;

    private ScheduleCalculator() {
    }

    public static ScheduleResult calculate(String[][] classValues, double[] minutesPerDay) {
        String[][] values = classValues != null ? classValues : new String[0][];
        double[] minutes = minutesPerDay != null ? minutesPerDay : new double[0];

        double[] priorities = new double[CLASS_COUNT];
        double totalPriority = 0.0;

        for (int i = 0; i < CLASS_COUNT; i++) {
            String[] row = i < values.length && values[i] != null ? values[i] : new String[0];
            double priority = computePriority(row);
            priorities[i] = priority;
            totalPriority += priority;
        }

        if (totalPriority == 0.0) {
            Arrays.fill(priorities, BASE_PRIORITY);
            totalPriority = BASE_PRIORITY * priorities.length;
        }

        double[] ratios = new double[CLASS_COUNT];
        for (int i = 0; i < CLASS_COUNT; i++) {
            ratios[i] = priorities[i] / totalPriority;
        }

        Object[][] tableData = new Object[CLASS_COUNT][COLUMN_NAMES.length];
        for (int i = 0; i < CLASS_COUNT; i++) {
            String[] row = i < values.length && values[i] != null ? values[i] : new String[0];
            String className = getValue(row, 0);
            if (className == null || className.trim().isEmpty()) {
                className = "Class " + (i + 1);
            }
            tableData[i][0] = className;
            for (int day = 0; day < DAY_COUNT; day++) {
                double minutesForDay = (day < minutes.length ? minutes[day] : 0.0) * ratios[i];
                tableData[i][day + 1] = formatMinutes(minutesForDay);
            }
        }

        double totalMinutes = 0.0;
        for (int day = 0; day < DAY_COUNT; day++) {
            totalMinutes += day < minutes.length ? minutes[day] : 0.0;
        }
        double totalHours = totalMinutes / 60.0;
        String summaryText = String.format(Locale.US, "Allocate %.1f total study hours each week.", totalHours);

        return new ScheduleResult(tableData, summaryText);
    }

    private static double computePriority(String[] row) {
        double priority = BASE_PRIORITY;

        String level = getValue(row, 1);
        if ("HL".equalsIgnoreCase(level)) {
            priority += HL_WEIGHT;
        } else if (!level.isEmpty()) {
            priority += SL_WEIGHT;
        }

        priority += gradeWeight(getValue(row, 2), getValue(row, 3));

        if ("Yes".equalsIgnoreCase(getValue(row, 4))) {
            priority += TEST_WEIGHT;
        }

        priority += difficultyWeight(getValue(row, 5));

        return Math.max(priority, BASE_PRIORITY * 0.25);
    }

    private static double gradeWeight(String currentGrade, String targetGrade) {
        try {
            int current = Integer.parseInt(currentGrade);
            int target = Integer.parseInt(targetGrade);
            int gap = target - current;
            if (gap > 0) {
                return Math.min(gap, 3) * GRADE_GAP_MULTIPLIER;
            }
            return MAINTAIN_GRADE_WEIGHT;
        } catch (NumberFormatException ignored) {
            if ((currentGrade != null && !currentGrade.isEmpty()) || (targetGrade != null && !targetGrade.isEmpty())) {
                return MAINTAIN_GRADE_WEIGHT;
            }
            return UNKNOWN_GRADE_WEIGHT;
        }
    }

    private static double difficultyWeight(String difficulty) {
        if (difficulty == null) {
            return DIFFICULTY_DEFAULT;
        }
        if ("Very Hard".equalsIgnoreCase(difficulty)) {
            return DIFFICULTY_VERY_HARD;
        }
        if ("Hard".equalsIgnoreCase(difficulty)) {
            return DIFFICULTY_HARD;
        }
        if ("Average".equalsIgnoreCase(difficulty)) {
            return DIFFICULTY_AVERAGE;
        }
        if ("Easy".equalsIgnoreCase(difficulty)) {
            return DIFFICULTY_EASY;
        }
        if ("Very Easy".equalsIgnoreCase(difficulty)) {
            return DIFFICULTY_DEFAULT;
        }
        return DIFFICULTY_DEFAULT;
    }

    private static String getValue(String[] row, int index) {
        if (row == null || index < 0 || index >= row.length || row[index] == null) {
            return "";
        }
        return row[index];
    }

    private static String formatMinutes(double minutes) {
        int totalMinutes = (int) Math.round(Math.max(0.0, minutes));
        int hours = totalMinutes / 60;
        int mins = totalMinutes % 60;

        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours)
                    .append(" ")
                    .append(hours == 1 ? "hour" : "hours");
        }
        if (mins > 0) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(mins)
                    .append(" ")
                    .append(mins == 1 ? "minute" : "minutes");
        }
        if (builder.length() == 0) {
            builder.append("0 minutes");
        }
        return builder.toString();
    }

    public static final class ScheduleResult {
        private final Object[][] tableData;
        private final String summaryText;

        private ScheduleResult(Object[][] tableData, String summaryText) {
            this.tableData = tableData;
            this.summaryText = summaryText;
        }

        public Object[][] getTableData() {
            Object[][] copy = new Object[tableData.length][];
            for (int i = 0; i < tableData.length; i++) {
                copy[i] = Arrays.copyOf(tableData[i], tableData[i].length);
            }
            return copy;
        }

        public String getSummaryText() {
            return summaryText;
        }
    }
}