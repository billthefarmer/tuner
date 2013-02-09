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

import org.billthefarmer.tuner.MainActivity.Audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;

// Class Display

public class Display extends TunerView
{
    protected Audio audio;

    int large;
    int medium;
    int small;
    
    int margin;
    int space;

    static final String notes[] =
    {"C", "C#", "D", "Eb", "E", "F",
     "F#", "G", "Ab", "A", "Bb", "B"};

    public Display(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);
	
	large = height / 3;
	medium = height / 4;
	small = height / 9;
	margin = width / 32;
	space = width / 48;

	paint.setTextSize(medium);
	float dx = paint.measureText("0000.00Hz");
	
	if (dx + (margin * 2) >= width / 2)
	{
	    float xscale = (width / 2) / (dx + (margin * 2));
	    paint.setTextScaleX(xscale);
	}
    }

    @SuppressLint("DefaultLocale")
    protected void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);

	paint.setColor(Color.BLACK);
	paint.setStrokeWidth(1);
	paint.setStyle(Style.FILL_AND_STROKE);

	if (audio == null)
	    return;

	if (audio.multiple)
	{
	    String s;
	    float x;

	    paint.setTextSize(small);
	    canvas.translate(0, width / 64);

	    for (int i = 0; i < audio.count; i++)
	    {
		canvas.translate(0, small);

		s = String.format("%s%d", notes[audio.maxima.n[i] % 12],
				  audio.maxima.n[i] / 12);
		canvas.drawText(s, margin, 0, paint);

		double cents = -12.0 * log2(audio.maxima.r[i] /
					    audio.maxima.f[i]) * 100.0;

		// Ignore silly values

		if (Double.isNaN(cents))
		    continue;

		s = String.format("%+5.2f¢", cents);
		x = width * 43 / 368;
		canvas.drawText(s, x, 0, paint);
				
		s = String.format("%4.2fHz", audio.maxima.r[i]);
		x = width * 107 / 368;
		canvas.drawText(s, x, 0, paint);

		s = String.format("%4.2fHz", audio.maxima.f[i]);
		x = width * 12 / 23;
		canvas.drawText(s, x, 0, paint);

		x = width * 139 / 184;
		s = String.format("%+5.2fHz", audio.maxima.r[i] -
				  audio.maxima.f[i]);
		canvas.drawText(s, x, 0, paint);
	    }

	    if (audio.count == 0)
	    {
		canvas.translate(0, small);

		s = String.format("%s%d", notes[audio.n % 12], audio.n / 12);
		canvas.drawText(s, margin, 0, paint);

		s = String.format("%+5.2f¢", audio.cents);
		x = width * 43 / 368;
		canvas.drawText(s, x, 0, paint);
				
		s = String.format("%4.2fHz", audio.nearest);
		x = width * 107 / 368;
		canvas.drawText(s, x, 0, paint);

		s = String.format("%4.2fHz", audio.frequency);
		x = width * 12 / 23;
		canvas.drawText(s, x, 0, paint);

		x = width * 139 / 184;
		s = String.format("%+5.2fHz", audio.difference);
		canvas.drawText(s, x, 0, paint);
	    }
	}

	else
	{
	    String s;

	    canvas.translate(0, large);
	    paint.setTextSize(large);
	    paint.setTypeface(Typeface.DEFAULT_BOLD);
	    canvas.drawText(notes[audio.n % 12], margin, 0, paint);

	    float dx = paint.measureText(notes[audio.n % 12]);

	    paint.setTextSize(medium);
	    s = Integer.toString(audio.n / 12);
	    canvas.drawText(s, margin + dx, 0, paint);

	    paint.setTextSize(large);
	    s = String.format("%+5.2f¢", audio.cents);
	    dx = paint.measureText(s);
	    canvas.drawText(s, width - dx - margin, 0, paint);

	    canvas.translate(0, medium);
	    paint.setTextSize(medium);
	    paint.setTypeface(Typeface.DEFAULT);
	    s = String.format("%4.2fHz", audio.nearest);
	    canvas.drawText(s, margin, 0, paint);

	    s = String.format("%4.2fHz", audio.frequency);
	    dx = paint.measureText(s);
	    canvas.drawText(s, width - dx - margin, 0, paint);

	    canvas.translate(0, medium);
	    paint.setTextSize(medium);
	    s = String.format("%4.2fHz", audio.reference);
	    canvas.drawText(s, margin, 0, paint);

	    s = String.format("%+5.2fHz", audio.difference);
	    dx = paint.measureText(s);
	    canvas.drawText(s, width - dx - margin, 0, paint);
	}
    }

	// Log2

	protected double log2(double d)
	{
	    return Math.log(d) / Math.log(2.0);
	}
}
