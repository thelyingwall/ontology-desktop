package org.ontology.ui;

import org.ontology.service.AppService;
import org.ontology.service.PropertyType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

public class FindDialog extends JDialog {

    private JComboBox<String> combo1;

    public FindDialog(Frame owner, AppService appService) {
        super(owner, "Szukaj", true);
        setupMenuShortcuts();
        setSize(600, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));


        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridwidth = 2;

        int row = 0;


        JLabel label1 = new JLabel("Wybierz klasę:");
        gbc.gridy = row++;
        topPanel.add(label1, gbc);

        combo1 =
                new JComboBox<>(appService.getClasses().toArray(new String[0]));
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo1, gbc);


        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label2 = new JLabel("Wybierz właściwość:");
        gbc.gridy = row++;
        topPanel.add(label2, gbc);

        JComboBox<String> combo2 = new JComboBox<>(
                Arrays.stream(PropertyType.values())
                        .map(Enum::name)
                        .sorted()
                        .toArray(String[]::new)
        );
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo2, gbc);


        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label3 = new JLabel("Wartość:");
        gbc.gridy = row++;
        topPanel.add(label3, gbc);

        JTextField valueField = new JTextField(30);
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(valueField, gbc);

        add(topPanel, BorderLayout.NORTH);


        JButton searchButton = new JButton("Szukaj");
        JButton cancelButton = new JButton("Anuluj");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.add(searchButton);
        searchPanel.add(cancelButton);

        DefaultTableModel tableModel =
                new DefaultTableModel(new Object[]{"Lp.", "Wyniki"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        JTable resultTable = new JTable(tableModel);

        resultTable = new JTable(tableModel);
        configureTable(resultTable);

        Utils.setAccessible(resultTable, "Tabela, nagłówki: Lp., Nazwa");

        resultTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(resultTable);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(5, 5));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);


        searchButton.addActionListener(e -> {
            String selectedClass = (String) combo1.getSelectedItem();
            String selectedProperty = (String) combo2.getSelectedItem();
            String selectedValue = valueField.getText();

            List<String> results =
                    appService.findIndividualsByClassAndProperty(
                            selectedClass, selectedProperty, selectedValue
                    );

            tableModel.setRowCount(0);

            if (results == null || results.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Nie znaleziono wyników.",
                        "Brak wyników",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            int lp = 1;
            for (String r : results) {
                lp = tableModel.getRowCount() + 1;
                tableModel.addRow(new Object[]{lp, r});
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void configureTable(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumn lpCol = table.getColumnModel().getColumn(0);
        lpCol.setMinWidth(30);
        lpCol.setMaxWidth(30);

        TableColumn individualCol = table.getColumnModel().getColumn(1);
        individualCol.setMinWidth(150);

    }

    private void setupMenuShortcuts() {
        JRootPane rootPane = getRootPane();

        // Escape
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "selectClassList");
        rootPane.getActionMap().put("selectClassList", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                combo1.requestFocusInWindow();
            }
        });
    }
}
