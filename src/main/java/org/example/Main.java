package org.example;

import org.example.service.AppService;
import org.example.ui.AppWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppService service = new AppService("ontology.rdf");
            new AppWindow(service);
        });
    }
}