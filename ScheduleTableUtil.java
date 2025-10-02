import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public final class ScheduleTableUtil {

    private static final int[] COLUMN_WIDTHS = {180, 150, 150, 150, 150, 150, 150, 150};
    private static final DefaultTableCellRenderer CELL_RENDERER = new TooltipCellRenderer();

    private ScheduleTableUtil() {
    }

    public static void configure(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(38);
        table.setFillsViewportHeight(true);
        table.setFont(table.getFont().deriveFont(Font.PLAIN, 14f));
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 14f));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            int width = i < COLUMN_WIDTHS.length ? COLUMN_WIDTHS[i] : COLUMN_WIDTHS[COLUMN_WIDTHS.length - 1];
            column.setPreferredWidth(width);
            column.setMinWidth(width);
        }

        table.setDefaultRenderer(Object.class, CELL_RENDERER);
    }

    private static class TooltipCellRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            java.awt.Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                setToolTipText(value.toString());
            } else {
                setToolTipText(null);
            }
            return component;
        }
    }
}