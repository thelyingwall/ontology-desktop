package org.ontology.ui;

import org.ontology.service.AppService;
import org.ontology.service.RelationType;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.ontology.service.PropertyKeys.*;

public class DetailsDialog extends JDialog {

    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }


    public DetailsDialog(Frame owner, AppService appService, String rekord, Boolean isView) {
        super(owner, isView ? "Szczegóły" : "Edycja", true);
        if (isView)
            this.details(owner, appService, rekord);
        else this.edit(owner, appService, rekord);
    }

    //szczegoly
    public void details(Frame owner, AppService appService, String rekord) {
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

            Utils.setAccessible(field, property);

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

//    edycja
    public void edit(Frame owner, AppService appService, String rekord) {
        Map<String, String> properties = appService.getPropertiesOfInstance(rekord);
        Map<String, JTextField> fields = new LinkedHashMap<>();

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
            field.setEditable(true);

            label.setLabelFor(field);

            Utils.setAccessible(field, property);

            fields.put(property, field);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            topPanel.add(field, gbc);

            row++;
        }

        add(topPanel, BorderLayout.NORTH);

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {

            Map<String, String> updatedValues = new LinkedHashMap<>();

            for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
                String property = entry.getKey();
                String value = entry.getValue().getText().trim();

                updatedValues.put(property, value);
            }

            appService.updateInstance(rekord, updatedValues);
            dispose();

//            String name = nameField.getText().trim();
//            String latitude = latitudeField.getText().replace(',', '.').trim();
//            String longitude = longitudeField.getText().replace(',', '.').trim();
//            String comment = commentField.getText().trim();
//
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Pole " + NAMED_INDIVIDUAL + " jest wymagane!", "Błąd", JOptionPane.ERROR_MESSAGE);
//                nameField.requestFocus();
//                return;
//            }
//            if (!noLocalization) {
//
//                if (latitude.isEmpty() || longitude.isEmpty()) {
//                    JOptionPane.showMessageDialog(this, "Pole Lokalizacja jest wymagane!", "Błąd", JOptionPane.ERROR_MESSAGE);
//                    latitudeField.requestFocus();
//                    longitudeField.requestFocus();
//                    return;
//                }
//
//                if (!appService.isValidLatitude(latitude)) {
//                    JOptionPane.showMessageDialog(
//                            this,
//                            "Niepoprawna szerokość geograficzna.\n" +
//                                    "Zakres: -90 do 90",
//                            "Błąd",
//                            JOptionPane.ERROR_MESSAGE
//                    );
//                    latitudeField.requestFocusInWindow();
//                    return;
//                }
//
//                if (!appService.isValidLongitude(longitude)) {
//                    JOptionPane.showMessageDialog(
//                            this,
//                            "Niepoprawna długość geograficzna.\n" +
//                                    "Zakres: -180 do 180",
//                            "Błąd",
//                            JOptionPane.ERROR_MESSAGE
//                    );
//                    longitudeField.requestFocusInWindow();
//                    return;
//                }
//            }
//
//            boolean success = false;
//            String message;
//            int messageType;
//
//            try {
//                properties.put(TYPE, className);
//                properties.put(NAMED_INDIVIDUAL, nameField.getText());
//                if (!longitudeField.getText().isEmpty() && !latitudeField.getText().isEmpty())
//                    properties.put(LOCATION_GPS_COORDINATES, latitudeField.getText().replace(',', '.') + ", " + longitudeField.getText().replace(',', '.'));
//                if (!commentField.getText().isEmpty())
//                    properties.put(COMMENT, commentField.getText());
//                success = appService.saveInstance(properties);
//                if (success) {
//                    saved = true;
//                    message = "Sukces!";
//                    messageType = JOptionPane.INFORMATION_MESSAGE;
//                } else {
//                    message = "Błąd";
//                    messageType = JOptionPane.ERROR_MESSAGE;
//                }
//            } catch (Exception ex) {
//                message = "Wystąpił błąd: " + ex.getMessage();
//                messageType = JOptionPane.ERROR_MESSAGE;
//            }

//            JOptionPane.showMessageDialog(this, message, "Informacja", messageType);
//
//            if (success) dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    //nowe indywiduum
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

        Utils.setAccessible(recordLabel, "Nowa instancja dla klasy " + className);

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
        Utils.setAccessible(nameField, NAMED_INDIVIDUAL);
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
            Utils.setAccessible(localizationLabel, "Lokalizacja");
            topPanel.add(localizationLabel, gbc);

            JPanel gpsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

            gpsPanel.add(new JLabel("szer. geo.:"));
            Utils.setAccessible(latitudeField, "szerokość geograficzna");
            gpsPanel.add(latitudeField);
            gpsPanel.add(new JLabel("dł. geo.:"));
            Utils.setAccessible(longitudeField, "długość geograficzna");
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
        Utils.setAccessible(commentField, COMMENT);
        topPanel.add(commentField, gbc);

        add(topPanel, BorderLayout.NORTH);

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String latitude = latitudeField.getText().replace(',', '.').trim();
            String longitude = longitudeField.getText().replace(',', '.').trim();
            String comment = commentField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pole " + NAMED_INDIVIDUAL + " jest wymagane!", "Błąd", JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                return;
            }
            if (!noLocalization) {

                if (latitude.isEmpty() || longitude.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Pole Lokalizacja jest wymagane!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    latitudeField.requestFocus();
                    longitudeField.requestFocus();
                    return;
                }

                if (!appService.isValidLatitude(latitude)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Niepoprawna szerokość geograficzna.\n" +
                                    "Zakres: -90 do 90",
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE
                    );
                    latitudeField.requestFocusInWindow();
                    return;
                }

                if (!appService.isValidLongitude(longitude)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Niepoprawna długość geograficzna.\n" +
                                    "Zakres: -180 do 180",
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE
                    );
                    longitudeField.requestFocusInWindow();
                    return;
                }
            }

            boolean success = false;
            String message;
            int messageType;

            try {
                properties.put(TYPE, className);
                properties.put(NAMED_INDIVIDUAL, nameField.getText());
                if (!longitudeField.getText().isEmpty() && !latitudeField.getText().isEmpty())
                    properties.put(LOCATION_GPS_COORDINATES, latitudeField.getText().replace(',', '.') + ", " + longitudeField.getText().replace(',', '.'));
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
