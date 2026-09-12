package org.ontology.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderer przedstawiający wartość komórki tabeli jako przycisk bez obsługi akcji.
 */
class SimpleButtonRenderer extends JButton implements TableCellRenderer {

    /**
     * Tworzy renderer z włączonym oznaczaniem fokusu.
     */
    public SimpleButtonRenderer() {
        setFocusPainted(true);
    }

    /**
     * Konfiguruje przycisk używany do narysowania komórki tabeli.
     *
     * @param table tabela zawierająca komórkę
     * @param value wartość wyświetlana na przycisku
     * @param isSelected czy wiersz jest zaznaczony
     * @param hasFocus czy komórka ma fokus
     * @param row indeks wiersza
     * @param column indeks kolumny
     * @return skonfigurowany przycisk renderujący komórkę
     */
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        setText(value.toString());
        return this;
    }
}
