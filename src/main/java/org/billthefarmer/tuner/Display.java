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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Locale;

// Display
public class Display extends TunerView
{
    private static final String TAG = "Display";
    private static final int OCTAVE = 12;

    private int larger;
    private int large;
    private int medium;
    private int small;

    private int margin;

    // Note values for display
    private static final String notes[] =
    {
        "C", "C", "D", "E", "E", "F",
        "F", "G", "A", "A", "B", "B"
    };

    private static final String sharps[] =
    {
        "", "\u266F", "", "\u266D", "", "",
        "\u266F", "", "\u266D", "", "\u266D", ""
    };

    // Constructor
    public Display(Context context, AttributeSet attrs)
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

        // Calculate text sizes
        larger = height / 2;
        large = height / 3;
        medium = height / 5;
        small = height / 9;
        margin = width / 32;

        // Make sure the text will fit the width
        paint.setTextSize(medium);
        paint.setTextScaleX(1.0f);
        float dx = paint.measureText("0000.00Hz");

        // Scale the text if it won't fit
        if (dx + (margin * 2) >= width / 2)
        {
            float xscale = (width / 2) / (dx + (margin * 2));
            paint.setTextScaleX(xscale);
        }
    }

    // On draw
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // No display if no audio
        if (audio == null)
            return;

        // Draw lock icon
        if (audio.lock && lock != null)
            canvas.drawBitmap(lock.getBitmap(), 2,
                              height - lock.getIntrinsicHeight() - 2, null);
        // Set up paint
        paint.setStrokeWidth(1);
        paint.setColor(textColour);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);

        // Multiple values
        if (audio.multiple)
        {
            String s;
            float x;

            // Set text size
            paint.setTextSize(small);

            for (int i = 0; i < audio.count; i++)
            {
                // Move down
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.translate(0, small);

                // Calculate cents
                double cents = -12.0 * log2(audio.maxima.r[i] /
                                            audio.maxima.f[i]) * 100.0;
                // Ignore silly values
                if (Double.isNaN(cents))
                    continue;

                // Draw note
                s = String.format(Locale.getDefault(), "%s",
                                  notes[(audio.maxima.n[i] - audio.transpose +
                                         OCTAVE) % OCTAVE]);
                canvas.drawText(s, margin / 2, 0, paint);
                float dx = paint.measureText(s);

                // Draw sharp/flat
                paint.setTextSize(small / 2);
                s = String.format(Locale.getDefault(), "%s",
                                  sharps[(audio.maxima.n[i] - audio.transpose +
                                          OCTAVE) % OCTAVE]);
                canvas.drawText(s, margin / 2 + dx, paint.ascent(), paint);

                // Draw octave
                s = String.format(Locale.getDefault(), "%d",
                                  (audio.maxima.n[i] -
                                   audio.transpose) / OCTAVE);
                canvas.drawText(s, margin / 2 + dx, 0, paint);

                // Draw cents
                paint.setTextSize(small);
                s = String.format(Locale.getDefault(),
                                  "%+5.2f\u00A2", cents);
                x = width * 2 / 23;
                canvas.drawText(s, x, 0, paint);

                // Draw nearest
                s = String.format(Locale.getDefault(),
                                  "%4.2fHz", audio.maxima.r[i]);
                x = width * 25 / 92;
                canvas.drawText(s, x, 0, paint);

                // Draw frequency
                s = String.format(Locale.getDefault(),
                                  "%4.2fHz", audio.maxima.f[i]);
                x = width * 12 / 23;
                canvas.drawText(s, x, 0, paint);

                // Draw difference
                x = width - margin / 2;
                paint.setTextAlign(Paint.Align.RIGHT);
                s = String.format(Locale.getDefault(),
                                  "%+5.2fHz", audio.maxima.r[i] -
                                  audio.maxima.f[i]);
                canvas.drawText(s, x, 0, paint);
            }

            // If multiple and no data, don't have a blank display
            if (audio.count == 0)
            {
                // Move down
                canvas.translate(0, small);

                // Draw note
                s = String.format(Locale.getDefault(), "%s",
                                  notes[(audio.note - audio.transpose +
                                         OCTAVE) % OCTAVE]);
                canvas.drawText(s, margin / 2, 0, paint);
                float dx = paint.measureText(s);

                // Draw sharp/flat
                paint.setTextSize(small / 2);
                s = String.format(Locale.getDefault(), "%s",
                                  sharps[(audio.note - audio.transpose +
                                          OCTAVE) % OCTAVE]);
                canvas.drawText(s, margin / 2 + dx, paint.ascent(), paint);

                // Draw octave
                s = String.format(Locale.getDefault(), "%d",
                                  (audio.note - audio.transpose) / OCTAVE);
                canvas.drawText(s, margin / 2 + dx, 0, paint);

                // Draw cents

                paint.setTextSize(small);
                s = String.format(Locale.getDefault(),
                                  "%+5.2f\u00A2", audio.cents);
                x = width * 2 / 23;
                canvas.drawText(s, x, 0, paint);

                // Draw nearest

                s = String.format(Locale.getDefault(),
                                  "%4.2fHz", audio.nearest);
                x = width * 107 / 368;
                canvas.drawText(s, x, 0, paint);

                // Draw frequency
                s = String.format(Locale.getDefault(),
                                  "%4.2fHz", audio.frequency);
                x = width * 12 / 23;
                canvas.drawText(s, x, 0, paint);

                // Draw difference
                x = width - margin / 2;
                paint.setTextAlign(Paint.Align.RIGHT);
                s = String.format(Locale.getDefault(),
                                  "%+5.2fHz", audio.difference);
                canvas.drawText(s, x, 0, paint);
            }
        }

        // Not multiple
        else
        {
            String s;

            // Set up text
            paint.setTextSize(larger);
            paint.setTypeface(Typeface.DEFAULT_BOLD);

            // Move down
            canvas.translate(0, larger);

            // Draw note
            s = String.format(Locale.getDefault(), "%s",
                              notes[(audio.note - audio.transpose +
                                     OCTAVE) % OCTAVE]);
            canvas.drawText(s, margin, 0, paint);

            // Measure text
            float dx = paint.measureText(s);

            // Draw sharps/flats
            paint.setTextSize(larger / 2);
            s = String.format(Locale.getDefault(), "%s",
                              sharps[(audio.note - audio.transpose +
                                      OCTAVE) % OCTAVE]);
            canvas.translate(0, paint.ascent());
            canvas.drawText(s, margin + dx, 0, paint);

            // Draw octave
            s = String.format(Locale.getDefault(), "%d",
                              (audio.note - audio.transpose) / OCTAVE);
            canvas.translate(0, -paint.ascent());
            canvas.drawText(s, margin + dx, 0, paint);

            // Set up text
            paint.setTextSize(large);
            //	    paint.setTypeface(Typeface.DEFAULT);
            paint.setTextAlign(Paint.Align.RIGHT);

            // Draw cents

            s = String.format(Locale.getDefault(),
                              "%+5.2f\u00A2", audio.cents);
            // dx = paint.measureText(s);
            canvas.drawText(s, width - margin, 0, paint);

            // Set up text
            paint.setTextSize(medium);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTypeface(Typeface.DEFAULT);

            // Move down
            canvas.translate(0, medium);

            // Draw nearest
            s = String.format(Locale.getDefault(),
                              "%4.2fHz", audio.nearest);
            canvas.drawText(s, margin, 0, paint);

            // Draw frequency
            paint.setTextAlign(Paint.Align.RIGHT);
            s = String.format(Locale.getDefault(),
                              "%4.2fHz", audio.frequency);
            // dx = paint.measureText(s);
            canvas.drawText(s, width - margin, 0, paint);

            // Set up text
            paint.setTextSize(medium);
            paint.setTextAlign(Paint.Align.LEFT);

            // Move down
            canvas.translate(0, medium);

            // Draw reference
            s = String.format(Locale.getDefault(),
                              "%4.2fHz", audio.reference);
            canvas.drawText(s, margin, 0, paint);

            // Draw difference
            paint.setTextAlign(Paint.Align.RIGHT);
            s = String.format(Locale.getDefault(),
                              "%+5.2fHz", audio.difference);
            // dx = paint.measureText(s);
            canvas.drawText(s, width - margin, 0, paint);
        }
    }

    // Log2
    protected double log2(double d)
    {
        return Math.log(d) / Math.log(2.0);
    }
}
