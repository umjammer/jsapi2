/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 22 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.speech;


import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static java.lang.System.getLogger;


/**
 * TODO this class should be replaced by Locale?
 * system property
 * <li>javax.speech.SpeechLocale.comparisonStrictness ... set strictness for #match() method values are
 *                                                        STRICT</li>
 *
 * @since 2.0.6
 */
public final class SpeechLocale {

    private static final Logger logger = getLogger(SpeechLocale.class.getName());

    public static final SpeechLocale ENGLISH;

    public static final SpeechLocale US;

    public static final SpeechLocale FRENCH;

    public static final SpeechLocale GERMAN;

    /**
     * The default locale. Except for during bootstrapping, this should never be
     * null. Note the logic in the main constructor, to detect when
     * bootstrapping has completed.
     */
    private static SpeechLocale DEFAULT_LOCALE;

    static {
        ENGLISH = new SpeechLocale("en");
        US = new SpeechLocale("en", "US");
        FRENCH = new SpeechLocale("fr");
        GERMAN = new SpeechLocale("de");

        String defaultLanguage = System.getProperty("microedition.locale");
        if (defaultLanguage == null) {
            defaultLanguage = "en";
        }
        DEFAULT_LOCALE = new SpeechLocale(defaultLanguage);
    }

    private String language;

    private String country;

    private String variant;

    /**
     * Convert new iso639 codes to the old ones.
     *
     * @param language the language to check
     * @return the appropriate code
     */
    private String convertLanguage(String language) {
        if (language.isEmpty())
            return language;
        language = language.toLowerCase();
        int index = "he,id,yi".indexOf(language);
        if (index != -1)
            return "iw,in,ji".substring(index, index + 2);
        return language;
    }

    public SpeechLocale(String language, String country, String variant) {
        this.language = language;
        this.country = country;
        this.variant = variant;
    }

    public SpeechLocale(String language, String country) {
        this(language, country, "");
    }

    public SpeechLocale(String language) {
        this(language, "", "");
    }

    public static SpeechLocale getDefault() {
        return DEFAULT_LOCALE;
    }

    public static SpeechLocale[] getAvailableLocales() {
        return new SpeechLocale[] {ENGLISH, US, FRENCH, GERMAN};
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getVariant() {
        return variant;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result
                + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((variant == null) ? 0 : variant.hashCode());
        return result;
    }

    /** for {@link #match(SpeechLocale)} */
    private enum ComparisonStrictness {
        /** match if on of them is matched */
        LENIENT,
        /** compare all */
        STRICT;
    };

    /** strictness for {@link #match(SpeechLocale)} */
    private static ComparisonStrictness comparisonStrictness;

    /* */
    static {
        String property = System.getProperty("javax.speech.SpeechLocale.comparisonStrictness", "STRICT");
        try {
            comparisonStrictness = ComparisonStrictness.valueOf(property.toUpperCase());
        } catch (Exception e) {
logger.log(Level.WARNING, "javax.speech.SpeechLocale.comparisonStrictness is wring: " + property);
            comparisonStrictness = ComparisonStrictness.STRICT;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SpeechLocale other = (SpeechLocale) obj;
        if (country == null) {
            if (other.country != null) {
                return false;
            }
        } else if (!country.equals(other.country)) {
            return false;
        }
        if (language == null) {
            if (other.language != null) {
                return false;
            }
        } else if (!language.equals(other.language)) {
            return false;
        }
        if (variant == null) {
            if (other.variant != null) {
                return false;
            }
        } else if (!variant.equals(other.variant)) {
            return false;
        }
        return true;
    }

    /**
     * @since 2.2.1 using {@link #comparisonStrictness} for comparison strictness.
     * @param require null means any match
     */
    public boolean match(SpeechLocale require) {
        if (require == null) {
            return true;
        }
        boolean countryMatched = true;
        if (!require.country.isEmpty()) {
            if (!require.country.equals(country)) {
                countryMatched = false;
            }
        }
        boolean languageMatched = true;
        if (!require.language.isEmpty()) {
            if (!require.language.equals(language)) {
                languageMatched = false;
            }
        }
        boolean variantMatched = true;
        if (!require.variant.isEmpty()) {
            if (!require.variant.equals(variant)) {
                variantMatched = false;
            }
        }
        return switch (comparisonStrictness) {
            case STRICT -> countryMatched && languageMatched && variantMatched;
            case LENIENT -> languageMatched || countryMatched && !require.country.isEmpty();
        };
    }

    public String toString() {
        if ((language.isEmpty()) && (country.isEmpty())) {
            return "";
        }

        StringBuilder str = new StringBuilder();
        str.append(language);
        if (!country.isEmpty()) {
            str.append('_').append(country);
        }
        if (!variant.isEmpty()) {
            str.append('_').append(variant);
        }

        return str.toString();
    }
}
