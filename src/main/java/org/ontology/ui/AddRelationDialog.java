package org.ontology.ui;

import org.ontology.service.AppService;
import org.ontology.service.RelationType;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AddRelationDialog extends JDialog {

    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    //nowa relacja
    public AddRelationDialog(Frame owner, AppService appService) {
        super(owner, "Nowa relacja", true);

        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel recordLabel = new JLabel("Nowa relacja");
        recordLabel.setFont(recordLabel.getFont().deriveFont(Font.BOLD));

        Utils.setAccessible(recordLabel, "Nowa relacja");

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        topPanel.add(recordLabel, gbc);

        int row = 1;

// --- ComboBox 1 ---
        JLabel label1 = new JLabel("Wybierz indywiduum:");
        gbc.gridy = row++;
        topPanel.add(label1, gbc);

        JComboBox<String> combo1 = new JComboBox<>(appService.getAllInstances().toArray(new String[0]));
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo1, gbc);

        Utils.setAccessible(combo1, "Indywiduum 1");

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

// --- ComboBox 3 ---
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label3 = new JLabel("Wybierz docelowe indywiduum:");
        gbc.gridy = row++;
        topPanel.add(label3, gbc);

        JComboBox<String> combo3 = new JComboBox<>(appService.getAllInstances().toArray(new String[0]));
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo3, gbc);

        Utils.setAccessible(combo3, "Docelowe indywiduum");

        add(topPanel, BorderLayout.NORTH);

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            String selectedIndividual1 = (String) combo1.getSelectedItem();
            String selectedRelationType = (String) combo2.getSelectedItem();
            String selectedIndividual2 = (String) combo3.getSelectedItem();

            if (selectedIndividual1.equals(selectedIndividual2)) {
                JOptionPane.showMessageDialog(this, "Wybrano te same indywidua!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = false;
            String message;
            int messageType;

            success = appService.addNewRelation(selectedIndividual1, selectedRelationType, selectedIndividual2);
            if (success) {
                saved = true;
                message = "Sukces!";
                messageType = JOptionPane.INFORMATION_MESSAGE;
            } else {
                message = "Błąd";
                messageType = JOptionPane.ERROR_MESSAGE;
            }

            JOptionPane.showMessageDialog(this, message, "Informacja", messageType);

            if (success) dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
