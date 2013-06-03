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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;

// Spectrum

public class Spectrum extends Graticule
{
    private Path path;

    private float max;

    // Constructor

    public Spectrum(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	path = new Path();
    }

    // Draw trace

    @Override
    @SuppressLint("DefaultLocale")
    protected void drawTrace(Canvas canvas)
    {

	// Check for data

	if (audio == null || audio.xa == null)
	    return;

	// Draw D if downsample

	if (audio.downsample)
	{
	    // Color yellow

	    paint.setStrokeWidth(1);
	    paint.setTextAlign(Align.LEFT);
	    paint.setColor(Color.YELLOW);

	    float height = paint.getFontMetrics(null);
	    canvas.drawText("D", 4, height - 2, paint);
	}

	// Translate camvas

	canvas.translate(0, height);

	// Chack max value

	if (max < 1.0f)
	    max = 1.0f;

	// Calculate the scaling

	float yscale = (height / max);

	max = 0.0f;

	// Rewind path

	path.rewind();
	path.moveTo(0, 0);

	// If zoomed

	if (audio.zoom)
	{
	    // Calculate limits

	    double lower = audio.lower / audio.fps;
	    double higher = audio.higher / audio.fps;
	    double nearest = audio.nearest / audio.fps;
	    
	    // Calculate scale

	    float xscale = (float)((width / (nearest - lower)) / 2.0);

	    int lo = (int)Math.floor(lower);
	    int hi = (int)Math.ceil(higher);

	    // Create trace

	    for (int i = lo; i <= hi; i++)
	    {
		if (i > 0 && i < audio.xa.length)
		{
		    float value = (float)audio.xa[i];

		    // Get max value

		    if (max < value)
			max = value;

		    float y = -value * yscale;
		    float x = (float)((i - lower) * xscale); 

		    path.lineTo(x, y);
		    path.addCircle(x, y, 1, Direction.CW);
		}
	    }

	    // Create centre line

	    path.moveTo(width / 2, 0);
	    path.lineTo(width / 2, -height);

	    // Color green

	    paint.setStrokeWidth(2);
	    paint.setAntiAlias(true);
	    paint.setColor(Color.GREEN);

	    // Draw trace

	    canvas.drawPath(path, paint);
	    path.rewind();

	    // Yellow pen for frequency trace

	    paint.setTextAlign(Align.CENTER);
	    paint.setColor(Color.YELLOW);
	    paint.setStyle(Style.FILL);
	    paint.setAntiAlias(false);
	    paint.setStrokeWidth(1);

	    // Create lines for each frequency

	    for (int i = 0; i < audio.count; i++)
	    {
		// Draw line for each that are in range

		if (audio.maxima.f[i] > audio.lower &&
		    audio.maxima.f[i] < audio.higher)
		{
		    float x =
			(float)((audio.maxima.f[i] - audio.lower) /
				audio.fps * xscale);

		    path.moveTo(x, 0);
		    path.lineTo(x, -height);

		    double f = audio.maxima.f[i];

		    // Reference freq

		    double fr = audio.maxima.r[i];
		    double c = -12.0 * log2(fr / f);

		    // Ignore silly values

		    if (Double.isNaN(c))
			continue;

		    // Draw cents value

		    String s = String.format("%+1.0f", c * 100.0);
		    canvas.drawText(s, x, 0, paint);
		}
	    }

	    // Yellow pen for frequency trace

	    paint.setStyle(Style.STROKE);
	    paint.setAntiAlias(true);
	    paint.setStrokeWidth(2);

	    // Draw path

	    canvas.drawPath(path, paint);
	}

	// Not zoomed

	else
	{
	    // Calculate x scale

	    float xscale = (float)audio.xa.length / (float)width;

	    // Create trace

	    for (int x = 0; x < width; x++)
	    {
		float value = 0.0f;

		// Don't show DC component

		if (x > 0)
		{
		    // Find max value for each vertex

		    for (int j = 0; j < xscale; j++)
		    {
			int n = (int)(x * xscale) + j;

			if (value < audio.xa[n])
			    value = (float)audio.xa[n];
		    }
		}

		// Get max value

		if (max < value)
		    max = value;

		float y = -value * yscale;

		path.lineTo(x, y);
	    }

	    // Color green

	    paint.setStrokeWidth(2);
	    paint.setAntiAlias(true);
	    paint.setColor(Color.GREEN);

	    // Draw path

	    canvas.drawPath(path, paint);
	    path.rewind();

	    // Yellow pen for frequency trace

	    paint.setTextAlign(Align.CENTER);
	    paint.setColor(Color.YELLOW);
	    paint.setStyle(Style.FILL);
	    paint.setAntiAlias(false);
	    paint.setStrokeWidth(1);

	    // Create lines for each frequency

	    for (int i = 0; i < audio.count; i++)
	    {
		// Draw line for each

		float x =
		    (float)(audio.maxima.f[i] / audio.fps / xscale);

		path.moveTo(x, 0);
		path.lineTo(x, -height);

		double f = audio.maxima.f[i];

		// Reference freq

		double fr = audio.maxima.r[i];
		double c = -12.0 * log2(fr / f);

		// Ignore silly values

		if (Double.isNaN(c))
		    continue;

		// Draw cents value

		String s = String.format("%+1.0f", c * 100.0);
		canvas.drawText(s, x, 0, paint);
	    }

	    // Yellow pen for frequency trace

	    paint.setStyle(Style.STROKE);
	    paint.setAntiAlias(true);
	    paint.setStrokeWidth(2);

	    // Draw path

	    canvas.drawPath(path, paint);
	}
    }

    // Log2

    protected double log2(double d)
    {
	return Math.log(d) / Math.log(2.0);
    }
}
