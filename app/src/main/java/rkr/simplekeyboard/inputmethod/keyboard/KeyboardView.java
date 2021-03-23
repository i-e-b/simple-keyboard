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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.compat.PreferenceManagerCompat;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyDrawParams;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyVisualAttributes;
import rkr.simplekeyboard.inputmethod.latin.settings.Settings;

/**
 * A view that renders a virtual {@link Keyboard}.
 *
 * @attr ref R.styleable#KeyboardView_keyBackground
 * @attr ref R.styleable#KeyboardView_functionalKeyBackground
 * @attr ref R.styleable#KeyboardView_spacebarBackground
 * @attr ref R.styleable#KeyboardView_spacebarIconWidthRatio
 * @attr ref R.styleable#Keyboard_Key_keyLabelFlags
 * @attr ref R.styleable#KeyboardView_keyHintLetterPadding
 * @attr ref R.styleable#KeyboardView_keyShiftedLetterHintPadding
 * @attr ref R.styleable#KeyboardView_keyTextShadowRadius
 * @attr ref R.styleable#KeyboardView_verticalCorrection
 * @attr ref R.styleable#Keyboard_Key_keyTypeface
 * @attr ref R.styleable#Keyboard_Key_keyLetterSize
 * @attr ref R.styleable#Keyboard_Key_keyLabelSize
 * @attr ref R.styleable#Keyboard_Key_keyLargeLetterRatio
 * @attr ref R.styleable#Keyboard_Key_keyLargeLabelRatio
 * @attr ref R.styleable#Keyboard_Key_keyHintLetterRatio
 * @attr ref R.styleable#Keyboard_Key_keyShiftedLetterHintRatio
 * @attr ref R.styleable#Keyboard_Key_keyHintLabelRatio
 * @attr ref R.styleable#Keyboard_Key_keyLabelOffCenterRatio
 * @attr ref R.styleable#Keyboard_Key_keyHintLabelOffCenterRatio
 * @attr ref R.styleable#Keyboard_Key_keyPreviewTextRatio
 * @attr ref R.styleable#Keyboard_Key_keyTextColor
 * @attr ref R.styleable#Keyboard_Key_keyTextColorDisabled
 * @attr ref R.styleable#Keyboard_Key_keyTextShadowColor
 * @attr ref R.styleable#Keyboard_Key_keyHintLetterColor
 * @attr ref R.styleable#Keyboard_Key_keyHintLabelColor
 * @attr ref R.styleable#Keyboard_Key_keyShiftedLetterHintInactivatedColor
 * @attr ref R.styleable#Keyboard_Key_keyShiftedLetterHintActivatedColor
 * @attr ref R.styleable#Keyboard_Key_keyPreviewTextColor
 */
public class KeyboardView extends View {
    // XML attributes
    private final KeyVisualAttributes mKeyVisualAttributes;
    private final float mVerticalCorrection;
    private final Drawable mKeyBackground;
    private final Rect mKeyBackgroundPadding = new Rect();
    private static final float KET_TEXT_SHADOW_RADIUS_DISABLED = -1.0f;
    public int mCustomColor = 0;

    protected static boolean sIsBeingPressed = false;

    // The maximum key label width in the proportion to the key width.
    private static final float MAX_LABEL_RATIO = 0.90f;

    // Main keyboard
    // TODO: Consider having a dummy keyboard object to make this @NonNull
    private Keyboard mKeyboard;
    private final KeyDrawParams mKeyDrawParams = new KeyDrawParams();

    // Drawing
    /** True if all keys should be drawn */
    private boolean mInvalidateAllKeys;
    /** The keys that should be drawn */
    private final HashSet<Key> mInvalidatedKeys = new HashSet<>();
    /** The working rectangle for clipping */
    private final Rect mClipRect = new Rect();
    /** The keyboard bitmap buffer for faster updates */
    private Bitmap mOffscreenBuffer;
    /** The canvas for the above mutable keyboard bitmap */
    private final Canvas mOffscreenCanvas = new Canvas();
    private final Paint mPaint = new Paint();
    private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();

    public KeyboardView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    public KeyboardView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray keyboardViewAttr = context.obtainStyledAttributes(attrs,
                R.styleable.KeyboardView, defStyle, R.style.KeyboardView);
        mKeyBackground = keyboardViewAttr.getDrawable(R.styleable.KeyboardView_keyBackground);
        mKeyBackground.getPadding(mKeyBackgroundPadding);
        final Drawable functionalKeyBackground = keyboardViewAttr.getDrawable(
                R.styleable.KeyboardView_functionalKeyBackground);
        final Drawable spacebarBackground = keyboardViewAttr.getDrawable(
                R.styleable.KeyboardView_spacebarBackground);
        mVerticalCorrection = keyboardViewAttr.getDimension(
                R.styleable.KeyboardView_verticalCorrection, 0.0f);
        keyboardViewAttr.recycle();

        final TypedArray keyAttr = context.obtainStyledAttributes(attrs,
                R.styleable.Keyboard_Key, defStyle, R.style.KeyboardView);
        mKeyVisualAttributes = KeyVisualAttributes.newInstance(keyAttr);
        keyAttr.recycle();

        mPaint.setAntiAlias(true);
    }

    /**
     * Attaches a keyboard to this view. The keyboard can be switched at any time and the
     * view will re-layout itself to accommodate the keyboard.
     * @see Keyboard
     * @see #getKeyboard()
     * @param keyboard the keyboard to display in this view
     */
    public void setKeyboard(final Keyboard keyboard) {
        mKeyboard = keyboard;
        final int keyHeight = keyboard.mMostCommonKeyHeight;
        mKeyDrawParams.updateParams(keyHeight, mKeyVisualAttributes);
        mKeyDrawParams.updateParams(keyHeight, keyboard.mKeyVisualAttributes);
        final SharedPreferences prefs = PreferenceManagerCompat.getDeviceSharedPreferences(getContext());
        mCustomColor = Settings.readKeyboardColor(prefs, getContext());
        invalidateAllKeys();
        requestLayout();
    }

    /**
     * Returns the current keyboard being displayed by this view.
     * @return the currently attached keyboard
     * @see #setKeyboard(Keyboard)
     */
    public Keyboard getKeyboard() {
        return mKeyboard;
    }

    protected float getVerticalCorrection() {
        return mVerticalCorrection;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Keyboard keyboard = getKeyboard();
        if (keyboard == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        // The main keyboard expands to the entire this {@link KeyboardView}.
        final int width = keyboard.mOccupiedWidth + getPaddingLeft() + getPaddingRight();
        final int height = keyboard.mOccupiedHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        onDrawKeyboard(canvas);
    }

    private void freeOffscreenBuffer() {
        mOffscreenCanvas.setBitmap(null);
        mOffscreenCanvas.setMatrix(null);
        if (mOffscreenBuffer != null) {
            mOffscreenBuffer.recycle();
            mOffscreenBuffer = null;
        }
    }

    protected float mLastTouchX = 0.0f;
    protected float mLastTouchY = 0.0f;

    // TODO: this IS the top level drawing.
    private void onDrawKeyboard(final Canvas canvas) {
        PointerTracker.lastDrawLeft = this.getLeft();
        PointerTracker.lastDrawTop = this.getLeft();
        PointerTracker.lastDrawWidth = this.getWidth();
        PointerTracker.lastDrawHeight = this.getHeight();

        int height = canvas.getHeight();
        int width = canvas.getWidth();
        float fontDiv = Math.min(height, width) / 9.0f;

        int xZero = 0;
        int yZero = 0;
        int oneThirdWidth = width / 3;
        int oneThirdHeight = height / 3;
        int twoThirdWidth = oneThirdWidth *2;
        int twoThirdHeight = oneThirdHeight*2;

        int ninthWidth = width / 9;
        int ninthHeight = height / 9;

        final Paint paint = mPaint;
        setBackgroundColor(mCustomColor);

        paint.setARGB(255, 255, 255,255);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setARGB(255, 127, 255,127);
        canvas.drawCircle(mLastTouchX, mLastTouchY, 50, paint);

        // Major lines
        paint.setARGB(255, 0, 0,64);
        canvas.drawLine(xZero, oneThirdHeight, width, oneThirdHeight, paint);
        canvas.drawLine(xZero, twoThirdHeight, width, twoThirdHeight, paint);

        canvas.drawLine(oneThirdWidth, yZero, oneThirdWidth, height, paint);
        canvas.drawLine(twoThirdWidth, yZero, twoThirdWidth, height, paint);

        // Minor lines (pre-touch only)
        if(!sIsBeingPressed) {
            paint.setARGB(255, 180, 180, 127);
            for (int i = 1; i < 9; i++) {
                canvas.drawLine(xZero, ninthHeight * i, width, ninthHeight * i, paint);
                canvas.drawLine(ninthWidth * i, yZero, ninthWidth * i, height, paint);
            }
        }

        paint.setARGB(255, 127, 127,255);
        paint.setTypeface(Typeface.MONOSPACE);

        // TODO: draw keys in appropriate zoom mode
        if(sIsBeingPressed) {
            float bigDiv = fontDiv * 3;
            paint.setTextSize(bigDiv);
            canvas.drawText("Big font", 12.5f, fontDiv + bigDiv, paint);
        } else {
            paint.setTextSize(fontDiv);
            canvas.drawText("←↑→↓↲⇥⇦ Small font = "+fontDiv,12.5f, fontDiv, paint);
        }


        mInvalidatedKeys.clear();
        mInvalidateAllKeys = false;
    }

    public Paint newLabelPaint(final Key key) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (key == null) {
            paint.setTypeface(mKeyDrawParams.mTypeface);
            paint.setTextSize(mKeyDrawParams.mLabelSize);
        } else {
            paint.setColor(key.selectTextColor(mKeyDrawParams));
            paint.setTypeface(key.selectTypeface(mKeyDrawParams));
            paint.setTextSize(key.selectTextSize(mKeyDrawParams));
        }
        return paint;
    }

    /**
     * Requests a redraw of the entire keyboard. Calling {@link #invalidate} is not sufficient
     * because the keyboard renders the keys to an off-screen buffer and an invalidate() only
     * draws the cached buffer.
     * @see #invalidateKey(Key)
     */
    public void invalidateAllKeys() {
        mInvalidatedKeys.clear();
        mInvalidateAllKeys = true;
        invalidate();
    }

    /**
     * Invalidates a key so that it will be redrawn on the next repaint. Use this method if only
     * one key is changing it's content. Any changes that affect the position or size of the key
     * may not be honored.
     * @param key key in the attached {@link Keyboard}.
     * @see #invalidateAllKeys
     */
    public void invalidateKey(final Key key) {
        if (mInvalidateAllKeys || key == null) {
            return;
        }
        mInvalidatedKeys.add(key);
        final int x = key.getX() + getPaddingLeft();
        final int y = key.getY() + getPaddingTop();
        invalidate(x, y, x + key.getWidth(), y + key.getHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        freeOffscreenBuffer();
    }

    public void deallocateMemory() {
        freeOffscreenBuffer();
    }
}
