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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

// Tuner View

public class TunerView extends View
{
    int width;
    int height;
    Paint paint;
    Rect clipRect;
    RectF outlineRect;

    // Constructor

    protected TunerView(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	paint = new Paint();
    }

    // On Size Changed

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	// Save the new width and height

	width = w;
	height = h;

	// Create some rects for
	// the outline and clipping

	outlineRect = new RectF(1, 1, width - 1, height - 1);
	clipRect = new Rect(4, 4, width - 4, height - 4);
    }

    // On Draw

    protected void onDraw(Canvas canvas)
    {
	// Set up the paint and draw the outline

	paint.setStrokeWidth(3);
	paint.setAntiAlias(true);
	paint.setColor(Color.GRAY);
	paint.setStyle(Paint.Style.STROKE);
	canvas.drawRoundRect(outlineRect, 10, 10, paint);

	// Set the cliprect

	canvas.clipRect(clipRect);
    }
}
