/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.util.Log;

import rkr.simplekeyboard.inputmethod.event.Event;
import rkr.simplekeyboard.inputmethod.latin.common.Constants;
import rkr.simplekeyboard.inputmethod.latin.utils.CapsModeUtils;
import rkr.simplekeyboard.inputmethod.latin.utils.RecapitalizeStatus;

/**
 * Keyboard state machine.
 *
 * This class contains all keyboard state transition logic.
 *
 * The actions are {@link SwitchActions}'s methods.
 */
public final class KeyboardState {
    private static final String TAG = KeyboardState.class.getSimpleName();
    private static final boolean DEBUG_EVENT = false;
    private static final boolean DEBUG_INTERNAL_ACTION = false;

    public interface SwitchActions {
        boolean DEBUG_ACTION = false;

        void initialiseKeyboard();

        /**
         * Request to call back {@link KeyboardState#onUpdateShiftState(int, int)}.
         */
        void requestUpdatingShiftState(final int autoCapsFlags, final int recapitalizeMode);

        boolean DEBUG_TIMER_ACTION = false;

        void startDoubleTapShiftKeyTimer();
        boolean isInDoubleTapShiftKeyTimeout();
        void cancelDoubleTapShiftKeyTimer();
    }

    private final SwitchActions mSwitchActions;

    private ShiftKeyState mShiftKeyState = new ShiftKeyState("Shift");
    private ModifierKeyState mSymbolKeyState = new ModifierKeyState("Symbol");

    // TODO: Merge {@link #mSwitchState}, {@link #mIsAlphabetMode}, {@link #mAlphabetShiftState},
    // {@link #mIsSymbolShifted}, {@link #mPrevMainKeyboardWasShiftLocked}, and
    // {@link #mPrevSymbolsKeyboardWasShifted} into single state variable.
    private static final int SWITCH_STATE_ALPHA = 0;
    private static final int SWITCH_STATE_SYMBOL_BEGIN = 1;
    private static final int SWITCH_STATE_SYMBOL = 2;
    private static final int SWITCH_STATE_MOMENTARY_ALPHA_AND_SYMBOL = 3;
    private static final int SWITCH_STATE_MOMENTARY_SYMBOL_AND_MORE = 4;
    private int mSwitchState = SWITCH_STATE_ALPHA;

    private boolean mIsAlphabetMode;
    private boolean mIsSymbolShifted;
    private boolean mPrevMainKeyboardWasShiftLocked;
    private boolean mPrevSymbolsKeyboardWasShifted;
    private int mRecapitalizeMode;

    // For handling double tap.
    private boolean mIsInAlphabetUnshiftedFromShifted;
    private boolean mIsInDoubleTapShiftKey;

    private final SavedKeyboardState mSavedKeyboardState = new SavedKeyboardState();

    static final class SavedKeyboardState {
        public boolean mIsValid;
        public boolean mIsAlphabetMode;
        public boolean mIsAlphabetShiftLocked;
        public int mShiftMode;

        @Override
        public String toString() {
            if (!mIsValid) {
                return "INVALID";
            }
            if (mIsAlphabetMode) {
                return mIsAlphabetShiftLocked ? "ALPHABET_SHIFT_LOCKED"
                        : "ALPHABET_" + shiftModeToString(mShiftMode);
            }
            return "SYMBOLS_" + shiftModeToString(mShiftMode);
        }
    }

    public KeyboardState(final SwitchActions switchActions) {
        mSwitchActions = switchActions;
        mRecapitalizeMode = RecapitalizeStatus.NOT_A_RECAPITALIZE_MODE;
    }

    public void onLoadKeyboard() {
        mSwitchActions.initialiseKeyboard();
    }

    // Constants for {@link SavedKeyboardState#mShiftMode} and {@link #setShifted(int)}.
    private static final int UNSHIFT = 0;
    private static final int MANUAL_SHIFT = 1;
    private static final int AUTOMATIC_SHIFT = 2;
    private static final int SHIFT_LOCK_SHIFTED = 3;

    public void onPressKey(final int code, final boolean isSinglePointer, final int autoCapsFlags,
            final int recapitalizeMode) {
        if (DEBUG_EVENT) {
            Log.d(TAG, "onPressKey: code=" + Constants.printableCode(code)
                    + " single=" + isSinglePointer
                    + " " + stateToString(autoCapsFlags, recapitalizeMode));
        }
        if (code != Constants.CODE_SHIFT) {
            // Because the double tap shift key timer is to detect two consecutive shift key press,
            // it should be canceled when a non-shift key is pressed.
            mSwitchActions.cancelDoubleTapShiftKeyTimer();
        }
        if (code == Constants.CODE_SHIFT) {
        } else if (code == Constants.CODE_CAPSLOCK) {
            // Nothing to do here. See {@link #onReleaseKey(int,boolean)}.
        } else if (code == Constants.CODE_SWITCH_ALPHA_SYMBOL) {
            onPressSymbol(autoCapsFlags, recapitalizeMode);
        } else {
            mShiftKeyState.onOtherKeyPressed();
            mSymbolKeyState.onOtherKeyPressed();
        }
    }

    public void onReleaseKey(final int code, final boolean withSliding, final int autoCapsFlags,
            final int recapitalizeMode) {
        if (DEBUG_EVENT) {
            Log.d(TAG, "onReleaseKey: code=" + Constants.printableCode(code)
                    + " sliding=" + withSliding
                    + " " + stateToString(autoCapsFlags, recapitalizeMode));
        }
    }

    private void onPressSymbol(final int autoCapsFlags,
            final int recapitalizeMode) {
        mSymbolKeyState.onPress();
        mSwitchState = SWITCH_STATE_MOMENTARY_ALPHA_AND_SYMBOL;
    }

    private void onReleaseSymbol(final boolean withSliding, final int autoCapsFlags,
            final int recapitalizeMode) {
        if (mSymbolKeyState.isChording()) {
            // Switch back to the previous keyboard mode if the user chords the mode change key and
            // another key, then releases the mode change key.
        } else if (!withSliding) {
            // If the mode change key is being released without sliding, we should forget the
            // previous symbols keyboard shift state and simply switch back to symbols layout
            // (never symbols shifted) next time the mode gets changed to symbols layout.
            mPrevSymbolsKeyboardWasShifted = false;
        }
        mSymbolKeyState.onRelease();
    }

    public void onUpdateShiftState(final int autoCapsFlags, final int recapitalizeMode) {
        if (DEBUG_EVENT) {
            Log.d(TAG, "onUpdateShiftState: " + stateToString(autoCapsFlags, recapitalizeMode));
        }
        mRecapitalizeMode = recapitalizeMode;
    }

    // TODO: Remove this method. Come up with a more comprehensive way to reset the keyboard layout
    // when a keyboard layout set doesn't get reloaded in LatinIME.onStartInputViewInternal().
    public void onResetKeyboardStateToAlphabet(final int autoCapsFlags,
            final int recapitalizeMode) {
        if (DEBUG_EVENT) {
            Log.d(TAG, "onResetKeyboardStateToAlphabet: "
                    + stateToString(autoCapsFlags, recapitalizeMode));
        }
    }

    public void onEvent(final Event event, final int autoCapsFlags, final int recapitalizeMode) {
        final int code = event.isFunctionalKeyEvent() ? event.mKeyCode : event.mCodePoint;
        if (DEBUG_EVENT) {
            Log.d(TAG, "onEvent: code=" + Constants.printableCode(code)
                    + " " + stateToString(autoCapsFlags, recapitalizeMode));
        }

        switch (mSwitchState) {
        case SWITCH_STATE_MOMENTARY_ALPHA_AND_SYMBOL:
            if (code == Constants.CODE_SWITCH_ALPHA_SYMBOL) {
                // Detected only the mode change key has been pressed, and then released.
                if (mIsAlphabetMode) {
                    mSwitchState = SWITCH_STATE_ALPHA;
                } else {
                    mSwitchState = SWITCH_STATE_SYMBOL_BEGIN;
                }
            }
            break;
        case SWITCH_STATE_MOMENTARY_SYMBOL_AND_MORE:
            if (code == Constants.CODE_SHIFT) {
                // Detected only the shift key has been pressed on symbol layout, and then
                // released.
                mSwitchState = SWITCH_STATE_SYMBOL_BEGIN;
            }
            break;
        case SWITCH_STATE_SYMBOL_BEGIN:
            break;
        case SWITCH_STATE_SYMBOL:
            break;
        }
    }

    static String shiftModeToString(final int shiftMode) {
        switch (shiftMode) {
        case UNSHIFT: return "UNSHIFT";
        case MANUAL_SHIFT: return "MANUAL";
        case AUTOMATIC_SHIFT: return "AUTOMATIC";
        default: return null;
        }
    }

    private static String switchStateToString(final int switchState) {
        switch (switchState) {
        case SWITCH_STATE_ALPHA: return "ALPHA";
        case SWITCH_STATE_SYMBOL_BEGIN: return "SYMBOL-BEGIN";
        case SWITCH_STATE_SYMBOL: return "SYMBOL";
        case SWITCH_STATE_MOMENTARY_ALPHA_AND_SYMBOL: return "MOMENTARY-ALPHA-SYMBOL";
        case SWITCH_STATE_MOMENTARY_SYMBOL_AND_MORE: return "MOMENTARY-SYMBOL-MORE";
        default: return null;
        }
    }

    @Override
    public String toString() {
        return "[keyboard=" + ((mIsSymbolShifted ? "SYMBOLS_SHIFTED" : "SYMBOLS"))
                + " shift=" + mShiftKeyState
                + " symbol=" + mSymbolKeyState
                + " switch=" + switchStateToString(mSwitchState) + "]";
    }

    private String stateToString(final int autoCapsFlags, final int recapitalizeMode) {
        return this + " autoCapsFlags=" + CapsModeUtils.flagsToString(autoCapsFlags)
                + " recapitalizeMode=" + RecapitalizeStatus.modeToString(recapitalizeMode);
    }
}
