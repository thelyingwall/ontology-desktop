package org.ontology.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class SimpleButtonRenderer extends JButton implements TableCellRenderer {

    public SimpleButtonRenderer() {
        setFocusPainted(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        setText(value.toString());
        return this;
    }
}
