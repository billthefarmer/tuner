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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

// Graticule

public class Graticule extends TunerView
{
    // Contructor

    protected Graticule(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On Size Changed

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);

	// Calculate indented width and height

	width = (int)(clipRect.right - clipRect.left);
	height = (int)(clipRect.bottom - clipRect.top);
    }

    // On Draw

    protected void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);

	// Draw black rectangle

	canvas.drawColor(Color.BLACK);

	// Set up paint for dark green thin lines

	paint.setAntiAlias(false);
	paint.setStrokeWidth(1);
	paint.setColor(0xff007f00);

	canvas.translate(clipRect.left, clipRect.top);

	// Draw the graticule

	for (int i = (width % 10) / 2; i <= width; i += 10)
	    canvas.drawLine(i, 0, i, height, paint);

	for (int i = (height % 10) / 2; i <= height; i +=10)
	    canvas.drawLine(0, i, width, i, paint);
    }
}
