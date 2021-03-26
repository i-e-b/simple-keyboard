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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Locale;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.keyboard.KeyboardTheme;
import rkr.simplekeyboard.inputmethod.latin.common.StringUtils;
import rkr.simplekeyboard.inputmethod.latin.utils.ResourceUtils;
import rkr.simplekeyboard.inputmethod.latin.utils.XmlParseUtils;

public class KeyboardDefaultSettings<KP extends KeyboardParams> {
    protected final KP mParams;
    protected final Context mContext;
    protected final Resources mResources;

    public KeyboardDefaultSettings(final Context context, final KP params) {
        mContext = context;
        final Resources res = context.getResources();
        mResources = res;

        mParams = params;

        params.mGridWidth = res.getInteger(R.integer.config_keyboard_grid_width);
        params.mGridHeight = res.getInteger(R.integer.config_keyboard_grid_height);
    }

    public static KeyboardParams Defaults() {
        KeyboardParams x = new KeyboardParams();
        x.mBaseHeight = 680;
        x.mBaseWidth = 1080;
        x.mBottomPadding = 15.5f;
        x.mHorizontalGap = 19;
        x.mId = 0;
        x.mLeftPadding = 9.5f;
        x.mOccupiedHeight = 667;
        x.mOccupiedWidth = 1080;
        x.mRightPadding = 9.5f;
        x.mTopPadding = 15.5f;
        x.mVerticalGap = 46;
        return x;
    }

}
