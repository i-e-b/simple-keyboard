/*
 * Copyright (C) 2008 The Android Open Source Project
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardParams;
import rkr.simplekeyboard.inputmethod.latin.InputView;
import rkr.simplekeyboard.inputmethod.latin.LatinIME;
import rkr.simplekeyboard.inputmethod.latin.settings.SettingsValues;

import static android.content.res.Configuration.UI_MODE_NIGHT_NO;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

public final class KeyboardLoader {
    private View mMainKeyboardFrame;
    private MainKeyboardView mKeyboardView;
    private LatinIME mLatinIME;

    @SuppressLint("StaticFieldLeak")
    private static final KeyboardLoader sInstance = new KeyboardLoader();

    public static KeyboardLoader getInstance() {
        return sInstance;
    }

    private KeyboardLoader() {
        // Intentional empty constructor for singleton.
    }

    public static void init(final LatinIME latinIme) {
        sInstance.initInternal(latinIme);
    }

    private void initInternal(final LatinIME latinIme) {
        mLatinIME = latinIme;
    }

    public void updateKeyboardTheme(final int uiMode) {
        if ((uiMode & UI_MODE_NIGHT_YES) > 0){
            mKeyboardView.SetNightMode();
        } else if ((uiMode & UI_MODE_NIGHT_NO) > 0){
            mKeyboardView.SetDayMode();
        }
    }

    public void loadKeyboard()
    {
        mLatinIME.getMaxWidth();
        setKeyboard();
    }

    public void onHideWindow() {
        if (mKeyboardView != null) {
            mKeyboardView.onHideWindow();
        }
    }

    private void setKeyboard() {
        setMainKeyboardFrame();
        mKeyboardView.setKeyboard(KeyboardParams.Defaults());
    }

    public void resetKeyboard() {
        setKeyboard();
    }

    public boolean isImeSuppressedByHardwareKeyboard(final SettingsValues settingsValues) {
        return settingsValues.mHasHardwareKeyboard;
    }

    private void setMainKeyboardFrame() {
        // TODO: we fake this so the IME is not hidden in the debugger
        //final int visibility =  isImeSuppressedByHardwareKeyboard(settingsValues) ? View.GONE : View.VISIBLE;
        mKeyboardView.setVisibility(View.VISIBLE);
        mMainKeyboardFrame.setVisibility(View.VISIBLE);
    }

    public View getVisibleKeyboardView() {
        return mKeyboardView;
    }

    public MainKeyboardView getMainKeyboardView() {
        return mKeyboardView;
    }

    public void deallocateMemory() {
        if (mKeyboardView != null) {
            mKeyboardView.cancelAllOngoingEvents();
            mKeyboardView.deallocateMemory();
        }
    }

    public View onCreateInputView() {
        if (mKeyboardView != null) {
            mKeyboardView.closing();
        }

        Context ctx = new ContextWrapper(mLatinIME);

        InputView mCurrentInputView = (InputView) LayoutInflater.from(ctx).inflate(
                R.layout.input_view, null);
        mMainKeyboardFrame = mCurrentInputView.findViewById(R.id.main_keyboard_frame);

        mKeyboardView = mCurrentInputView.findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboardActionListener(mLatinIME);
        return mCurrentInputView;
    }
}
