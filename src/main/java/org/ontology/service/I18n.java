package org.ontology.service;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Udostępnia bieżący pakiet tłumaczeń oraz metody pobierania zlokalizowanych tekstów.
 */
public class I18n {
    private static ResourceBundle bundle;

    /**
     * Ustawia język używany do wyszukiwania tekstów interfejsu.
     *
     * @param locale wybrany język
     */
    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n/messages", locale);
    }

    /**
     * Zwraca tekst przypisany do klucza w aktualnym pakiecie zasobów.
     *
     * @param key klucz komunikatu
     * @return przetłumaczony tekst komunikatu
     */
    public static String t(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "???" + key + "???";
        }
    }

    static {
        setLocale(Locale.getDefault());
    }
}
