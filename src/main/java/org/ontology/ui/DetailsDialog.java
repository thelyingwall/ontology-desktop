package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.ontology.service.PropertyKeys.*;

public class DetailsDialog extends JDialog {

    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    public DetailsDialog(Frame owner, AppService appService, String rekord, Boolean isView) {
        super(owner, "Szczegóły", true);
        Map<String, String> properties = appService.getPropertiesOfInstance(rekord);

        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel recordLabel = new JLabel(rekord);
        recordLabel.setFont(recordLabel.getFont().deriveFont(Font.BOLD));

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        topPanel.add(recordLabel, gbc);

        int row = 1;

        for (Map.Entry<String, String> entry : properties.entrySet()) {

            String property = entry.getKey();
            String value = entry.getValue();

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            JLabel label = new JLabel(property + ":");
            topPanel.add(label, gbc);

            JTextField field = new JTextField(30);
            field.setText(value != null ? value : "Brak danych");
            field.setEditable(false);

            label.setLabelFor(field);

            field.getAccessibleContext().setAccessibleName(property);
            field.getAccessibleContext().setAccessibleDescription(
                    "Wartość pola " + property
            );

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            topPanel.add(field, gbc);

            row++;
        }

        add(topPanel, BorderLayout.NORTH);

        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public DetailsDialog(Frame owner, AppService appService, String className) {
        super(owner, "Nowa instancja", true);
        boolean noLocalization = appService.noLocalization(className);
        Map<String, String> properties = new LinkedHashMap<>();

        setSize(600, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel recordLabel = new JLabel("Nowa instancja dla klasy " + className);
        recordLabel.setFont(recordLabel.getFont().deriveFont(Font.BOLD));

        recordLabel.getAccessibleContext().setAccessibleName(
                "Informacja"
        );
        recordLabel.getAccessibleContext().setAccessibleDescription(
                "Nowa instancja dla klasy " + className
        );

        recordLabel.setFocusable(true);
        recordLabel.requestFocusInWindow();

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        topPanel.add(recordLabel, gbc);

        JLabel nameLabel = new JLabel(NAMED_INDIVIDUAL + ":");
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(30);
        String prefix = AppService.decapitalize(className);
        nameField.setText(prefix);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(nameField, gbc);

        JTextField latitudeField = new JTextField(10);
        JTextField longitudeField = new JTextField(10);

        if (!noLocalization) {
            JLabel localizationLabel = new JLabel("Lokalizacja:");
            gbc.gridy = 2;
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            topPanel.add(localizationLabel, gbc);

            JPanel gpsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

            gpsPanel.add(new JLabel("szer. geo.:"));
            gpsPanel.add(latitudeField);
            gpsPanel.add(new JLabel("dł. geo.:"));
            gpsPanel.add(longitudeField);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            topPanel.add(gpsPanel, gbc);
        }

        JLabel commentLabel = new JLabel(COMMENT + ":");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(commentLabel, gbc);

        JTextField commentField = new JTextField(30);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(commentField, gbc);

        add(topPanel, BorderLayout.NORTH);

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String latitude = latitudeField.getText().trim();
            String longitude = longitudeField.getText().trim();
            String comment = commentField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pole " + NAMED_INDIVIDUAL + " jest wymagane!", "Błąd", JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                return;
            }
            if (!noLocalization && (latitude.isEmpty() || longitude.isEmpty())) {
                JOptionPane.showMessageDialog(this, "Pole Lokalizacja jest wymagane!", "Błąd", JOptionPane.ERROR_MESSAGE);
                latitudeField.requestFocus();
                longitudeField.requestFocus();
                return;
            }

            boolean success = false;
            String message;
            int messageType;

            try {
                properties.put(TYPE, className);
                properties.put(NAMED_INDIVIDUAL, nameField.getText());
                if (!longitudeField.getText().isEmpty() && !latitudeField.getText().isEmpty())
                    properties.put(LOCATION_GPS_COORDINATES, longitudeField.getText() + ", " + latitudeField.getText());
                if (!commentField.getText().isEmpty())
                    properties.put(COMMENT, commentField.getText());
                success = appService.saveInstance(properties);
                if (success) {
                    saved = true;
                    message = "Sukces!";
                    messageType = JOptionPane.INFORMATION_MESSAGE;
                } else {
                    message = "Błąd";
                    messageType = JOptionPane.ERROR_MESSAGE;
                }
            } catch (Exception ex) {
                message = "Wystąpił błąd: " + ex.getMessage();
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
