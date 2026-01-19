package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private final JPanel panel = new JPanel();
    private final JButton detailsButton = new JButton("Szczegóły");
    private final JButton editButton = new JButton("Edytuj");
    private final JButton deleteButton = new JButton("Usuń");

    private String rekord;
    private final JFrame owner;
    private final AppService appService;
    private final JTable table;


    public ButtonEditor(JFrame owner, AppService appService, JTable table) {
        this.owner = owner;
        this.appService = appService;
        this.table = table;

        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.add(detailsButton);
        panel.add(editButton);
        panel.add(deleteButton);

        detailsButton.addActionListener(e -> {
            fireEditingStopped();
            DetailsDialog dialog = new DetailsDialog(owner, appService, rekord, true);
            dialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            fireEditingStopped();
            DetailsDialog dialog = new DetailsDialog(owner, appService, rekord, false);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                ((AppWindow) owner).loadInstances();
            }
        });

        deleteButton.addActionListener(e -> {
            fireEditingStopped();
            int confirm = JOptionPane.showConfirmDialog(
                    owner,
                    "Czy na pewno chcesz usunąć rekord \"" + rekord + "\"?",
                    "Potwierdzenie",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                appService.deleteInstance(rekord);
                ((AppWindow) owner).loadInstances();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        rekord = table.getValueAt(row, 1).toString();
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return rekord;
    }

}