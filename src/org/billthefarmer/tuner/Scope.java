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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.AttributeSet;

// Scope

public class Scope extends Graticule
{
    private Path path;
    private int max;

    // Constructor

    public Scope(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	path = new Path();
    }

    // Draw trace

    @Override
    protected void drawTrace(Canvas canvas)
    {

	// Check for data

	if (audio == null || audio.data == null)
	    return;

	// Draw F if filter

	if (audio.filter)
	{
	    // Color yellow

	    paint.setStrokeWidth(2);
	    paint.setAntiAlias(true);
	    paint.setColor(Color.YELLOW);

	    float height = paint.getFontMetrics(null);
	    canvas.drawText("F", 4, height - 2, paint);
	}

	// Initialise sync

	int maxdx = 0;
	int dx = 0;
	int n = 0;

	// Look for zero crossing

	for (int i = 1; i < audio.data.length; i++)
	{
	    dx = audio.data[i] - audio.data[i - 1];
	    if (maxdx < dx)
	    {
		maxdx = dx;
		n = i;
	    }

	    if (maxdx > 0 && dx < 0)
		break;
	}

	// Translate camvas

	canvas.translate(0, height / 2);

	// Check max value

	if (max < 4096)
	    max = 4096;

	// Calculate y scale

	float yscale = max / (height / 2);

	// Reset max value

	max = 0;

	// Calculate x scale

	float xscale = (float)Math.ceil((double)width / audio.data.length);

	// Rewind the path

	path.rewind();
	path.moveTo(0, 0);

	// Create the trace

	for (int i = 0; i < Math.min(width, audio.data.length - n); i++)
	{
	    // Get max value

	    if (max < Math.abs(audio.data[n + i]))
		max = Math.abs(audio.data[n + i]);

	    float y = -audio.data[n + i] / yscale;
	    float x = i * xscale;

	    path.lineTo(x, y);
	}

	// Color green

	paint.setStrokeWidth(2);
	paint.setAntiAlias(true);
	paint.setColor(Color.GREEN);

	// Draw trace

	canvas.drawPath(path, paint);
    }
}
