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

package rkr.simplekeyboard.inputmethod.latin.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

public final class ResourceUtils {
    private static final String TAG = ResourceUtils.class.getSimpleName();

    private ResourceUtils() {
        // This utility class is not publicly instantiable.
    }

    private static final HashMap<String, String> sDeviceOverrideValueMap = new HashMap<>();

    private static final String[] BUILD_KEYS_AND_VALUES = {
        "HARDWARE", Build.HARDWARE,
        "MODEL", Build.MODEL,
        "BRAND", Build.BRAND,
        "MANUFACTURER", Build.MANUFACTURER
    };
    private static final HashMap<String, String> sBuildKeyValues;
    private static final String sBuildKeyValuesDebugString;

    static {
        sBuildKeyValues = new HashMap<>();
        final ArrayList<String> keyValuePairs = new ArrayList<>();
        final int keyCount = BUILD_KEYS_AND_VALUES.length / 2;
        for (int i = 0; i < keyCount; i++) {
            final int index = i * 2;
            final String key = BUILD_KEYS_AND_VALUES[index];
            final String value = BUILD_KEYS_AND_VALUES[index + 1];
            sBuildKeyValues.put(key, value);
            keyValuePairs.add(key + '=' + value);
        }
        sBuildKeyValuesDebugString = "[" + TextUtils.join(" ", keyValuePairs) + "]";
    }

    public static String getDeviceOverrideValue(final Resources res, final int overrideResId,
            final String defaultValue) {
        final int orientation = res.getConfiguration().orientation;
        final String key = overrideResId + "-" + orientation;
        if (sDeviceOverrideValueMap.containsKey(key)) {
            return sDeviceOverrideValueMap.get(key);
        }

        final String[] overrideArray = res.getStringArray(overrideResId);
        final String overrideValue = findConstantForKeyValuePairs(sBuildKeyValues, overrideArray);
        // The overrideValue might be an empty string.
        if (overrideValue != null) {
            Log.i(TAG, "Find override value:"
                    + " resource="+ res.getResourceEntryName(overrideResId)
                    + " build=" + sBuildKeyValuesDebugString
                    + " override=" + overrideValue);
            sDeviceOverrideValueMap.put(key, overrideValue);
            return overrideValue;
        }

        sDeviceOverrideValueMap.put(key, defaultValue);
        return defaultValue;
    }

    @SuppressWarnings("serial")
    static class DeviceOverridePatternSyntaxError extends Exception {
        public DeviceOverridePatternSyntaxError(final String message, final String expression) {
            this(message, expression, null);
        }

        public DeviceOverridePatternSyntaxError(final String message, final String expression,
                final Throwable throwable) {
            super(message + ": " + expression, throwable);
        }
    }

    /**
     * Find the condition that fulfills specified key value pairs from an array of
     * "condition,constant", and return the corresponding string constant. A condition is
     * "pattern1[:pattern2...] (or an empty string for the default). A pattern is
     * "key=regexp_value" string. The condition matches only if all patterns of the condition
     * are true for the specified key value pairs.
     *
     * For example, "condition,constant" has the following format.
     *  - HARDWARE=mako,constantForNexus4
     *  - MODEL=Nexus 4:MANUFACTURER=LGE,constantForNexus4
     *  - ,defaultConstant
     *
     * @param keyValuePairs attributes to be used to look for a matched condition.
     * @param conditionConstantArray an array of "condition,constant" elements to be searched.
     * @return the constant part of the matched "condition,constant" element. Returns null if no
     * condition matches.
     */
    private static String findConstantForKeyValuePairs(final HashMap<String, String> keyValuePairs,
            final String[] conditionConstantArray) {
        if (conditionConstantArray == null || keyValuePairs == null) {
            return null;
        }
        String foundValue = null;
        for (final String conditionConstant : conditionConstantArray) {
            final int posComma = conditionConstant.indexOf(',');
            if (posComma < 0) {
                Log.w(TAG, "Array element has no comma: " + conditionConstant);
                continue;
            }
            final String condition = conditionConstant.substring(0, posComma);
            if (condition.isEmpty()) {
                Log.w(TAG, "Array element has no condition: " + conditionConstant);
                continue;
            }
            try {
                if (fulfillsCondition(keyValuePairs, condition)) {
                    // Take first match
                    if (foundValue == null) {
                        foundValue = conditionConstant.substring(posComma + 1);
                    }
                    // And continue walking through all conditions.
                }
            } catch (final DeviceOverridePatternSyntaxError e) {
                Log.w(TAG, "Syntax error, ignored", e);
            }
        }
        return foundValue;
    }

    private static boolean fulfillsCondition(final HashMap<String,String> keyValuePairs,
            final String condition) throws DeviceOverridePatternSyntaxError {
        final String[] patterns = condition.split(":");
        // Check all patterns in a condition are true
        boolean matchedAll = true;
        for (final String pattern : patterns) {
            final int posEqual = pattern.indexOf('=');
            if (posEqual < 0) {
                throw new DeviceOverridePatternSyntaxError("Pattern has no '='", condition);
            }
            final String key = pattern.substring(0, posEqual);
            final String value = keyValuePairs.get(key);
            if (value == null) {
                throw new DeviceOverridePatternSyntaxError("Unknown key", condition);
            }
            final String patternRegexpValue = pattern.substring(posEqual + 1);
            try {
                if (!value.matches(patternRegexpValue)) {
                    matchedAll = false;
                    // And continue walking through all patterns.
                }
            } catch (final PatternSyntaxException e) {
                throw new DeviceOverridePatternSyntaxError("Syntax error", condition, e);
            }
        }
        return matchedAll;
    }

    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color) {
            return true;
        }
        // See http://www.nbdtech.com/Blog/archive/2008/04/27/Calculating-the-Perceived-Brightness-of-a-Color.aspx
        boolean bright = false;
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1] * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
        if (brightness >= 210) {
            bright = true;
        }
        return bright;
    }
}
