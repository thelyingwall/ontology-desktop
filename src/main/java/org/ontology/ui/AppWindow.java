package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.TableColumn;

public class AppWindow extends JFrame{

    private JComboBox<String> comboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel resultsInfoLabel;
    private JButton loadButton;
    private JButton addButton;

    private final AppService appService;

    public AppWindow(AppService appService) {
        super("Ontology Desktop App");
        this.appService = appService;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        root.add(createFormPanel(), BorderLayout.NORTH);
        root.add(createResultsPanel(), BorderLayout.CENTER);

        attachLoadButtonListener();
        attachAddButtonListener();

        setContentPane(root);
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel comboLabel = new JLabel("Wybierz klasę:");
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        form.add(comboLabel, gbc);

        comboBox = new JComboBox<>();
        comboBox.setModel(
                new DefaultComboBoxModel<>(
                        appService.getClasses().toArray(new String[0])
                )
        );
        comboLabel.setLabelFor(comboBox);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        form.add(comboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton loadButton = new JButton("Załaduj");
        JButton addButton = new JButton("Dodaj");

        buttonPanel.add(loadButton);
        buttonPanel.add(addButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(buttonPanel, gbc);

        this.loadButton = loadButton;
        this.addButton = addButton;

        return form;
    }

    private JPanel createResultsPanel() {
        resultsInfoLabel = new JLabel();
        resultsInfoLabel.setBorder(
                BorderFactory.createEmptyBorder(4, 0, 4, 0)
        );

        tableModel = new DefaultTableModel(
                new Object[]{"Lp.", "Rekord", "Akcja"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        table = new JTable(tableModel);
        configureTable(table);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(
                BorderFactory.createTitledBorder("Wyniki")
        );
        resultsPanel.add(resultsInfoLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        return resultsPanel;
    }

    private void configureTable(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumn lpCol = table.getColumnModel().getColumn(0);
        lpCol.setMinWidth(30);
        lpCol.setMaxWidth(30);

        TableColumn akcjaCol = table.getColumnModel().getColumn(2);
        akcjaCol.setMinWidth(100);
        akcjaCol.setMaxWidth(100);

        akcjaCol.setCellRenderer(new ButtonRenderer());
        akcjaCol.setCellEditor(new ButtonEditor(this, appService));
    }

    private void attachLoadButtonListener() {
        loadButton.addActionListener(e -> loadInstances());
    }

    private void attachAddButtonListener() {
        addButton.addActionListener(e -> addInstance());
    }

    private void loadInstances() {
        String selectedClass = comboBox.getSelectedItem().toString();
        List<String> instances = appService.getInstancesOfClass(selectedClass);

        tableModel.setRowCount(0);

        int count = instances.size();
        resultsInfoLabel.setText(
                count == 0
                        ? "Brak wyników dla " + selectedClass
                        : "Ilość znalezionych wyników dla "
                        + selectedClass + ": " + count
        );

        int lp = 1;
        for (String inst : instances) {
            tableModel.addRow(new Object[]{lp++ + ".", inst, "Szczegóły"});
        }
    }

    private void addInstance() {
        String selectedClass = comboBox.getSelectedItem().toString();

        DetailsDialog dialog = new DetailsDialog(this, appService, selectedClass);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadInstances();
        }
    }
}
