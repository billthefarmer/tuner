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
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Status extends View
{
    Audio audio;

    int width;
    int height;
    int margin;

    Paint paint;

    public Status(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	paint = new Paint();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	width = w;
	height = h;

	margin = width / 32;
    }

    @SuppressLint("DefaultLocale")
    protected void onDraw(Canvas canvas)
    {
	String s;

	paint.setStrokeWidth(3);
	paint.setColor(Color.GRAY);
	paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	paint.setStyle(Paint.Style.STROKE);
	canvas.drawLine(0, 0, width, 0, paint);

	if (audio == null)
	    return;

	paint.setStrokeWidth(1);
	paint.setColor(Color.BLACK);
	paint.setTextSize(height / 2);
	paint.setStyle(Paint.Style.FILL_AND_STROKE);

	canvas.translate(0, height * 2 / 3);

	s = String.format("Sample rate: %1.0f", audio.sample);
	canvas.drawText(s, margin, 0, paint);

	float x = margin + paint.measureText(s + "  ");
	if (audio.filter)
	{
	    s = getResources().getString(R.string.filter);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	if (audio.downsample)
	{
	    s = getResources().getString(R.string.downsample);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	if (audio.zoom)
	{
	    s = getResources().getString(R.string.zoom);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	if (audio.lock)
	{
	    s = getResources().getString(R.string.lock);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

	if (audio.strobe)
	{
	    s = getResources().getString(R.string.strobe);
	    canvas.drawText(s, x, 0, paint);
	    x += paint.measureText(s + " ");
	}

    }
}
