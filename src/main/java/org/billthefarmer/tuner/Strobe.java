////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.tuner;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

// Strobe
public class Strobe extends TunerView
    implements ValueAnimator.AnimatorUpdateListener
{
    protected int colour;
    protected int foreground;
    protected int background;

    private static final int CUSTOM = 3;

    private static final float SLOW = 20;
    private static final float MEDIUM = 30;
    private static final float FAST = 40;

    private static final float SCALE_VALUE = 1000;

    private Matrix matrix;
    private Canvas source;
    private Bitmap bitmap;
    private Bitmap rounded;
    private Paint xferPaint;

    private BitmapShader smallShader;
    private BitmapShader mediumShader;
    private BitmapShader largeShader;
    private BitmapShader largerShader;
    private LinearGradient smallGradient;
    private LinearGradient mediumGradient;
    private LinearGradient largeGradient;
    private LinearGradient smallBlurGradient;

    private int size;
    private float offset;
    private float scale;
    private double cents;

    // Constructor
    public Strobe(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    // On size changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        // Recalculate dimensions
        width = clipRect.right - clipRect.left;
        height = clipRect.bottom - clipRect.top;

        // Calculate size and scale
        size = height / 4;
        scale = width / SCALE_VALUE;

        // Create rounded bitmap
        rounded = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounded);
        paint.reset();
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(new RectF(0, 0, width, height), 10, 10, paint);

        // Create magic paint
        xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // Create a bitmap to draw on
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        source = new Canvas(bitmap);

        // Create matrix for translating shaders
        matrix = new Matrix();

        // Create animator
        ValueAnimator animator = ValueAnimator.ofInt(0, 10000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(10000);

        animator.addUpdateListener(this);

        animator.start();

        // Create the shaders
        createShaders();
    }

    // Animation update

    @Override
    public void onAnimationUpdate(ValueAnimator animator)
    {
        // Do inertia calculation

        if (audio != null)
            cents = (cents * 19.0 + audio.cents) / 20.0;

        invalidate();
    }

    // Create shaders
    protected void createShaders()
    {
        // Get the colours
        if (audio != null)
        {
            Resources resources = getResources();

            int[] fore = resources.getIntArray(R.array.foreground_colours);
            int[] back = resources.getIntArray(R.array.background_colours);

            if (colour < CUSTOM)
            {
                foreground = fore[colour];
                background = back[colour];
            }
        }

        // Create the bitmap shaders
        smallShader =
            new BitmapShader(createShaderBitmap(size,
                                                size, foreground, background),
                             Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mediumShader =
            new BitmapShader(createShaderBitmap(size * 2,
                                                size, foreground, background),
                             Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        largeShader =
            new BitmapShader(createShaderBitmap(size * 4,
                                                size, foreground, background),
                             Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        largerShader =
            new BitmapShader(createShaderBitmap(size * 8,
                                                size, foreground, background),
                             Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);

        // Create the gradients
        smallGradient = new LinearGradient(0, 0, size, 0,
                                           background, foreground,
                                           Shader.TileMode.MIRROR);
        mediumGradient = new LinearGradient(0, 0, size * 2, 0,
                                            background, foreground,
                                            Shader.TileMode.MIRROR);
        largeGradient = new LinearGradient(0, 0, size * 4, 0,
                                           background, foreground,
                                           Shader.TileMode.MIRROR);

        // Calculate intermediate colours for the small gradient
        int red = (Color.red(foreground) + Color.red(background)) / 2;
        int green = (Color.green(foreground) + Color.green(background)) / 2;
        int blue = (Color.blue(foreground) + Color.blue(background)) / 2;

        smallBlurGradient =
            new LinearGradient(0, 0, size, 0,
                               background,
                               Color.argb(191, red, green, blue),
                               Shader.TileMode.MIRROR);
    }

    // Create shader bitmap
    private Bitmap createShaderBitmap(int width, int height, int f, int b)
    {
        // Create bitmap twice as wide as the block
        Bitmap bitmap =
            Bitmap.createBitmap(width * 2, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        // Draw the bitmap
        canvas.drawColor(b);
        paint.setColor(f);
        canvas.drawRect(0, 0, width, height, paint);

        return bitmap;
    }

    // On draw
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // Don't draw if turned off
        if (audio == null || !audio.strobe)
            return;

        // Calculate offset
        offset = offset + ((float) cents * scale);

        if (offset > size * 16)
            offset = 0;

        if (offset < 0)
            offset = size * 16;

        // Draw strobe
        drawStrobe(source);

        // Use the magic paint
        source.drawBitmap(rounded, 0, 0, xferPaint);

        // Draw the result on the canvas
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    // Draw strobe
    private void drawStrobe(Canvas canvas)
    {
        // Reset the paint
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        // Translate
        matrix.setTranslate(offset, 0);

        // Draw the strobe chequers
        if (Math.abs(cents) < SLOW)
        {
            smallShader.setLocalMatrix(matrix);
            paint.setShader(smallShader);
            canvas.drawRect(0, 0, width, size, paint);
        }
        else if (Math.abs(cents) < MEDIUM)
        {
            smallGradient.setLocalMatrix(matrix);
            paint.setShader(smallGradient);
            canvas.drawRect(0, 0, width, size, paint);
        }
        else if (Math.abs(cents) < FAST)
        {
            smallBlurGradient.setLocalMatrix(matrix);
            paint.setShader(smallBlurGradient);
            canvas.drawRect(0, 0, width, size, paint);
        }
        else
        {
            paint.setShader(null);
            paint.setColor(background);
            canvas.drawRect(0, 0, width, size, paint);
        }

        if (Math.abs(cents) < MEDIUM)
        {
            mediumShader.setLocalMatrix(matrix);
            paint.setShader(mediumShader);
            canvas.drawRect(0, size, width, size * 2, paint);
        }
        else
        {
            mediumGradient.setLocalMatrix(matrix);
            paint.setShader(mediumGradient);
            canvas.drawRect(0, size, width, size * 2, paint);
        }

        if (Math.abs(cents) < FAST)
        {
            largeShader.setLocalMatrix(matrix);
            paint.setShader(largeShader);
            canvas.drawRect(0, size * 2, width, size * 3, paint);
        }
        else
        {
            largeGradient.setLocalMatrix(matrix);
            paint.setShader(largeGradient);
            canvas.drawRect(0, size * 2, width, size * 3, paint);
        }

        largerShader.setLocalMatrix(matrix);
        paint.setShader(largerShader);
        canvas.drawRect(0, size * 3, width, size * 4, paint);
        paint.setShader(null);
    }
}
