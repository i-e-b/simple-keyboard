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

import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import rkr.simplekeyboard.inputmethod.keyboard.Key;
import rkr.simplekeyboard.inputmethod.keyboard.KeyboardId;
import rkr.simplekeyboard.inputmethod.latin.common.Constants;

public class KeyboardParams {
    public KeyboardId mId;

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

    // Keys are sorted from top-left to bottom-right order.
    public final SortedSet<Key> mSortedKeys = new TreeSet<>(ROW_COLUMN_COMPARATOR);
    public final ArrayList<Key> mShiftKeys = new ArrayList<>();
    public final ArrayList<Key> mAltCodeKeysWhileTyping = new ArrayList<>();
    public final KeyboardIconsSet mIconsSet = new KeyboardIconsSet();
    public final KeyboardTextsSet mTextsSet = new KeyboardTextsSet();
    public final KeyStylesSet mKeyStyles = new KeyStylesSet(mTextsSet);

    private final UniqueKeysCache mUniqueKeysCache;
    public boolean mAllowRedundantMoreKeys;

    public int mMostCommonKeyHeight = 0;
    public int mMostCommonKeyWidth = 0;

    // Comparator to sort {@link Key}s from top-left to bottom-right order.
    private static final Comparator<Key> ROW_COLUMN_COMPARATOR = new Comparator<Key>() {
        @Override
        public int compare(final Key lhs, final Key rhs) {
            if (lhs.getY() < rhs.getY()) return -1;
            if (lhs.getY() > rhs.getY()) return 1;
            return Integer.compare(lhs.getX(), rhs.getX());
        }
    };

    public KeyboardParams(final UniqueKeysCache keysCache) {
        mUniqueKeysCache = keysCache;
    }

}
