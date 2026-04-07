package org.ontology.ui;

import org.ontology.service.AppService;
import org.ontology.enums.RelationType;
import org.ontology.service.I18n;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.Arrays;

public class AddRelationDialog extends JDialog {

    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    //nowa relacja
    public AddRelationDialog(Frame owner, AppService appService) {
        super(owner, I18n.t("newRelation"), true);

        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel recordLabel = new JLabel(I18n.t("newRelation"));
        recordLabel.setFont(recordLabel.getFont().deriveFont(Font.BOLD));

        Utils.setAccessible(recordLabel, I18n.t("newRelation"));

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        topPanel.add(recordLabel, gbc);

        int row = 1;

// --- ComboBox 1 ---
        JLabel label1 = new JLabel(I18n.t("chooseIndividual"));
        gbc.gridy = row++;
        topPanel.add(label1, gbc);

        JComboBox<String> combo1 = new JComboBox<>(appService.getAllInstances().toArray(new String[0]));
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo1, gbc);

        Utils.setAccessible(combo1, MessageFormat.format(I18n.t("individualLp"), 1));

// --- ComboBox 2 ---
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label2 = new JLabel(I18n.t("chooseRelationType"));
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

        Utils.setAccessible(combo2, I18n.t("relationType"));

// --- ComboBox 3 ---
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label3 = new JLabel(I18n.t("chooseTargetIndividual"));
        gbc.gridy = row++;
        topPanel.add(label3, gbc);

        JComboBox<String> combo3 = new JComboBox<>(appService.getAllInstances().toArray(new String[0]));
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(combo3, gbc);

        Utils.setAccessible(combo3, I18n.t("targetIndividual"));

        add(topPanel, BorderLayout.NORTH);

        JButton cancelButton = new JButton(I18n.t("button.cancel"));
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton(I18n.t("button.save"));
        saveButton.addActionListener(e -> {
            String selectedIndividual1 = (String) combo1.getSelectedItem();
            String selectedRelationType = (String) combo2.getSelectedItem();
            String selectedIndividual2 = (String) combo3.getSelectedItem();

            if (selectedIndividual1.equals(selectedIndividual2)) {
                JOptionPane.showMessageDialog(this,
                        I18n.t("messageBox.sameIndividualsError"),
                        I18n.t("messageBox.error")
                        , JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = false;
            String message;
            int messageType;

            success = appService.addNewRelation(selectedIndividual1, selectedRelationType, selectedIndividual2);
            if (success) {
                saved = true;
                message = I18n.t("messageBox.saveOntologyModelSuccess");
                messageType = JOptionPane.INFORMATION_MESSAGE;
            } else {
                message = I18n.t("messageBox.error");
                messageType = JOptionPane.ERROR_MESSAGE;
            }

            JOptionPane.showMessageDialog(this, message, I18n.t("information"), messageType);

            if (success) dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
