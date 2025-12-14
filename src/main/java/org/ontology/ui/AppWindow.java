package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppWindow extends JFrame{

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

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel comboLabel = new JLabel("Wybierz klasę:");
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        form.add(comboLabel, gbc);

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.getAccessibleContext()
                .setAccessibleName("Wybór klasy");
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        form.add(comboBox, gbc);
        comboLabel.setLabelFor(comboBox);

        comboBox.setModel(new DefaultComboBoxModel<>(appService.getClasses().toArray(new String[0])));

        JButton button = new JButton("Załaduj");
        button.getAccessibleContext()
                .setAccessibleName("Załaduj");
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(button, gbc);

        root.add(form, BorderLayout.NORTH);

        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Wyniki")
        );
        resultsArea.getAccessibleContext()
                .setAccessibleName("Wyniki");

        root.add(scrollPane, BorderLayout.CENTER);


        button.addActionListener(e -> {
            String selectedClass = comboBox.getSelectedItem().toString();
            List<String> instances = appService.getInstancesOfClass(selectedClass);

            resultsArea.setText("");
            for (String inst : instances) {
                resultsArea.append(inst + "\n");
            }
        });

        setContentPane(root);
    }
}
