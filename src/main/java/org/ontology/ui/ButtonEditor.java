package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private final JButton button = new JButton("Szczegóły");
    private String rekord;

    public ButtonEditor(JFrame owner, AppService appService) {
        button.addActionListener(e -> {
            fireEditingStopped();
            DetailsDialog dialog = new DetailsDialog(owner, appService, rekord);
            dialog.setVisible(true);
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected,
            int row, int column) {

        rekord = table.getValueAt(row, 1).toString();
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Szczegóły";
    }
}

