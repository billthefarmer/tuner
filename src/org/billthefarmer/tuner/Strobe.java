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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;

public class Strobe extends TunerView
{
    Audio audio;
    Handler handler;
    Runnable run;

    float size;
    float scale;
    float offset;
    double cents;

    private static final int DELAY = 100;

    // Constructor

    public Strobe(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	handler = new Handler();
	run = new Runnable()
	    {
		@Override
		public void run()
		{
		    invalidate();
		    handler.postDelayed(this, DELAY);
		}
	    };

	handler.postDelayed(run, DELAY);
    }

    // OnSizeChanged

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);

	size = height / 4;

	scale = width / 500;
    }

    // On draw

    protected void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);

	if (!audio.strobe)
		return;

	if (audio != null)
	    cents = (cents * 7.0 + audio.cents) / 8.0;

	offset = (float)(offset + (cents * scale));

	if (offset > size * 16)
		offset = 0.0f;

	if (offset < 0.0f)
		offset = size * 16;

	// Reset the paint to black

	paint.setStrokeWidth(1);
	paint.setColor(Color.BLACK);
	paint.setStyle(Style.FILL);

	float y = 0;
	float x = offset - size * 16;
	for (int i = 0; i <= width / size * 2; i++)
	{
	    canvas.drawRect(x, y, x + size, y + size, paint);
	    x += size * 2;
	}

	y += size;
	x = offset - size * 16;
	for (int i = 0; i <= width / size * 4; i++)
	{
	    canvas.drawRect(x, y, x + size * 2, y + size, paint);
	    x += size * 4;
	}

	y += size;
	x = offset - size * 16;
	for (int i = 0; i <= width / size * 8; i++)
	{
	    canvas.drawRect(x, y, x + size * 4, y + size, paint);
	    x += size * 8;
	}

	y += size;
	x = offset - size * 16;
	for (int i = 0; i <= width / size * 16; i++)
	{
	    canvas.drawRect(x, y, x + size * 8, y + size, paint);
	    x += size * 16;
	}

    }
}
