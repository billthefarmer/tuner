////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License along
//  with this program; if not, write to the Free Software Foundation, Inc.,
//  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.tuner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;

// Display

public class Display extends TunerView
{
    private static final int OCTAVE = 12;

    private int large;
    private int medium;
    private int small;
    
    private int margin;

    // Note values for display

    private static final String notes[] =
    {"C", "C#", "D", "Eb", "E", "F",
     "F#", "G", "Ab", "A", "Bb", "B"};

    // Constructor

    public Display(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On size changed

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);

	// Calculate text sizes
	
	large = height / 3;
	medium = height / 4;
	small = height / 9;
	margin = width / 32;

	// Make sure the text will fit the width

	paint.setTextSize(medium);
	float dx = paint.measureText("0000.00Hz");
	
	// Scale the text if it won't fit

	if (dx + (margin * 2) >= width / 2)
	{
	    float xscale = (width / 2) / (dx + (margin * 2));
	    paint.setTextScaleX(xscale);
	}
    }

    // On draw

    @SuppressLint("DefaultLocale")
    protected void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);

	// No display if no audio

	if (audio == null)
	    return;

	// Set up paint

	paint.setStrokeWidth(1);
	paint.setColor(Color.BLACK);
	paint.setTextAlign(Align.LEFT);
	paint.setStyle(Style.FILL);

	// Multiple values

	if (audio.multiple)
	{
	    String s;
	    float x;

	    // Set text size

	    paint.setTextSize(small);
	    canvas.translate(0, margin / 2);

	    for (int i = 0; i < audio.count; i++)
	    {
		// Move down

		canvas.translate(0, small);

		// Calculate cents

		double cents = -12.0 * log2(audio.maxima.r[i] /
					    audio.maxima.f[i]) * 100.0;
		// Ignore silly values

		if (Double.isNaN(cents))
		    continue;

		// Draw note

		s = String.format("%s%d", notes[audio.maxima.n[i] % OCTAVE],
				  audio.maxima.n[i] / OCTAVE);
		canvas.drawText(s, margin, 0, paint);

		// Draw cents

		s = String.format("%+5.2f¢", cents);
		x = width * 43 / 368;
		canvas.drawText(s, x, 0, paint);

		// Draw nearest

		s = String.format("%4.2fHz", audio.maxima.r[i]);
		x = width * 107 / 368;
		canvas.drawText(s, x, 0, paint);

		// Draw frequency

		s = String.format("%4.2fHz", audio.maxima.f[i]);
		x = width * 12 / 23;
		canvas.drawText(s, x, 0, paint);

		// Draw difference

		x = width * 139 / 184;
		s = String.format("%+5.2fHz", audio.maxima.r[i] -
				  audio.maxima.f[i]);
		canvas.drawText(s, x, 0, paint);
	    }

	    // If multiple and no data, don't have a blank display

	    if (audio.count == 0)
	    {
		// Move down

		canvas.translate(0, small);

		// Draw note

		s = String.format("%s%d", notes[audio.n % OCTAVE],
				  audio.n / OCTAVE);
		canvas.drawText(s, margin, 0, paint);

		// Draw cents

		s = String.format("%+5.2f¢", audio.cents);
		x = width * 43 / 368;
		canvas.drawText(s, x, 0, paint);
				
		// Draw nearest
	
		s = String.format("%4.2fHz", audio.nearest);
		x = width * 107 / 368;
		canvas.drawText(s, x, 0, paint);

		// Draw frequency

		s = String.format("%4.2fHz", audio.frequency);
		x = width * 12 / 23;
		canvas.drawText(s, x, 0, paint);

		// Draw difference

		x = width * 139 / 184;
		s = String.format("%+5.2fHz", audio.difference);
		canvas.drawText(s, x, 0, paint);
	    }
	}

	// Not multiple

	else
	{
	    String s;

	    // Move down

	    canvas.translate(0, large);

	    // Set up text

	    paint.setTextSize(large);
	    paint.setTypeface(Typeface.DEFAULT_BOLD);

	    // Draw note

	    canvas.drawText(notes[audio.n % OCTAVE], margin, 0, paint);

	    // Measure text

	    float dx = paint.measureText(notes[audio.n % OCTAVE]);

	    // Draw octave

	    paint.setTextSize(medium);
	    s = Integer.toString(audio.n / OCTAVE);
	    canvas.drawText(s, margin + dx, 0, paint);

	    // Set up text

	    paint.setTextSize(large);
	    paint.setTextAlign(Align.RIGHT);

	    // Draw cents

	    s = String.format("%+5.2f¢", audio.cents);
	    // dx = paint.measureText(s);
	    canvas.drawText(s, width - margin, 0, paint);

	    // Move down

	    canvas.translate(0, medium);

	    // Set up text

	    paint.setTextSize(medium);
	    paint.setTextAlign(Align.LEFT);
	    paint.setTypeface(Typeface.DEFAULT);

	    // Draw nearest

	    s = String.format("%4.2fHz", audio.nearest);
	    canvas.drawText(s, margin, 0, paint);

	    // Draw frequency

	    paint.setTextAlign(Align.RIGHT);
	    s = String.format("%4.2fHz", audio.frequency);
	    // dx = paint.measureText(s);
	    canvas.drawText(s, width - margin, 0, paint);

	    // Move down

	    canvas.translate(0, medium);

	    // Set up text

	    paint.setTextSize(medium);
	    paint.setTextAlign(Align.LEFT);

	    // Draw reference

	    s = String.format("%4.2fHz", audio.reference);
	    canvas.drawText(s, margin, 0, paint);

	    // Draw difference

	    paint.setTextAlign(Align.RIGHT);
	    s = String.format("%+5.2fHz", audio.difference);
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
