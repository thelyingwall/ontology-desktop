package org.ontology.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class ButtonRenderer extends JPanel implements TableCellRenderer {

    private final JButton detailsButton = new JButton("Szczegóły");
    private final JButton editButton = new JButton("Edytuj");
    private final JButton deleteButton = new JButton("Usuń");

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        add(detailsButton);
        add(editButton);
        add(deleteButton);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        return this;
    }
}