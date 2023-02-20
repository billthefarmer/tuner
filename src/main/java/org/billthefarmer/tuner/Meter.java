////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version  of the License, or
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

// Meter
public class Meter extends TunerView
{
    private LinearGradient gradient;
    private Matrix matrix;
    private Rect bar;
    private Path thumb;

    private double cents;
    private float medium;

    // Constructor
    public Meter(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create a matrix for scaling
        matrix = new Matrix();
    }

    // On size changed
    @Override
    @SuppressWarnings("deprecation")
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        // Recalculate dimensions
        width = clipRect.right - clipRect.left;
        height = clipRect.bottom - clipRect.top;

        // Recalculate text size
        medium = height / 3.0f;
        paint.setTextSize(medium);

        // Scale text if necessary to fit it in
        float dx = paint.measureText("50");
        if (dx >= width / 11)
            paint.setTextScaleX((width / 12) / dx);

        // Create a rect for the horizontal bar
        bar = new Rect(width / 36 - width / 2, -height / 128,
                       width / 2 - width / 36, height / 128);

        // Create a path for the thumb
        thumb = new Path();

        thumb.moveTo(0, -2);
        thumb.lineTo(1, -1);
        thumb.lineTo(1, 2);
        thumb.lineTo(-1, 2);
        thumb.lineTo(-1, -1);
        thumb.close();

        // Create a gradient for the thumb
        gradient = new
        LinearGradient(0, 0, 0, 4,
                       resources.getColor(android.R.color.background_light),
                       resources.getColor(android.R.color.primary_text_light),
                       Shader.TileMode.MIRROR);

        // Create a matrix to scale the thumb
        matrix.setScale(height / 16, height / 16);

        // Scale the thumb
        thumb.transform(matrix);

        // Scale the gradient
        gradient.setLocalMatrix(matrix);

        // Schedule update
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                post(() -> update());
            }
        }, 10, 10);
    }

    // update
    private void update()
    {
        // Do the inertia calculation
        if (audio != null)
            cents = ((cents * 19.0) + audio.cents) / 20.0;

        invalidate();
    }

    // On draw
    @Override
    @SuppressWarnings("deprecation")
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // Draw display icon
        if (audio != null && audio.screen && screen != null)
            canvas.drawBitmap(screen.getBitmap(), 0,
                              height - screen.getIntrinsicHeight(), null);

        // Reset the paint to black
        paint.setStrokeWidth(1);
        paint.setColor(textColour);
        paint.setStyle(Paint.Style.FILL);

        // Translate the canvas down
        // and to the centre
        canvas.translate(width / 2, medium);

        // Calculate x scale
        float xscale = width / 11;

        // Draw the scale legend
        for (int i = 0; i <= 5; i++)
        {
            String s = String.format(Locale.getDefault(), "%d", i * 10);
            float x = i * xscale;

            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(s, x, 0, paint);
            canvas.drawText(s, -x, 0, paint);
        }

        // Wider lines for the scale
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        canvas.translate(0, medium / 1.5f);

        // Draw the scale
        for (int i = 0; i <= 5; i++)
        {
            float x = i * xscale;

            canvas.drawLine(x, 0, x, -medium / 2, paint);
            canvas.drawLine(-x, 0, -x, -medium / 2, paint);
        }

        // Draw the fine scale
        for (int i = 0; i <= 25; i++)
        {
            float x = i * xscale / 5;

            canvas.drawLine(x, 0, x, -medium / 4, paint);
            canvas.drawLine(-x, 0, -x, -medium / 4, paint);
        }

        // Transform the canvas down
        // for the meter pointer
        canvas.translate(0, medium / 2.0f);

        // Set fill style and fill
        // the bar

        // paint.setShader(gradient);
        // paint.setStyle(Paint.Style.FILL);
        // canvas.drawRect(bar, paint);
        // paint.setShader(null);

        // Draw the bar outline
        paint.setStrokeWidth(2);
        paint.setColor(resources.getColor(android.R.color.darker_gray));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(bar, paint);

        // Translate the canvas to
        // the scaled cents value
        canvas.translate((float) cents * (xscale / 10), -height / 64);

        // Set up the paint for
        // rounded corners
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        // Set fill style and fill
        // the thumb
        paint.setShader(gradient);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(thumb, paint);
        paint.setShader(null);

        // Draw the thumb outline
        paint.setStrokeWidth(2);
        paint.setColor(resources.getColor(android.R.color.darker_gray));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(thumb, paint);
    }
}
