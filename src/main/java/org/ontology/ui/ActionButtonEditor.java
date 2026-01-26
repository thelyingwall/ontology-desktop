package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private final JButton button = new JButton();
    private String rekord;
    private final JFrame owner;
    private final AppService appService;
    private final JTable table;
    private final int column;

    public ActionButtonEditor(
            JFrame owner,
            AppService appService,
            JTable table,
            int column) {

        this.owner = owner;
        this.appService = appService;
        this.table = table;
        this.column = column;

        button.addActionListener(e -> {
            fireEditingStopped();

            switch (column) {
                case 2 -> { // szczegóły
                    new DetailsDialog(owner, appService, rekord, true)
                            .setVisible(true);
                }
                case 3 -> { // edytuj
                    DetailsDialog d =
                            new DetailsDialog(owner, appService, rekord, false);
                    d.setVisible(true);
                    if (d.isSaved()) {
                        ((AppWindow) owner).loadInstances();
                    }
                }
                case 4 -> { // usuń
                    int confirm = JOptionPane.showConfirmDialog(
                            owner,
                            "Czy na pewno usunąć \"" + rekord + "\"?",
                            "Potwierdzenie",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        appService.deleteInstance(rekord);
                        ((AppWindow) owner).loadInstances();
                    }
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value,
            boolean isSelected, int row, int column) {

        rekord = table.getValueAt(row, 1).toString();
        button.setText(value.toString());
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }
}
