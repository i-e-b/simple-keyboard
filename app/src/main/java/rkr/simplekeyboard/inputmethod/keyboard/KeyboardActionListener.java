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

import android.view.KeyEvent;

import rkr.simplekeyboard.inputmethod.latin.RichInputConnection;
import rkr.simplekeyboard.inputmethod.latin.common.Constants;

public interface KeyboardActionListener {
    /**
     * Sends a string of characters to the listener.
     *
     * @param rawText the string of characters to be registered.
     */
    void onTextInput(final String rawText);

    void SendKeyEvent(KeyEvent keyEvent);

    KeyboardActionListener EMPTY_LISTENER = new Adapter();


    class Adapter implements KeyboardActionListener {
        @Override
        public void SendKeyEvent(KeyEvent keyEvent) { }
        @Override
        public void onTextInput(String text) {}
    }
}
