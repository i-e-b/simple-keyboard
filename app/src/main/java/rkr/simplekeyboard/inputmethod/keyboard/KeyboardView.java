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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.compat.PreferenceManagerCompat;
import rkr.simplekeyboard.inputmethod.keyboard.internal.KeyboardParams;
import rkr.simplekeyboard.inputmethod.latin.settings.Settings;

/**
 * A view that renders a virtual {@link KeyboardParams}.
 */
public class KeyboardView extends View {
    /** If true, the keyboard area is reduced to make it square */
    private static final boolean FIX_TO_SQUARE = false;
    /** If true, the keyboard width is reduced to make it a little narrower, but not as extreme as 'FIX_TO_SQUARE' */
    private static final boolean NARROW_SLIGHTLY = true;
    // XML attributes
    public int mCustomColor = 0;

    protected static boolean sIsBeingPressed = false;

    // Main keyboard
    private KeyboardParams mKeyboard;

    // Drawing
    private final Paint mPaint = new Paint();
    private boolean mDarkColors = false;

    public KeyboardView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    public KeyboardView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        mPaint.setAntiAlias(true);
    }

    /**
     * Attaches a keyboard to this view. The keyboard can be switched at any time and the
     * view will re-layout itself to accommodate the keyboard.
     * @see KeyboardParams
     * @see #getKeyboard()
     * @param keyboard the keyboard to display in this view
     */
    public void setKeyboard(final KeyboardParams keyboard) {
        mKeyboard = keyboard;
        final SharedPreferences prefs = PreferenceManagerCompat.getDeviceSharedPreferences(getContext());
        mCustomColor = Settings.readKeyboardColor(prefs, getContext());
        invalidateAllKeys();
        requestLayout();
    }

    /**
     * Returns the current keyboard being displayed by this view.
     * @return the currently attached keyboard
     * @see #setKeyboard(KeyboardParams)
     */
    public KeyboardParams getKeyboard() {
        return mKeyboard;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final KeyboardParams keyboard = getKeyboard();
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

    protected float mLastTouchX = 0.0f;
    protected float mLastTouchY = 0.0f;

    // TODO: this IS the top level drawing.
    private void onDrawKeyboard(final Canvas canvas) {

        int left = 0;
        int top = 0;
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        if (FIX_TO_SQUARE){
            int min = Math.min(height, width);
            left = (width - min) / 2;
            top = (height - min) / 2;
            height = min;
            width = min;
        } else if (NARROW_SLIGHTLY){
            int min = Math.min(height, width);
            if (min < width){
                left = (width - min) / 4;
                width -= (width - min) / 2;
            }
        }

        PointerTracker.lastDrawLeft = left;
        PointerTracker.lastDrawTop = top;
        PointerTracker.lastDrawWidth = width;
        PointerTracker.lastDrawHeight = height;

        float fontDiv = (Math.min(height, width) / 9.0f) - 3.0f;

        int oneThirdWidth = width / 3;
        int oneThirdHeight = height / 3;
        int twoThirdWidth = oneThirdWidth *2;
        int twoThirdHeight = oneThirdHeight*2;

        int ninthWidth = width / 9;
        int ninthHeight = height / 9;

        final Paint paint = mPaint;

        // Light colors
        int backgroundColor = 0xFF_FF_FF_FF;
        int majorLineColor = 0xFF_00_00_00;

        if (mDarkColors){
            backgroundColor = 0xFF_00_00_00;
            majorLineColor = 0xFF_FF_FF_FF;
        }

        paint.setColor(backgroundColor);
        canvas.drawRect(left, top, width+left, height+top, paint);

        // Major lines
        paint.setColor(majorLineColor);
        canvas.drawLine(left, oneThirdHeight+top, width+left, oneThirdHeight+top, paint);
        canvas.drawLine(left, twoThirdHeight+top, width+left, twoThirdHeight+top, paint);

        canvas.drawLine(oneThirdWidth+left, top, oneThirdWidth+left, height+top, paint);
        canvas.drawLine(twoThirdWidth+left, top, twoThirdWidth+left, height+top, paint);

        paint.setTypeface(Typeface.MONOSPACE);
        String[][] layout = KeyboardLayout.CurrentLayout();

        if(!sIsBeingPressed) {
            DrawZoomedOutView(canvas, height, width, fontDiv, left, top, ninthWidth, ninthHeight, paint, layout);
        } else {
            DrawZoomedInView(canvas, left, top, fontDiv, oneThirdWidth, oneThirdHeight, paint, layout);
        }
    }

    private void DrawZoomedInView(Canvas canvas, int left, int top, float fontDiv, int oneThirdWidth, int oneThirdHeight, Paint paint, String[][] layout) {
        // Light colors
        int mainFontColor = 0xFF_00_00_7F;
        int modeFontColor = 0xFF_80_80_80;

        if (mDarkColors){
            mainFontColor = 0xFF_7F_7F_FF;
            //modeFontColor = 0xFF_80_80_80;
        }

        // Draw zoomed view
        int qx = KeyboardLayout.sQuadrantX;
        int qy = KeyboardLayout.sQuadrantY;

        float bigDiv = fontDiv * 3;
        paint.setTextSize(bigDiv);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                // bounds check
                if (y+qy >= layout.length) continue;
                if (x+qx >= layout[y+qy].length) continue;

                String desc = KeyboardLayout.Visualise(layout[y+qy][x+qx]);

                if (desc.length() > 1) {
                    paint.setColor(modeFontColor);
                    paint.setTextSize(bigDiv * 0.4f);
                }
                else {
                    paint.setColor(mainFontColor);
                    paint.setTextSize(bigDiv);
                }

                float sw = paint.measureText(desc);
                float offs = (oneThirdWidth / 2.0f) - (sw / 2.0f);
                canvas.drawText(desc,x*oneThirdWidth + offs + left, (bigDiv*0.9f)+(y*oneThirdHeight) + top, paint);
            }
        }
    }

    private void DrawZoomedOutView(Canvas canvas, int height, int width, float fontDiv, int left, int top, int ninthWidth, int ninthHeight, Paint paint, String[][] layout) {
        // Light colors
        int minorLineColor = 0xFF_A0_A0_A0;
        int mainFontColor = 0xFF_00_00_7F;
        int modeFontColor = 0xFF_80_80_80;

        if (mDarkColors){
            minorLineColor = 0xFF_80_80_80;
            mainFontColor = 0xFF_7F_7F_FF;
            //modeFontColor = 0xFF_80_80_80;
        }

        // Minor lines (pre-touch only)
        paint.setColor(minorLineColor);
        for (int i = 1; i < 9; i++) {
            canvas.drawLine(left, top+ninthHeight * i, left+width, top+ninthHeight * i, paint);
            canvas.drawLine(left + ninthWidth * i, top, left+ninthWidth * i, height, paint);
        }

        // key positions
        paint.setTextSize(fontDiv);
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                String desc = KeyboardLayout.Visualise(layout[y][x]);
                if (desc.length() > 1) {
                    paint.setColor(modeFontColor);
                    paint.setTextSize(fontDiv * 0.4f);
                }
                else {
                    paint.setColor(mainFontColor);
                    paint.setTextSize(fontDiv);
                }

                float sw = paint.measureText(desc);
                float offs = (ninthWidth / 2.0f) - (sw / 2.0f);
                canvas.drawText(desc,x*ninthWidth + offs+left, (fontDiv*0.9f)+(y*ninthHeight)+top, paint);
            }
        }
    }

    /**
     * Requests a redraw of the entire keyboard. Calling {@link #invalidate} is not sufficient
     * because the keyboard renders the keys to an off-screen buffer and an invalidate() only
     * draws the cached buffer.
     */
    public void invalidateAllKeys() {
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void deallocateMemory() {
    }

    public void SetNightMode() { mDarkColors = true; }
    public void SetDayMode() { mDarkColors = false; }
}
