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
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Objects;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.keyboard.internal.DrawingProxy;
import rkr.simplekeyboard.inputmethod.keyboard.internal.PointerTrackerQueue;
import rkr.simplekeyboard.inputmethod.latin.common.CoordinateUtils;
import rkr.simplekeyboard.inputmethod.latin.define.DebugFlags;

public final class PointerTracker implements PointerTrackerQueue.Element {
    private static final String TAG = PointerTracker.class.getSimpleName();
    private static final boolean DEBUG_EVENT = false;
    private static final boolean DEBUG_MOVE_EVENT = false;
    private static final boolean DEBUG_MODE = DebugFlags.DEBUG_ENABLED || DEBUG_EVENT;

    public static int lastDrawTop, lastDrawLeft, lastDrawWidth, lastDrawHeight;

    static final class PointerTrackerParams {
        public final boolean mKeySelectionByDraggingFinger;
        public final int mTouchNoiseThresholdTime;
        public final int mTouchNoiseThresholdDistance;
        public final int mKeyRepeatStartTimeout;
        public final int mKeyRepeatInterval;
        public final int mLongPressShiftLockTimeout;

        public PointerTrackerParams(final TypedArray mainKeyboardViewAttr) {
            mKeySelectionByDraggingFinger = mainKeyboardViewAttr.getBoolean(
                    R.styleable.MainKeyboardView_keySelectionByDraggingFinger, false);
            mTouchNoiseThresholdTime = mainKeyboardViewAttr.getInt(
                    R.styleable.MainKeyboardView_touchNoiseThresholdTime, 0);
            mTouchNoiseThresholdDistance = mainKeyboardViewAttr.getDimensionPixelSize(
                    R.styleable.MainKeyboardView_touchNoiseThresholdDistance, 0);
            mKeyRepeatStartTimeout = mainKeyboardViewAttr.getInt(
                    R.styleable.MainKeyboardView_keyRepeatStartTimeout, 0);
            mKeyRepeatInterval = mainKeyboardViewAttr.getInt(
                    R.styleable.MainKeyboardView_keyRepeatInterval, 0);
            mLongPressShiftLockTimeout = mainKeyboardViewAttr.getInt(
                    R.styleable.MainKeyboardView_longPressShiftLockTimeout, 0);
        }
    }

    // Parameters for pointer handling.
    private static PointerTrackerParams sParams;

    private static final ArrayList<PointerTracker> sTrackers = new ArrayList<>();
    private static final PointerTrackerQueue sPointerTrackerQueue = new PointerTrackerQueue();

    public final int mPointerId;

    private static DrawingProxy sDrawingProxy;
    private static KeyboardActionListener sListener = KeyboardActionListener.EMPTY_LISTENER;

    // Last pointer position.
    private int mLastX;
    private int mLastY;
    private boolean mCursorMoved = false;

    // true if this pointer is no longer triggering any action because it has been canceled.
    private boolean mIsTrackingForActionDisabled;

    // true if this pointer is in the dragging finger mode.
    boolean mIsInDraggingFinger;
    // true if this pointer is sliding from a modifier key and in the sliding key input mode,
    // so that further modifier keys should be ignored.
    boolean mIsInSlidingKeyInput;

    public static void init(final TypedArray mainKeyboardViewAttr,
                            final DrawingProxy drawingProxy) {
        sParams = new PointerTrackerParams(mainKeyboardViewAttr);

        sDrawingProxy = drawingProxy;
    }

    public static PointerTracker getPointerTracker(final int id) {
        final ArrayList<PointerTracker> trackers = sTrackers;

        // Create pointer trackers until we can get 'id+1'-th tracker, if needed.
        for (int i = trackers.size(); i <= id; i++) {
            final PointerTracker tracker = new PointerTracker(i);
            trackers.add(tracker);
        }

        return trackers.get(id);
    }

    public static void cancelAllPointerTrackers() {
        sPointerTrackerQueue.cancelAllPointerTrackers();
    }

    public static void setKeyboardActionListener(final KeyboardActionListener listener) {
        sListener = listener;
    }

    private PointerTracker(final int id) {
        mPointerId = id;
    }

    @Override
    public boolean isModifier() {
        return false;
    }

    public void getLastCoordinates(final int[] outCoords) {
        CoordinateUtils.set(outCoords, mLastX, mLastY);
    }

    public void processMotionEvent(final MotionEvent me) {
        final int action = me.getActionMasked();
        final long eventTime = me.getEventTime();
        final int index = me.getActionIndex();
        final int x = (int)(me.getX(index));
        final int y = (int)(me.getY(index));
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                onMoveEvent(x,y,eventTime);
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                onDownEvent(x, y, eventTime);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                onUpEvent(x, y, eventTime);
                break;
            case MotionEvent.ACTION_CANCEL:
                onCancelEvent(x, y, eventTime);
                break;
        }
    }

    private void onDownEvent(final int x, final int y, final long eventTime) {
        if (DEBUG_EVENT) {
            printTouchEvent("onDownEvent:", x, y, eventTime);
        }

        sPointerTrackerQueue.add(this);
        KeyboardLayout.TouchDown(getXIndex(x),getYIndex(y));
        sDrawingProxy.onKeyPressed();
    }

    private  int getXIndex(int x){
        int xi = ((x-lastDrawLeft)*3) / lastDrawWidth;
        if (xi > 2) xi = 2;
        if (xi < 0) xi = 0;
        return xi;
    }
    private  int getYIndex(int y){
        int yi = ((y-lastDrawTop)*3) / lastDrawHeight;
        if (yi > 2) yi = 2;
        if (yi < 0) yi = 0;
        return yi;
    }

    private void resetKeySelectionByDraggingFinger() {
        mIsInDraggingFinger = false;
        mIsInSlidingKeyInput = false;
    }

    private void onMoveEvent(final int x, final int y, final long eventTime) {
        if (DEBUG_MOVE_EVENT) {
            printTouchEvent("onMoveEvent:", x, y, eventTime);
        }
        mLastX = x;
        mLastY = y;
        sDrawingProxy.invalidateAll();
    }

    private void onUpEvent(final int x, final int y, final long eventTime) {
        if (DEBUG_EVENT) {
            printTouchEvent("onUpEvent  :", x, y, eventTime);
        }

        mLastX = x;
        mLastY = y;
        sDrawingProxy.onKeyReleased();

        sPointerTrackerQueue.releaseAllPointersOlderThan(this, eventTime);

        // get the key and send it
        String result = KeyboardLayout.TouchUp(getXIndex(x),getYIndex(y));
        if (Objects.equals(result, KeyboardLayout.nul)) {
            // nothing- most likely out-of-bounds, or an empty slot
            if (DEBUG_EVENT) {
                printTouchEvent("up for nothing:", x, y, eventTime);
            }
        } else if (KeyboardLayout.IsInternal(result)) {
            KeyboardLayout.SwitchMode(result);
        } else if (KeyboardLayout.IsSimple(result)) {
            sListener.onTextInput(result);
        } else {
            int[] codeAndMeta = KeyboardLayout.GetSpecialKey(result);
            if (codeAndMeta.length == 2 && codeAndMeta[0] > 0) {
                long t = SystemClock.uptimeMillis();
                sListener.SendKeyEvent(new KeyEvent(t, t, KeyEvent.ACTION_DOWN, codeAndMeta[0], 0, codeAndMeta[1]));
                t++;
                sListener.SendKeyEvent(new KeyEvent(t, t, KeyEvent.ACTION_UP, codeAndMeta[0], 0, codeAndMeta[1]));
            }
        }

        // clean up
        sPointerTrackerQueue.remove(this);
    }

    // Let this pointer tracker know that one of newer-than-this pointer trackers got an up event.
    // This pointer tracker needs to keep the key top graphics "pressed", but needs to get a
    // "virtual" up event.
    @Override
    public void onPhantomUpEvent(final long eventTime) {
        if (DEBUG_EVENT) {
            printTouchEvent("onPhntEvent:", mLastX, mLastY, eventTime);
        }
        onUpEventInternal();
        cancelTrackingForAction();
    }

    private void onUpEventInternal() {
        resetKeySelectionByDraggingFinger();

        // Release the last pressed key.
        sDrawingProxy.onKeyReleased();

        if (mCursorMoved) {
            mCursorMoved = false;
        }
    }

    @Override
    public void cancelTrackingForAction() {
        mIsTrackingForActionDisabled = true;
    }

    private void onCancelEvent(final int x, final int y, final long eventTime) {
        if (DEBUG_EVENT) {
            printTouchEvent("onCancelEvt:", x, y, eventTime);
        }

        sDrawingProxy.onKeyReleased();

        cancelAllPointerTrackers();
        sPointerTrackerQueue.releaseAllPointers(eventTime);
        onCancelEventInternal();
    }

    private void onCancelEventInternal() {
        resetKeySelectionByDraggingFinger();
    }

    private void printTouchEvent(final String title, final int x, final int y,
            final long eventTime) {
        Log.d(TAG, String.format("[%d]%s%s %4d %4d %5d %s", mPointerId,
                (mIsTrackingForActionDisabled ? "-" : " "), title, x, y, eventTime, 0));
    }
}
