package org.ontology;

import org.ontology.service.AppService;
import org.ontology.ui.AppWindow;

import javax.swing.*;

/**
 * Punkt wejścia aplikacji desktopowej do przeglądania i edycji ontologii.
 */
public class Main {
    /**
     * Uruchamia interfejs użytkownika na wątku zdarzeń Swing. Wczytuje domyślna ontologię
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppService service = new AppService("ontology.rdf");
            new AppWindow(service);
        });
    }
}
