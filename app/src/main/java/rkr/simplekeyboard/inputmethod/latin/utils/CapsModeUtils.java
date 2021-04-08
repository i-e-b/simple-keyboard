/*
 * Copyright (C) 2013 The Android Open Source Project
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

package rkr.simplekeyboard.inputmethod.latin.utils;

import android.text.TextUtils;

import java.util.ArrayList;

import rkr.simplekeyboard.inputmethod.latin.common.Constants;

public final class CapsModeUtils {
    private CapsModeUtils() {
        // This utility class is not publicly instantiable.
    }

    /**
     * Helper method to find out if a code point is starting punctuation.
     *
     * This include the Unicode START_PUNCTUATION category, but also some other symbols that are
     * starting, like the inverted question mark or the double quote.
     *
     * @param codePoint the code point
     * @return true if it's starting punctuation, false otherwise.
     */
    private static boolean isStartPunctuation(final int codePoint) {
        return (codePoint == Constants.CODE_DOUBLE_QUOTE || codePoint == Constants.CODE_SINGLE_QUOTE
                || codePoint == Constants.CODE_INVERTED_QUESTION_MARK
                || codePoint == Constants.CODE_INVERTED_EXCLAMATION_MARK
                || Character.getType(codePoint) == Character.START_PUNCTUATION);
    }

    /**
     * Convert capitalize mode flags into human readable text.
     *
     * @param capsFlags The modes flags to be converted. It may be any combination of
     * {@link TextUtils#CAP_MODE_CHARACTERS}, {@link TextUtils#CAP_MODE_WORDS}, and
     * {@link TextUtils#CAP_MODE_SENTENCES}.
     * @return the text that describe the <code>capsMode</code>.
     */
    public static String flagsToString(final int capsFlags) {
        final int capsFlagsMask = TextUtils.CAP_MODE_CHARACTERS | TextUtils.CAP_MODE_WORDS
                | TextUtils.CAP_MODE_SENTENCES;
        if ((capsFlags & ~capsFlagsMask) != 0) {
            return "unknown<0x" + Integer.toHexString(capsFlags) + ">";
        }
        final ArrayList<String> builder = new ArrayList<>();
        if ((capsFlags & android.text.TextUtils.CAP_MODE_CHARACTERS) != 0) {
            builder.add("characters");
        }
        if ((capsFlags & android.text.TextUtils.CAP_MODE_WORDS) != 0) {
            builder.add("words");
        }
        if ((capsFlags & android.text.TextUtils.CAP_MODE_SENTENCES) != 0) {
            builder.add("sentences");
        }
        return builder.isEmpty() ? "none" : TextUtils.join("|", builder);
    }
}
