package org.ontology.ui;

import org.ontology.service.AppService;

import javax.swing.*;
import java.awt.*;

public class DetailsDialog extends JDialog {

    public DetailsDialog(Frame owner, AppService appService, String rekord) {
        super(owner, "Szczegóły rekordu", true);
        String gps = appService.getGpsCoordinatesOfInstance(rekord);

        setSize(600, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel recordLabel = new JLabel("Szczegóły rekordu: " + rekord);
        recordLabel.setFont(recordLabel.getFont().deriveFont(Font.BOLD));

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        topPanel.add(recordLabel, gbc);

        JLabel localizationLabel = new JLabel("location_gps_coordinates:");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        topPanel.add(localizationLabel, gbc);

        JTextField localizationField = new JTextField(30);
        localizationField.setText(
                gps != null ? gps : "Brak danych"
        );
        localizationField.setEditable(false);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        topPanel.add(localizationField, gbc);

        add(topPanel, BorderLayout.NORTH);

        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
