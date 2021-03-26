/*
 * Copyright (C) 2010 The Android Open Source Project
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

package rkr.simplekeyboard.inputmethod.keyboard;

import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Locale;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeySpecParser;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyVisualAttributes;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardParams;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardRow;
import rkr.simplekeyboard.inputmethod.keyboard.internal.MoreKeySpec;
import rkr.simplekeyboard.inputmethod.latin.common.Constants;
import rkr.simplekeyboard.inputmethod.latin.common.StringUtils;

import static rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardIconsSet.ICON_UNDEFINED;
import static rkr.simplekeyboard.inputmethod.latin.common.Constants.CODE_SHIFT;
import static rkr.simplekeyboard.inputmethod.latin.common.Constants.CODE_SWITCH_ALPHA_SYMBOL;
import static rkr.simplekeyboard.inputmethod.latin.common.Constants.CODE_UNSPECIFIED;

/**
 * Class for describing the position and characteristics of a single key in the keyboard.
 */
public class Key implements Comparable<Key> {
    /**
     * The key code (unicode or custom code) that this key generates.
     */
    private final int mCode = 0;

    /** Label to display */
    private final String mLabel;
    private static final int LABEL_FLAGS_PRESERVE_CASE = 0x10000;
    private static final int LABEL_FLAGS_FROM_CUSTOM_ACTION_LABEL = 0x40000;

    /** Icon to display instead of a label. Icon takes precedence over a label */
    private final int mIconId;

    /** Width of the key, excluding the padding */
    private final int mWidth;
    /** Height of the key, excluding the padding */
    private final int mHeight;
    /** Exact theoretical width of the key, excluding the padding */
    private final float mDefinedWidth;
    /** Exact theoretical height of the key, excluding the padding */
    private final float mDefinedHeight;
    /** X coordinate of the top-left corner of the key in the keyboard layout, excluding the
     *  padding. */
    private final int mX;
    /** Y coordinate of the top-left corner of the key in the keyboard layout, excluding the
     *  padding. */
    private final int mY;
    /** Hit bounding box of the key */
    private final Rect mHitbox = new Rect();

    // If this flag is specified, more keys keyboard should have the specified number of columns.
    // Otherwise more keys keyboard should have less than or equal to the specified maximum number
    // of columns.
    private static final int MORE_KEYS_FLAGS_FIXED_COLUMN = 0x00000100;
    // If this flag is specified, the order of more keys is determined by the order in the more
    // keys' specification. Otherwise the order of more keys is automatically determined.
    private static final int MORE_KEYS_FLAGS_FIXED_ORDER = 0x00000200;
    private static final int MORE_KEYS_MODE_MAX_COLUMN_WITH_AUTO_ORDER = 0;
    private static final int MORE_KEYS_MODE_FIXED_COLUMN_WITH_AUTO_ORDER =
            MORE_KEYS_FLAGS_FIXED_COLUMN;
    private static final int MORE_KEYS_MODE_FIXED_COLUMN_WITH_FIXED_ORDER =
            (MORE_KEYS_FLAGS_FIXED_COLUMN | MORE_KEYS_FLAGS_FIXED_ORDER);
    private static final int MORE_KEYS_FLAGS_HAS_LABELS = 0x40000000;
    private static final int MORE_KEYS_FLAGS_NO_PANEL_AUTO_MORE_KEY = 0x10000000;
    // TODO: Rename these specifiers to !autoOrder! and !fixedOrder! respectively.
    private static final String MORE_KEYS_AUTO_COLUMN_ORDER = "!autoColumnOrder!";
    private static final String MORE_KEYS_FIXED_COLUMN_ORDER = "!fixedColumnOrder!";
    private static final String MORE_KEYS_HAS_LABELS = "!hasLabels!";
    private static final String MORE_KEYS_NO_PANEL_AUTO_MORE_KEY = "!noPanelAutoMoreKey!";

    /** Background type that represents different key background visual than normal one. */
    public static final int BACKGROUND_TYPE_NORMAL = 1;

    private static final int ACTION_FLAGS_NO_KEY_PREVIEW = 0x02;
    private static final int ACTION_FLAGS_ALT_CODE_WHILE_TYPING = 0x04;
    private static final int ACTION_FLAGS_ENABLE_LONG_PRESS = 0x08;

    private final KeyVisualAttributes mKeyVisualAttributes;
    private final OptionalAttributes mOptionalAttributes;

    private static final class OptionalAttributes {
        /** Text to output when pressed. This can be multiple characters, like ".com" */
        public final String mOutputText;
        public final int mAltCode;
        /** Icon for disabled state */
        public final int mDisabledIconId;

        private OptionalAttributes(final String outputText, final int altCode,
                final int disabledIconId) {
            mOutputText = outputText;
            mAltCode = altCode;
            mDisabledIconId = disabledIconId;
        }

        public static OptionalAttributes newInstance(final String outputText, final int altCode,
                final int disabledIconId) {
            if (outputText == null && altCode == CODE_UNSPECIFIED
                    && disabledIconId == ICON_UNDEFINED) {
                return null;
            }
            return new OptionalAttributes(outputText, altCode, disabledIconId);
        }
    }

    private final int mHashCode;

    /** The current pressed state of this key */
    private boolean mPressed;
    /** Key is enabled and responds on press */
    private boolean mEnabled = true;

    /**
     * Constructor for a key on <code>MoreKeyKeyboard</code>.
     */
    public Key(final String label, final int iconId, final int code, final String outputText,
               final String hintLabel, final int labelFlags, final int backgroundType,
               final float x, final float y, final float width, final float height,
               final float leftPadding, final float rightPadding, final float topPadding,
               final float bottomPadding) {
        mHitbox.set(Math.round(x - leftPadding), Math.round(y - topPadding),
                Math.round(x + width + rightPadding), Math.round(y + height + bottomPadding));
        mX = Math.round(x);
        mY = Math.round(y);
        mWidth = Math.round(x + width) - mX;
        mHeight = Math.round(y + height) - mY;
        mDefinedWidth = width;
        mDefinedHeight = height;
        mLabel = label;
        mOptionalAttributes = OptionalAttributes.newInstance(outputText, CODE_UNSPECIFIED,
                ICON_UNDEFINED);
        mEnabled = (code != CODE_UNSPECIFIED);
        mIconId = iconId;
        mKeyVisualAttributes = null;

        mHashCode = computeHashCode(this);
    }

    /**
     * Create a key with the given top-left coordinate and extract its attributes from a key
     * specification string, Key attribute array, key style, and etc.
     *
     * @param keySpec the key specification.
     * @param keyAttr the Key XML attributes array.
     * @param params the keyboard building parameters.
     * @param row the row that this key belongs to. row's x-coordinate will be the right edge of
     *        this key.
     */
    public Key(final String keySpec, final TypedArray keyAttr, final KeyboardParams params, final KeyboardRow row) {
        // Update the row to work with the new key
        row.setCurrentKey(keyAttr, isSpacer());

        mDefinedWidth = row.getKeyWidth();
        mDefinedHeight = row.getKeyHeight();

        final float keyLeft = row.getKeyX();
        final float keyTop = row.getKeyY();
        final float keyRight = keyLeft + mDefinedWidth;
        final float keyBottom = keyTop + mDefinedHeight;

        final float leftPadding = row.getKeyLeftPadding();
        final float topPadding = row.getKeyTopPadding();
        final float rightPadding = row.getKeyRightPadding();
        final float bottomPadding = row.getKeyBottomPadding();

        mHitbox.set(Math.round(keyLeft - leftPadding), Math.round(keyTop - topPadding),
                Math.round(keyRight + rightPadding), Math.round(keyBottom + bottomPadding));
        mX = Math.round(keyLeft);
        mY = Math.round(keyTop);
        mWidth = Math.round(keyRight) - mX;
        mHeight = Math.round(keyBottom) - mY;


        final boolean needsToUpcase = false;
        final Locale localeForUpcasing = params.mId.getLocale();

        mIconId = 0;//KeySpecParser.getIconId(keySpec);

        final int code = 0;//KeySpecParser.getCode(keySpec);
        if ((LABEL_FLAGS_FROM_CUSTOM_ACTION_LABEL) != 0) {
            mLabel = params.mId.mCustomActionLabel;
        } else if (code >= Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // This is a workaround to have a key that has a supplementary code point in its label.
            // Because we can put a string in resource neither as a XML entity of a supplementary
            // code point nor as a surrogate pair.
            mLabel = new StringBuilder().appendCodePoint(code).toString();
        } else {
            final String label = "x";//KeySpecParser.getLabel(keySpec);
            mLabel = needsToUpcase
                    ? StringUtils.toTitleCaseOfKeyLabel(label, localeForUpcasing)
                    : label;
        }

        mOptionalAttributes = OptionalAttributes.newInstance("", 0, 0);
        mKeyVisualAttributes = KeyVisualAttributes.newInstance(keyAttr);
        mHashCode = computeHashCode(this);
    }

    private Key(final Key key) {
        // Final attributes.
        mLabel = key.mLabel;
        mIconId = key.mIconId;
        mWidth = key.mWidth;
        mHeight = key.mHeight;
        mDefinedWidth = key.mDefinedWidth;
        mDefinedHeight = key.mDefinedHeight;
        mX = key.mX;
        mY = key.mY;
        mHitbox.set(key.mHitbox);
        mKeyVisualAttributes = key.mKeyVisualAttributes;
        mOptionalAttributes = key.mOptionalAttributes;
        mHashCode = key.mHashCode;
        // Key state.
        mPressed = key.mPressed;
        mEnabled = key.mEnabled;
    }


    private static boolean needsToUpcase(final int labelFlags, final int keyboardElementId) {
        if ((labelFlags & LABEL_FLAGS_PRESERVE_CASE) != 0) return false;
        switch (keyboardElementId) {
        case KeyboardId.ELEMENT_ALPHABET_MANUAL_SHIFTED:
        case KeyboardId.ELEMENT_ALPHABET_AUTOMATIC_SHIFTED:
        case KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCKED:
            return true;
        default:
            return false;
        }
    }

    private static int computeHashCode(final Key key) {
        return Arrays.hashCode(new Object[] {
                key.mX,
                key.mY,
                key.mWidth,
                key.mHeight,
                key.mCode,
                key.mLabel,
                key.mIconId,
                key.getOutputText(),
                // Key can be distinguishable without the following members.
                // key.mOptionalAttributes.mAltCode,
                // key.mOptionalAttributes.mDisabledIconId,
                // key.mOptionalAttributes.mPreviewIconId,
                // key.mMaxMoreKeysColumn,
                // key.mDefinedHeight,
                // key.mDefinedWidth,
        });
    }

    private boolean equalsInternal(final Key o) {
        if (this == o) return true;
        return o.mX == mX
                && o.mY == mY
                && o.mWidth == mWidth
                && o.mHeight == mHeight
                && o.mCode == mCode
                && TextUtils.equals(o.mLabel, mLabel)
                && o.mIconId == mIconId
                && TextUtils.equals(o.getOutputText(), getOutputText());
    }

    @Override
    public int compareTo(Key o) {
        if (equalsInternal(o)) return 0;
        if (mHashCode > o.mHashCode) return 1;
        return -1;
    }

    @Override
    public int hashCode() {
        return mHashCode;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Key && equalsInternal((Key)o);
    }

    @Override
    public String toString() {
        return toShortString() + " " + getX() + "," + getY() + " " + getWidth() + "x" + getHeight();
    }

    public String toShortString() {
        final int code = getCode();
        if (code == Constants.CODE_OUTPUT_TEXT) {
            return getOutputText();
        }
        return Constants.printableCode(code);
    }

    public int getCode() {
        return mCode;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setHitboxRightEdge(final int right) {
        mHitbox.right = right;
    }

    public final boolean isSpacer() {
        return this instanceof Spacer;
    }

    public final boolean isShift() {
        return mCode == CODE_SHIFT;
    }

    public final boolean isModifier() {
        return mCode == CODE_SHIFT || mCode == CODE_SWITCH_ALPHA_SYMBOL;
    }

    public final boolean altCodeWhileTyping() {
        return false;
    }


    public final String getOutputText() {
        final OptionalAttributes attrs = mOptionalAttributes;
        return (attrs != null) ? attrs.mOutputText : null;
    }


    /**
     * Gets the width of the key in pixels, excluding the padding.
     * @return The width of the key in pixels, excluding the padding.
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Gets the height of the key in pixels, excluding the padding.
     * @return The height of the key in pixels, excluding the padding.
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Gets the theoretical width of the key in pixels, excluding the padding. This is the exact
     * width that the key was defined to be, but this will likely differ from the actual drawn width
     * because the normal (drawn/functional) width was determined by rounding the left and right
     * edge to fit evenly in a pixel.
     * @return The defined width of the key in pixels, excluding the padding.
     */
    public float getDefinedWidth() {
        return mDefinedWidth;
    }

    /**
     * Gets the theoretical height of the key in pixels, excluding the padding. This is the exact
     * height that the key was defined to be, but this will likely differ from the actual drawn
     * height because the normal (drawn/functional) width was determined by rounding the top and
     * bottom edge to fit evenly in a pixel.
     * @return The defined width of the key in pixels, excluding the padding.
     */
    public float getDefinedHeight() {
        return mDefinedHeight;
    }

    /**
     * Gets the x-coordinate of the top-left corner of the key in pixels, excluding the padding.
     * @return The x-coordinate of the top-left corner of the key in pixels, excluding the padding.
     */
    public int getX() {
        return mX;
    }

    /**
     * Gets the y-coordinate of the top-left corner of the key in pixels, excluding the padding.
     * @return The y-coordinate of the top-left corner of the key in pixels, excluding the padding.
     */
    public int getY() {
        return mY;
    }

    /**
     * Gets the amount of padding for the hitbox above the key's visible position.
     * @return The hitbox padding above the key.
     */
    public int getTopPadding() {
        return mY - mHitbox.top;
    }

    /**
     * Gets the amount of padding for the hitbox below the key's visible position.
     * @return The hitbox padding below the key.
     */
    public int getBottomPadding() {
        return mHitbox.bottom - mY - mHeight;
    }

    /**
     * Gets the amount of padding for the hitbox to the left of the key's visible position.
     * @return The hitbox padding to the left of the key.
     */
    public int getLeftPadding() {
        return mX - mHitbox.left;
    }

    /**
     * Gets the amount of padding for the hitbox to the right of the key's visible position.
     * @return The hitbox padding to the right of the key.
     */
    public int getRightPadding() {
        return mHitbox.right - mX - mWidth;
    }


    public final boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(final boolean enabled) {
        mEnabled = enabled;
    }

    public static class Spacer extends Key {
        public Spacer(final TypedArray keyAttr, final KeyboardParams params, final KeyboardRow row) {
            super(null /* keySpec */, keyAttr,  params, row);
        }
    }
}
