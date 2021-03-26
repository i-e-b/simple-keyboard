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
import android.content.res.Resources;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.event.Event;
import rkr.simplekeyboard.inputmethod.keyboard.KeyboardLayoutSet.KeyboardLayoutSetException;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardParams;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardTextsSet;
import rkr.simplekeyboard.inputmethod.latin.InputView;
import rkr.simplekeyboard.inputmethod.latin.LatinIME;
import rkr.simplekeyboard.inputmethod.latin.RichInputMethodManager;
import rkr.simplekeyboard.inputmethod.latin.settings.Settings;
import rkr.simplekeyboard.inputmethod.latin.settings.SettingsValues;
import rkr.simplekeyboard.inputmethod.latin.utils.ResourceUtils;

public final class KeyboardLoader {
    private static final String TAG = KeyboardLoader.class.getSimpleName();

    private int mCurrentUiMode;
    private View mMainKeyboardFrame;
    private MainKeyboardView mKeyboardView;
    private LatinIME mLatinIME;
    private RichInputMethodManager mRichImm;

    private KeyboardLayoutSet mKeyboardLayoutSet;
    // TODO: The following {@link KeyboardTextsSet} should be in {@link KeyboardLayoutSet}.
    private final KeyboardTextsSet mKeyboardTextsSet = new KeyboardTextsSet();

    private KeyboardTheme mKeyboardTheme;
    private Context mThemeContext;

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
        mRichImm = RichInputMethodManager.getInstance();
    }

    public void updateKeyboardTheme(final int uiMode) {
        final boolean themeUpdated = updateKeyboardThemeAndContextThemeWrapper(
                mLatinIME, KeyboardTheme.getKeyboardTheme(mLatinIME), uiMode);
        if (themeUpdated && mKeyboardView != null) {
            mLatinIME.setInputView(onCreateInputView(uiMode));
        }
    }

    private boolean updateKeyboardThemeAndContextThemeWrapper(final Context context,
            final KeyboardTheme keyboardTheme, final int uiMode) {
        if (mThemeContext == null || !keyboardTheme.equals(mKeyboardTheme) || mCurrentUiMode != uiMode) {
            mKeyboardTheme = keyboardTheme;
            mCurrentUiMode = uiMode;
            mThemeContext = new ContextThemeWrapper(context, keyboardTheme.mStyleId);
            KeyboardLayoutSet.onKeyboardThemeChanged();
            return true;
        }
        return false;
    }

    public void loadKeyboard(final EditorInfo editorInfo, final SettingsValues settingsValues,
            final int currentAutoCapsState, final int currentRecapitalizeState)

    {
        final KeyboardLayoutSet.Builder builder = new KeyboardLayoutSet.Builder(mThemeContext, editorInfo);
        final Resources res = mThemeContext.getResources();

        final int keyboardWidth = mLatinIME.getMaxWidth();
        final int keyboardHeight = ResourceUtils.getKeyboardHeight(res, settingsValues);
        builder.setKeyboardGeometry(keyboardWidth, keyboardHeight);

        builder.setKeyboardTheme(mKeyboardTheme.mThemeId);
        builder.setSubtype(mRichImm.getCurrentSubtype());

        mKeyboardLayoutSet = builder.build();
        try {
            // TODO: pull this up from the switcher
            setKeyboard(KeyboardId.ELEMENT_ALPHABET, KeyboardSwitchState.OTHER);
            mKeyboardTextsSet.setLocale(mRichImm.getCurrentSubtypeLocale(), mThemeContext);
        } catch (KeyboardLayoutSetException e) {
            Log.w(TAG, "loading keyboard failed: " + e.mKeyboardId, e.getCause());
        }
    }

    public void onHideWindow() {
        if (mKeyboardView != null) {
            mKeyboardView.onHideWindow();
        }
    }

    private void setKeyboard(
            final int keyboardId,
            final KeyboardSwitchState toggleState) {

        final SettingsValues currentSettingsValues = Settings.getInstance().getCurrent();
        setMainKeyboardFrame(currentSettingsValues, toggleState);

        final KeyboardParams newKeyboard = mKeyboardLayoutSet.getKeyboard(keyboardId);
        mKeyboardView.setKeyboard(newKeyboard);
    }

    public void resetKeyboard() {
        // TODO: pull this up from the switcher
        setKeyboard(KeyboardId.ELEMENT_ALPHABET, KeyboardSwitchState.OTHER);
    }

    public boolean isImeSuppressedByHardwareKeyboard(
            final SettingsValues settingsValues,
            final KeyboardSwitchState toggleState) {
        return settingsValues.mHasHardwareKeyboard && toggleState == KeyboardSwitchState.HIDDEN;
    }

    private void setMainKeyboardFrame(
            final SettingsValues settingsValues,
            final KeyboardSwitchState toggleState) {
        final int visibility =  isImeSuppressedByHardwareKeyboard(settingsValues, toggleState)
                ? View.GONE : View.VISIBLE;
        mKeyboardView.setVisibility(visibility);
        // The visibility of {@link #mKeyboardView} must be aligned with {@link #MainKeyboardFrame}.
        // @see #getVisibleKeyboardView() and
        // @see LatinIME#onComputeInset(android.inputmethodservice.InputMethodService.Insets)
        mMainKeyboardFrame.setVisibility(visibility);
    }

    public enum KeyboardSwitchState {
        HIDDEN(-1),
        SYMBOLS_SHIFTED(KeyboardId.ELEMENT_SYMBOLS_SHIFTED),
        OTHER(-1);

        final int mKeyboardId;

        KeyboardSwitchState(int keyboardId) {
            mKeyboardId = keyboardId;
        }
    }

    public KeyboardSwitchState getKeyboardSwitchState() {
        boolean hidden = mKeyboardLayoutSet == null
                || mKeyboardView == null
                || !mKeyboardView.isShown();
        if (hidden) {
            return KeyboardSwitchState.HIDDEN;
        } else if (isShowingKeyboardId(KeyboardId.ELEMENT_SYMBOLS_SHIFTED)) {
            return KeyboardSwitchState.SYMBOLS_SHIFTED;
        }
        return KeyboardSwitchState.OTHER;
    }

    public boolean isShowingKeyboardId(int... keyboardIds) {
        if (mKeyboardView == null || !mKeyboardView.isShown()) {
            return false;
        }
        int activeKeyboardId = mKeyboardView.getKeyboard().mId.mElementId;
        for (int keyboardId : keyboardIds) {
            if (activeKeyboardId == keyboardId) {
                return true;
            }
        }
        return false;
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

    public View onCreateInputView(final int uiMode) {
        if (mKeyboardView != null) {
            mKeyboardView.closing();
        }

        updateKeyboardThemeAndContextThemeWrapper(
                mLatinIME, KeyboardTheme.getKeyboardTheme(mLatinIME /* context */), uiMode);
        InputView mCurrentInputView = (InputView) LayoutInflater.from(mThemeContext).inflate(
                R.layout.input_view, null);
        mMainKeyboardFrame = mCurrentInputView.findViewById(R.id.main_keyboard_frame);

        mKeyboardView = mCurrentInputView.findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboardActionListener(mLatinIME);
        return mCurrentInputView;
    }
}
