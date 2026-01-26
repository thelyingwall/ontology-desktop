package org.ontology.ui;

import javax.swing.*;

public class Utils {

    public static void setAccessible(JComponent c, String name) {
        var ac = c.getAccessibleContext();
        ac.setAccessibleName(name);
        c.setFocusable(true);
        c.requestFocusInWindow();
    }
}
