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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;

// Graticule

public abstract class Graticule extends TunerView
{
    private static final int SIZE = 10;

    private Canvas source;
    private Bitmap bitmap;
    private Bitmap rounded;
    private Paint xferPaint;

    // Constructor

    protected Graticule(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On Size Changed

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);

	// Calculate indented width and height

	width = (clipRect.right - clipRect.left);
	height = (clipRect.bottom - clipRect.top);

	// Create rounded bitmap

	rounded = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	Canvas canvas = new Canvas(rounded);	
	paint.setStyle(Style.FILL);
	paint.setColor(Color.WHITE);
	canvas.drawRoundRect(new RectF(0, 0, width, height), 10, 10, paint);	

	// Create magic paint

	xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	xferPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

	// Create a bitmap to draw on

	bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	source = new Canvas(bitmap);
    }

    // On Draw

    @Override
    protected void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);

	// Reset the canvas transform

	source.setMatrix(null);

	// Draw on the canvas

	drawGraticule(source);
	drawTrace(source);

	// Reset the canvas transform again

	source.setMatrix(null);

	// Use the magic paint

	source.drawBitmap(rounded, 0, 0, xferPaint);

	// Draw the result on the canvas

	canvas.drawBitmap(bitmap, 0, 0, null);
    }

    // Draw graticule

    private void drawGraticule(Canvas canvas)
    {
	// Draw black rectangle

	canvas.drawColor(Color.BLACK);

	// Set up paint for dark green thin lines

	paint.setAntiAlias(false);
	paint.setStyle(Style.STROKE);
	paint.setColor(0xff007f00);
	paint.setStrokeWidth(1);

	// Draw the graticule

	for (int i = (width % SIZE) / 2; i <= width; i += SIZE)
	    canvas.drawLine(i, 0, i, height, paint);

	for (int i = (height % SIZE) / 2; i <= height; i += SIZE)
	    canvas.drawLine(0, i, width, i, paint);
    }

    // Draw trace

    protected abstract void drawTrace(Canvas canvas);
}
