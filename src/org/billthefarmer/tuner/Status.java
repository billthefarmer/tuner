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

import org.billthefarmer.tuner.MainActivity.Audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

// Status

public class Status extends View
{
    protected Audio audio;

    private int width;
    private int height;
    private int margin;

    private Paint paint;
    private Resources resources;

    // Constructor

    public Status(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	paint = new Paint();
	resources = getResources();
    }

    // On size changed

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	width = w;
	height = h;

	margin = width / 32;
    }

    // On draw

    @Override
    @SuppressLint("DefaultLocale")
    protected void onDraw(Canvas canvas)
    {
	String s;

	// Draw separator line

	paint.setStrokeWidth(3);
	paint.setColor(resources.getColor(android.R.color.darker_gray));
	paint.setAntiAlias(true);
	paint.setStyle(Paint.Style.STROKE);
	canvas.drawLine(0, 0, width, 0, paint);

	// Check for audio

	if (audio == null)
	    return;

	// Set up text

	paint.setStrokeWidth(1);
	paint.setColor(resources.getColor(android.R.color.primary_text_light));
	paint.setTextSize(height / 2);
	paint.setStyle(Paint.Style.FILL);

	// Move down

	canvas.translate(0, height * 2 / 3);

	// Draw sample rate text

	s = String.format(resources.getString(R.string.sample_rate),
			  audio.sample);
	canvas.drawText(s, margin, 0, paint);
	float x = margin + paint.measureText(s + "   ");

	// Filter

	if (audio.filter)
	{
	    s = resources.getString(R.string.filter);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	// Downsample

	if (audio.downsample)
	{
	    s = resources.getString(R.string.downsample);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	// Zoom

	if (audio.zoom)
	{
	    s = resources.getString(R.string.zoom);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	// Lock

	if (audio.lock)
	{
	    s = resources.getString(R.string.lock);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	// Multiple

	if (audio.multiple)
	{
	    s = resources.getString(R.string.multiple);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	// Screen

	if (audio.screen)
	{
	    s = resources.getString(R.string.display);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	// Strobe

	if (audio.strobe)
	{
	    s = resources.getString(R.string.strobe);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

    }
}
