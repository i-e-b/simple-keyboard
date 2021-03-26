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

package rkr.simplekeyboard.inputmethod.keyboard.internal;

public class KeyboardParams {
    public int mId;

    /** Total height and width of the keyboard, including padding and keys */
    public int mOccupiedHeight;
    public int mOccupiedWidth;

    /** Base height and width of the keyboard used to calculate rows' or keys' heights and
     *  widths
     */
    public float mBaseHeight;
    public float mBaseWidth;

    public float mTopPadding;
    public float mBottomPadding;
    public float mLeftPadding;
    public float mRightPadding;

    public KeyVisualAttributes mKeyVisualAttributes;

    public float mDefaultRowHeight;
    public float mDefaultKeyPaddedWidth;
    public float mHorizontalGap;
    public float mVerticalGap;

    public int mMoreKeysTemplate;
    public int mMaxMoreKeysKeyboardColumn;

    public int mGridWidth;
    public int mGridHeight;

    public final KeyboardIconsSet mIconsSet = new KeyboardIconsSet();

    public boolean mAllowRedundantMoreKeys;

    public KeyboardParams() {}

}
