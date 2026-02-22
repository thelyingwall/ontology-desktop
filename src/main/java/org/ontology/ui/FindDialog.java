package org.ontology.ui;

import org.ontology.models.IndividualsByRelations;
import org.ontology.models.SearchedIndividualsRelations;
import org.ontology.service.AppService;
import org.ontology.enums.PropertyType;
import org.ontology.enums.RelationType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindDialog extends JDialog {

    private JComboBox<String> combo1;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private List<IndividualsByRelations> results;
    private JLabel executionTimeLabel;

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

        combo1 = new JComboBox<>(appService.getClasses().toArray(new String[0]));
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

        tableModel = new DefaultTableModel(
                new Object[]{"Lp.", "Wyniki"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

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

    public FindDialog(Frame owner, AppService appService, boolean searchRelations, boolean searchByClass) {
        super(owner, "Szukaj", true);
        setupMenuShortcuts();
        setSize(1000, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        results = new ArrayList<>();

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridwidth = 2;

        int row = 0;

        // --- ComboBox 1 ---
        JLabel label1 = new JLabel(searchByClass ? "Wybierz klasę:" : "Wybierz indywiduum:");
        gbc.gridy = row++;
        topPanel.add(label1, gbc);

        combo1 = new JComboBox<>(
            searchByClass ? appService.getClasses().toArray(new String[0])
            : appService.getAllInstances().toArray(new String[0]));
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo1, gbc);

        Utils.setAccessible(combo1, searchByClass ? "Klasa" : "Indywiduum");

        // --- ComboBox 2 ---
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label2 = new JLabel("Wybierz typ relacji:");
        gbc.gridy = row++;
        topPanel.add(label2, gbc);

        JComboBox<String> combo2 = new JComboBox<>(
                Arrays.stream(RelationType.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
        );
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo2, gbc);

        Utils.setAccessible(combo2, "Typ relacji");

        add(topPanel, BorderLayout.NORTH);

        JButton searchButtonSPARQL = new JButton("Szukaj SPARQL");
        JButton searchButton = new JButton("Szukaj API JENA");
        JButton exportButton = new JButton("Eksport do CSV");
        JButton cancelButton = new JButton("Anuluj");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.add(searchButtonSPARQL);
        searchPanel.add(searchButton);
        searchPanel.add(exportButton);
        searchPanel.add(cancelButton);

        tableModel = new DefaultTableModel(
                new Object[]{"Lp.", "Indywiduum źródłowe", "Nazwa relacji", "Połączone indywiduum"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        resultTable = new JTable(tableModel);
        configureTable(resultTable, true);

        Utils.setAccessible(resultTable, "Tabela, nagłówki: Lp., Indywiduum źródłowe, Nazwa relacji, Połączone indywiduum");

        resultTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(resultTable);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(5, 5));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);


        executionTimeLabel = new JLabel();
        executionTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        executionTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel tableTopPanel = new JPanel(new BorderLayout());
        tableTopPanel.add(executionTimeLabel, BorderLayout.NORTH);
        tableTopPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tableTopPanel, BorderLayout.CENTER);


        add(centerPanel, BorderLayout.CENTER);

        searchButtonSPARQL.addActionListener(e -> {

            String selectedIndividual = (String) combo1.getSelectedItem();
            String selectedRelation = (String) combo2.getSelectedItem();

            SearchedIndividualsRelations temp = searchByClass ? appService.findRelationsByClassSPARQL(selectedIndividual, selectedRelation)
                : appService.findIndividualsByRelationSPARQL(selectedIndividual, selectedRelation);
            results = temp.getRelations();

            displayResults(results);
            executionTimeLabel.setText("Wyniki: %d, Czas: %s".formatted(
                    temp.getRelations().size(),
                    temp.getTime()
            ));
            Utils.setAccessible(executionTimeLabel, executionTimeLabel.getText());
        });

        searchButton.addActionListener(e -> {

            String selectedIndividual = (String) combo1.getSelectedItem();
            String selectedRelation = (String) combo2.getSelectedItem();

            SearchedIndividualsRelations temp = searchByClass ? appService.findRelationsByClass(selectedIndividual, selectedRelation)
                : appService.findIndividualsByRelation(selectedIndividual, selectedRelation);
            results = temp.getRelations();

            displayResults(results);
            executionTimeLabel.setText("Wyniki: %d, Czas: %s".formatted(
                    temp.getRelations().size(),
                    temp.getTime()
            ));
            Utils.setAccessible(executionTimeLabel, executionTimeLabel.getText());
        });

        exportButton.addActionListener(e -> {
            if (results == null || results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Brak danych do eksportu.",
                        "Eksport CSV",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String fileName = "export.csv";

            File resourcesDir = new File("src/main/resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }

            File fileToSave = new File(resourcesDir, fileName);

            boolean success = appService.exportCSV(results, fileToSave);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Eksport zakończony pomyślnie.\nPlik: src/main/resources/" + fileToSave.getName(),
                        "Eksport CSV",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Błąd przy eksporcie CSV.",
                        "Eksport CSV",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void configureTable(JTable table) {
        configureTable(table, false);
    }

    private void configureTable(JTable table, boolean relations) {
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumn lpCol = table.getColumnModel().getColumn(0);
        lpCol.setMinWidth(30);
        lpCol.setMaxWidth(30);

        if (relations) {
            TableColumn relationCol = table.getColumnModel().getColumn(1);
            relationCol.setMinWidth(100);

            TableColumn resultCol = table.getColumnModel().getColumn(2);
            resultCol.setMinWidth(100);
        } else {
            TableColumn individualCol = table.getColumnModel().getColumn(1);
            individualCol.setMinWidth(150);
        }
    }

    private void displayResults(List<IndividualsByRelations> results) {
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
        for (IndividualsByRelations r : results) {
            tableModel.addRow(new Object[]{
                    lp++,
                    r.getSourceIndividual(),
                    r.getRelation(),
                    r.getTargetIndividual()
            });
        }
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
