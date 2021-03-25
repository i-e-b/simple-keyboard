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
 * A view that renders a virtual keyboard
 */
public class KeyboardView extends View {
    public int mCustomColor = 0;

    protected static boolean sIsBeingPressed = false;

    // Main keyboard
    private final KeyDrawParams mKeyDrawParams = new KeyDrawParams();

    // Drawing
    private final Paint mPaint = new Paint();

    public KeyboardView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    public KeyboardView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray keyboardViewAttr = context.obtainStyledAttributes(attrs,
                R.styleable.KeyboardView, defStyle, R.style.KeyboardView);
        keyboardViewAttr.recycle();

        final TypedArray keyAttr = context.obtainStyledAttributes(attrs,
                R.styleable.Keyboard_Key, defStyle, R.style.KeyboardView);
        keyAttr.recycle();

        mPaint.setAntiAlias(true);
        requestLayout();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        PointerTracker.lastDrawLeft = this.getLeft();
        PointerTracker.lastDrawTop = this.getLeft();
        PointerTracker.lastDrawWidth = this.getWidth();
        PointerTracker.lastDrawHeight = this.getHeight();

        int height = canvas.getHeight();
        int width = canvas.getWidth();
        float fontDiv = (Math.min(height, width) / 9.0f) - 3.0f;

        int xZero = 0;
        int yZero = 0;
        int oneThirdWidth = width / 3;
        int oneThirdHeight = height / 3;
        int twoThirdWidth = oneThirdWidth * 2;
        int twoThirdHeight = oneThirdHeight * 2;

        int ninthWidth = width / 9;
        int ninthHeight = height / 9;

        final Paint paint = mPaint;
        setBackgroundColor(mCustomColor);

        paint.setARGB(255, 255, 255, 255);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setARGB(255, 127, 255, 127);
        canvas.drawCircle(mLastTouchX, mLastTouchY, 50, paint);

        // Major lines
        paint.setARGB(255, 0, 0, 64);
        canvas.drawLine(xZero, oneThirdHeight, width, oneThirdHeight, paint);
        canvas.drawLine(xZero, twoThirdHeight, width, twoThirdHeight, paint);

        canvas.drawLine(oneThirdWidth, yZero, oneThirdWidth, height, paint);
        canvas.drawLine(twoThirdWidth, yZero, twoThirdWidth, height, paint);

        paint.setTypeface(Typeface.MONOSPACE);
        char[][] layout = KeyboardLayout.CurrentLayout();

        if (!sIsBeingPressed) {
            DrawZoomedOutView(canvas, height, width, fontDiv, xZero, yZero, ninthWidth, ninthHeight, paint, layout);
        } else {
            DrawZoomedInView(canvas, fontDiv, oneThirdWidth, oneThirdHeight, paint, layout);
        }
    }

    private void DrawZoomedInView(Canvas canvas, float fontDiv, int oneThirdWidth, int oneThirdHeight, Paint paint, char[][] layout) {
        // Draw zoomed view
        int qx = KeyboardLayout.sQuadrantX;
        int qy = KeyboardLayout.sQuadrantY;

        paint.setARGB(255, 63, 63, 127);
        float bigDiv = fontDiv * 3;
        paint.setTextSize(bigDiv);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                String desc = KeyboardLayout.Visualise(layout[y + qy][x + qx]);

                if (desc.length() > 1) {
                    paint.setARGB(255, 127, 127, 127);
                    paint.setTextSize(bigDiv * 0.7f);
                } else {
                    paint.setARGB(255, 63, 63, 127);
                    paint.setTextSize(bigDiv);
                }

                float sw = paint.measureText(desc);
                float offs = (oneThirdWidth / 2.0f) - (sw / 2.0f);
                canvas.drawText(desc, x * oneThirdWidth + offs, (bigDiv * 0.8f) + (y * oneThirdHeight), paint);
            }
        }
    }

    private void DrawZoomedOutView(Canvas canvas, int height, int width, float fontDiv, int xZero, int yZero, int ninthWidth, int ninthHeight, Paint paint, char[][] layout) {
        // Minor lines (pre-touch only)
        paint.setARGB(255, 180, 180, 127);
        for (int i = 1; i < 9; i++) {
            canvas.drawLine(xZero, ninthHeight * i, width, ninthHeight * i, paint);
            canvas.drawLine(ninthWidth * i, yZero, ninthWidth * i, height, paint);
        }

        // key positions
        paint.setTextSize(fontDiv);
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                String desc = KeyboardLayout.Visualise(layout[y][x]);
                if (desc.length() > 1) {
                    paint.setARGB(255, 127, 127, 127);
                    paint.setTextSize(fontDiv * 0.6f);
                } else {
                    paint.setARGB(255, 63, 63, 127);
                    paint.setTextSize(fontDiv);
                }

                float sw = paint.measureText(desc);
                float offs = (ninthWidth / 2.0f) - (sw / 2.0f);
                canvas.drawText(desc, x * ninthWidth + offs, (fontDiv * 0.8f) + (y * ninthHeight), paint);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
