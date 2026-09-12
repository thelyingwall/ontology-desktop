package org.ontology.ui;

import javax.swing.*;

/**
 * Wspólne narzędzia pomocnicze dla komponentów interfejsu Swing.
 */
public class Utils {

    /**
     * Nadaje komponentowi nazwę dostępną dla czytników i przekazuje mu fokus.
     *
     * @param c komponent, który ma zostać skonfigurowany
     * @param name opisowa nazwa komponentu dla czytnika ekranu
     */
    public static void setAccessible(JComponent c, String name) {
        var ac = c.getAccessibleContext();
        ac.setAccessibleName(name);
        c.setFocusable(true);
        c.requestFocusInWindow();
    }
}
