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

import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyVisualAttributes;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardParams;

/**
 * Loads an XML description of a keyboard and stores the attributes of the keys. A keyboard
 * consists of rows of keys.
 * <p>The layout file for a keyboard contains XML that looks like the following snippet:</p>
 * <pre>
 * &lt;Keyboard
 *         latin:keyWidth="10%p"
 *         latin:rowHeight="50px"
 *         latin:horizontalGap="2%p"
 *         latin:verticalGap="2%p" &gt;
 *     &lt;Row latin:keyWidth="10%p" &gt;
 *         &lt;Key latin:keyLabel="A" /&gt;
 *         ...
 *     &lt;/Row&gt;
 *     ...
 * &lt;/Keyboard&gt;
 * </pre>
 */
public class Keyboard {
    public final KeyboardId mId;

    /** Total height of the keyboard, including the padding and keys */
    public final int mOccupiedHeight;
    /** Total width of the keyboard, including the padding and keys */
    public final int mOccupiedWidth;

    /** The padding below the keyboard */
    public final float mBottomPadding;
    /** Default gap between rows */
    public final float mVerticalGap;
    /** Default gap between columns */
    public final float mHorizontalGap;

    /** Per keyboard key visual parameters */
    public final KeyVisualAttributes mKeyVisualAttributes;

    public final int mMostCommonKeyHeight;
    public final int mMostCommonKeyWidth;

    public Keyboard(final KeyboardParams params) {
        mId = params.mId;
        mOccupiedHeight = params.mOccupiedHeight;
        mOccupiedWidth = params.mOccupiedWidth;
        mMostCommonKeyHeight = params.mMostCommonKeyHeight;
        mMostCommonKeyWidth = params.mMostCommonKeyWidth;
        mKeyVisualAttributes = params.mKeyVisualAttributes;
        mBottomPadding = params.mBottomPadding;
        mVerticalGap = params.mVerticalGap;
        mHorizontalGap = params.mHorizontalGap;
    }
}
