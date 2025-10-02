import java.util.Arrays;

public class ScheduleData {

    private static final int CLASS_COUNT = 6;
    private static final int CLASS_ATTRIBUTES = 6;
    private static final int DAY_COUNT = 7;

    private final String[][] classValues;
    private final double[] minutesPerDay;

    public ScheduleData(String[][] classValues, double[] minutesPerDay) {
        this.classValues = normaliseClassValues(classValues);
        this.minutesPerDay = normaliseMinutes(minutesPerDay);
    }

    public String[][] getClassValues() {
        return normaliseClassValues(classValues);
    }

    public double[] getMinutesPerDay() {
        return Arrays.copyOf(minutesPerDay, minutesPerDay.length);
    }

    private String[][] normaliseClassValues(String[][] source) {
        String[][] copy = new String[CLASS_COUNT][CLASS_ATTRIBUTES];
        for (int i = 0; i < CLASS_COUNT; i++) {
            Arrays.fill(copy[i], "");
        }
        if (source != null) {
            int rows = Math.min(source.length, CLASS_COUNT);
            for (int i = 0; i < rows; i++) {
                String[] row = source[i];
                if (row == null) {
                    continue;
                }
                int cols = Math.min(row.length, CLASS_ATTRIBUTES);
                for (int j = 0; j < cols; j++) {
                    copy[i][j] = row[j] == null ? "" : row[j];
                }
            }
        }
        return copy;
    }

    private double[] normaliseMinutes(double[] source) {
        double[] copy = new double[DAY_COUNT];
        if (source != null) {
            for (int i = 0; i < Math.min(source.length, DAY_COUNT); i++) {
                copy[i] = source[i];
            }
        }
        return copy;
    }
}