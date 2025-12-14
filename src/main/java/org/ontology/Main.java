package org.ontology;

import org.ontology.service.AppService;
import org.ontology.ui.AppWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppService service = new AppService("ontology.rdf");
            new AppWindow(service);
        });
    }
}