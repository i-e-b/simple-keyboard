/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rkr.simplekeyboard.inputmethod.latin.common;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Locale;

public final class StringUtils {
    private static final String EMPTY_STRING = "";

    private StringUtils() {
        // This utility class is not publicly instantiable.
    }

    public static String newSingleCodePointString(final int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            // Optimization: avoid creating a temporary array for characters that are
            // represented by a single char value
            return String.valueOf((char) codePoint);
        }
        // For surrogate pair
        return new String(Character.toChars(codePoint));
    }

    public static boolean containsInArray(final String text,
            final String[] array) {
        for (final String element : array) {
            if (text.equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comma-Splittable Text is similar to Comma-Separated Values (CSV) but has much simpler syntax.
     * Unlike CSV, Comma-Splittable Text has no escaping mechanism, so that the text can't contain
     * a comma character in it.
     */
    private static final String SEPARATOR_FOR_COMMA_SPLITTABLE_TEXT = ",";

    public static boolean containsInCommaSplittableText(final String text,
            final String extraValues) {
        if (TextUtils.isEmpty(extraValues)) {
            return false;
        }
        return containsInArray(text, extraValues.split(SEPARATOR_FOR_COMMA_SPLITTABLE_TEXT));
    }

    public static String removeFromCommaSplittableTextIfExists(final String text,
            final String extraValues) {
        if (TextUtils.isEmpty(extraValues)) {
            return EMPTY_STRING;
        }
        final String[] elements = extraValues.split(SEPARATOR_FOR_COMMA_SPLITTABLE_TEXT);
        if (!containsInArray(text, elements)) {
            return extraValues;
        }
        final ArrayList<String> result = new ArrayList<>(elements.length - 1);
        for (final String element : elements) {
            if (!text.equals(element)) {
                result.add(element);
            }
        }
        return TextUtils.join(SEPARATOR_FOR_COMMA_SPLITTABLE_TEXT, result);
    }

    public static String capitalizeFirstCodePoint(final String s,
            final Locale locale) {
        if (s.length() <= 1) {
            return s.toUpperCase(getLocaleUsedForToTitleCase(locale));
        }
        // Please refer to the comment below in
        // {@link #capitalizeFirstAndDowncaseRest(String,Locale)} as this has the same shortcomings
        final int cutoff = s.offsetByCodePoints(0, 1);
        return s.substring(0, cutoff).toUpperCase(getLocaleUsedForToTitleCase(locale))
                + s.substring(cutoff);
    }

    private static final String LANGUAGE_GREEK = "el";

    private static Locale getLocaleUsedForToTitleCase(final Locale locale) {
        // In Greek locale {@link String#toUpperCase(Locale)} eliminates accents from its result.
        // In order to get accented upper case letter, {@link Locale#ROOT} should be used.
        if (LANGUAGE_GREEK.equals(locale.getLanguage())) {
            return Locale.ROOT;
        }
        return locale;
    }

}
