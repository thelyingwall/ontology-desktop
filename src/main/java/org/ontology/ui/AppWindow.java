package org.ontology.ui;

import org.ontology.service.AppService;
import org.ontology.service.I18n;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.table.TableColumn;

/**
 * Główne okno aplikacji, umożliwiające wybór klas i zarządzanie ich indywiduami.
 */
public class AppWindow extends JFrame{

    private JComboBox<String> comboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel resultsInfoLabel;
    private JButton loadButton;
    private JButton addButton;

    private final AppService appService;

    /**
     * Tworzy główne okno aplikacji i inicjalizuje jego komponenty.
     *
     * @param appService serwis obsługujący model ontologii
     */
    public AppWindow(AppService appService) {
        super("Ontology Desktop App");
        this.appService = appService;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    /**
     * Składa główny układ okna, menu i obsługę zdarzeń.
     */
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

    /**
     * Tworzy pasek menu z operacjami plikowymi, wyszukiwaniem oraz wyborem języka.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(I18n.t("menu.label"));

        JMenuItem openItem = new JMenuItem(I18n.t("menu.loadOntologyFromFile"));
        openItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("src/main/resources");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                boolean success = appService.loadFile(selectedFile);
                System.out.println(MessageFormat.format(I18n.t("messageBox.file"),selectedFile.getAbsolutePath()));

                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            MessageFormat.format(I18n.t("messageBox.loadSuccess"), selectedFile.getName()),
                            I18n.t("messageBox.success"),
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            MessageFormat.format(I18n.t("messageBox.loadError"), selectedFile.getName()),
                            I18n.t("messageBox.error"),
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JMenuItem saveItem = new JMenuItem(I18n.t("menu.saveOntologyFromFile"));
        saveItem.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser("src/main/resources");
            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {

                File selectedFile = chooser.getSelectedFile();
                boolean success = appService.saveFile(selectedFile);

                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            MessageFormat.format(I18n.t("messageBox.saveSuccess"), selectedFile.getName()),
                            I18n.t("messageBox.success"),
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            MessageFormat.format(I18n.t("messageBox.saveError"), selectedFile.getName()),
                            I18n.t("messageBox.error"),
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JMenuItem relationItem = new JMenuItem(I18n.t("menu.addNewRelation"));
        relationItem.addActionListener(e -> {
            addRelation();
        });

        JMenuItem findItems = new JMenuItem(I18n.t("menu.search"));
        findItems.addActionListener(e -> {
            find();
        });

        JMenuItem findRelationItems = new JMenuItem(I18n.t("menu.searchIndividualsRelation"));
        findRelationItems.addActionListener(e -> {
            findRelations();
        });

        JMenuItem findRelationItemsByClass = new JMenuItem(I18n.t("menu.searchClassesRelation"));
        findRelationItemsByClass.addActionListener(e -> {
            findRelationsByClass();
        });

        JMenu languageSubMenu = new JMenu(I18n.t("menu.language"));

        JMenuItem polishItem = new JMenuItem(I18n.t("menu.language.polish"));
        polishItem.addActionListener(e -> {
            I18n.setLocale(new Locale("pl"));
            refreshUI();
        });

        JMenuItem englishItem = new JMenuItem(I18n.t("menu.language.english"));
        englishItem.addActionListener(e -> {
            I18n.setLocale(Locale.ENGLISH);
            refreshUI();
        });

        languageSubMenu.add(polishItem);
        languageSubMenu.add(englishItem);

        JMenuItem exitItem = new JMenuItem(I18n.t("button.close"));
        exitItem.addActionListener(e -> {
            System.exit(0);
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(relationItem);
        fileMenu.add(findItems);
        fileMenu.add(findRelationItems);
        fileMenu.add(findRelationItemsByClass);
        fileMenu.add(languageSubMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Tworzy panel wyboru klasy oraz przyciski ładowania i dodawania indywiduów.
     *
     * @return skonfigurowany panel formularza
     */
    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel comboLabel = new JLabel(I18n.t("chooseClass"));
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
        comboBox.getAccessibleContext().setAccessibleName(I18n.t("chooseClass"));
        comboBox.getAccessibleContext().setAccessibleDescription(I18n.t("classDropdown"));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        form.add(comboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton loadButton = new JButton(I18n.t("button.load"));
        JButton addButton = new JButton(I18n.t("button.add"));

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

    /**
     * Tworzy panel tabeli wyświetlającej indywidua wybranej klasy.
     *
     * @return skonfigurowany panel wyników
     */
    private JPanel createResultsPanel() {
        resultsInfoLabel = new JLabel();
        resultsInfoLabel.setBorder(
                BorderFactory.createEmptyBorder(4, 0, 4, 0)
        );

        tableModel = new DefaultTableModel(
                new Object[]{
                        I18n.t("results.lp"),
                        I18n.t("results.name"),
                        I18n.t("results.details"),
                        I18n.t("results.edit"),
                        I18n.t("results.delete")}, 0
        ) {
            /**
             * Pozwala edytować wyłącznie kolumny zawierające przyciski akcji.
             *
             * @param row indeks wiersza
             * @param column indeks kolumny
             * @return {@code true} dla kolumn akcji
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };

        table = new JTable(tableModel);
        configureTable(table);

        Utils.setAccessible(table, I18n.t("results.table.header"));
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(
                BorderFactory.createTitledBorder(I18n.t("results.label"))
        );
        resultsPanel.add(resultsInfoLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        return resultsPanel;
    }

    /**
     * Ustawia rozmiary kolumn oraz renderery i edytory przycisków tabeli.
     *
     * @param table tabela wyników do skonfigurowania
     */
    private void configureTable(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumn lpCol = table.getColumnModel().getColumn(0);
        lpCol.setMinWidth(30);
        lpCol.setMaxWidth(30);

        for (int i = 2; i <= 4; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setMinWidth(110);
            col.setMaxWidth(110);
            col.setCellRenderer(new SimpleButtonRenderer());
            col.setCellEditor(new ActionButtonEditor(this, appService, table, i));
        }
    }

    /**
     * Podłącza obsługę przycisku ładującego indywidua wybranej klasy.
     */
    private void attachLoadButtonListener() {
        loadButton.addActionListener(e -> loadInstances());
    }

    /**
     * Podłącza obsługę przycisku otwierającego formularz nowego indywiduum.
     */
    private void attachAddButtonListener() {
        addButton.addActionListener(e -> addInstance());
    }

    /**
     * Pobiera indywidua wybranej klasy i odświeża tabelę wyników.
     */
    public void loadInstances() {
        String selectedClass = comboBox.getSelectedItem().toString();
        List<String> instances = appService.getInstancesOfClass(selectedClass);

        tableModel.setRowCount(0);

        int count = instances.size();
        resultsInfoLabel.setText(
                count == 0
                        ? MessageFormat.format(I18n.t("results.empty"), selectedClass)
                        : MessageFormat.format(I18n.t("results.count"), selectedClass, count)
        );
        Utils.setAccessible(resultsInfoLabel, resultsInfoLabel.getText());

        int lp = 1;
        for (String inst : instances) {
            tableModel.addRow(new Object[]{
                    lp++ + ".",
                    inst,
                    I18n.t("button.details"),
                    I18n.t("button.edit"),
                    I18n.t("button.delete")
            });
        }
    }

    /**
     * Otwiera formularz tworzenia indywiduum, jeśli wybrana klasa nie jest abstrakcyjna.
     */
    private void addInstance() {
        String selectedClass = comboBox.getSelectedItem().toString();
        boolean newInstancesDisabled = appService.isAbstractClass(selectedClass);
        if (newInstancesDisabled) {
            JOptionPane.showMessageDialog(
                    this,
                    MessageFormat.format(I18n.t("messageBox.addIndividualError"), selectedClass),
                    I18n.t("messageBox.error"),
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

    /**
     * Otwiera dialog dodawania relacji między indywiduami.
     */
    private void addRelation() {
        AddRelationDialog dialog = new AddRelationDialog(this, appService);
        dialog.setVisible(true);
    }

    /**
     * Otwiera dialog wyszukiwania indywiduów po właściwości.
     */
    private void find() {
        FindDialog dialog = new FindDialog(this, appService);
        dialog.setVisible(true);
    }

    /**
     * Otwiera dialog wyszukiwania relacji wskazanego indywiduum.
     */
    private void findRelations() {
        FindDialog dialog = new FindDialog(this, appService, true, false);
        dialog.setVisible(true);
    }

    /**
     * Otwiera dialog wyszukiwania relacji powiązanych z wybraną klasą.
     */
    private void findRelationsByClass() {
        FindDialog dialog = new FindDialog(this, appService, true, true);
        dialog.setVisible(true);
    }

    /**
     * Rejestruje skróty klawiaturowe dla menu oraz listy wyboru klasy.
     */
    private void setupMenuShortcuts() {
        JRootPane rootPane = getRootPane();

        // Ctrl + \
        KeyStroke openMenuKey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, InputEvent.CTRL_DOWN_MASK);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(openMenuKey, "openMenu");
        rootPane.getActionMap().put("openMenu", new AbstractAction() {
            /**
             * Otwiera pierwsze menu i przekazuje fokus jego pierwszej pozycji.
             *
             * @param e zdarzenie wywołujące akcję
             */
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
            /**
             * Zamyka rozwinięte menu i przekazuje fokus liście klas.
             *
             * @param e zdarzenie wywołujące akcję
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                comboBox.requestFocusInWindow();
            }
        });
    }

    /**
     * Buduje ponownie interfejs po zmianie języka i odświeża komponenty Swing.
     */
    private void refreshUI() {
        getContentPane().removeAll();
        getJMenuBar().removeAll();

        createMenuBar();
        initComponents();

        SwingUtilities.updateComponentTreeUI(this);
        revalidate();
        repaint();
    }

}
