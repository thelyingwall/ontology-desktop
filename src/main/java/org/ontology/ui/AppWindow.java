package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
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

        createMenuBar();

        setupMenuShortcuts();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Menu");

        JMenuItem openItem = new JMenuItem("Wczytaj ontologię z pliku");
        openItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("src/main/resources");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                boolean success = appService.loadFile(selectedFile);
                System.out.println("Wybrano plik: " + selectedFile.getAbsolutePath());

                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Ontologia " + selectedFile.getName() + " została wczytana poprawnie",
                            "Sukces",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Błąd podczas wczytania ontologii " + selectedFile.getName(),
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JMenuItem saveItem = new JMenuItem("Zapisz ontologię do pliku");
        saveItem.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser("src/main/resources");
            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {

                File selectedFile = chooser.getSelectedFile();
                boolean success = appService.saveFile(selectedFile);

                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Ontologia " + selectedFile.getName() + " została zapisana poprawnie",
                            "Sukces",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Błąd podczas zapisu ontologii " + selectedFile.getName(),
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JMenuItem exitItem = new JMenuItem("Zamknij");
        exitItem.addActionListener(e -> {
            System.exit(0);
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
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
        comboBox.setFocusable(true);
        comboLabel.setLabelFor(comboBox);
        comboBox.getAccessibleContext().setAccessibleName("Wybierz klasę");
        comboBox.getAccessibleContext().setAccessibleDescription(
                "Lista rozwijana z wyborem klasy"
        );

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
        boolean newInstancesDisabled = appService.isAbstractClass(selectedClass);
        if (newInstancesDisabled) {
            JOptionPane.showMessageDialog(
                    this,
                    "Do klasy " + selectedClass + " nie można dodać indywiduum",
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            DetailsDialog dialog = new DetailsDialog(this, appService, selectedClass);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadInstances();
            }
        }
    }

    private void setupMenuShortcuts() {
        JRootPane rootPane = getRootPane();

        // Ctrl + \
        KeyStroke openMenuKey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, InputEvent.CTRL_DOWN_MASK);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(openMenuKey, "openMenu");
        rootPane.getActionMap().put("openMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenu fileMenu = getJMenuBar().getMenu(0);
                fileMenu.doClick();
                fileMenu.getMenuComponent(0).requestFocusInWindow();
            }
        });

        // Escape
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "selectClassList");
        rootPane.getActionMap().put("selectClassList", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                comboBox.requestFocusInWindow(); // zamień na Twój główny komponent
            }
        });
    }

}
