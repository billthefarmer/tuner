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
import android.graphics.Matrix;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;

// Meter

public class Meter extends TunerView
{
    Audio audio;

    Handler handler;
    Runnable run;

    Matrix matrix;
    Rect barRect;
    Path path;

    private float cents;
    private float medium;

    private static final int DELAY = 100;

    // Constructor

    public Meter(Context context, AttributeSet attrs)
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

	// Create a path for the thumb

	path = new Path();

	path.moveTo(0, -1);
	path.lineTo(1, 0);
	path.lineTo(1, 1);
	path.lineTo(-1, 1);
	path.lineTo(-1, 0);
	path.close();

	matrix = new Matrix();
    }

    // OnSizeChanged

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);

	// Recalculate text size

	medium = height / 3.0f;
	paint.setTextSize(medium);

	// Scale text if necessary to fit it in

	float dx = paint.measureText("50");
	if (dx >= width / 11)
	    paint.setTextScaleX((width / 12) / dx);

	// Create a rect for the horizoltal bar

	barRect = new Rect(width / 36 - width / 2, -height / 64,
			   width / 2 - width / 36, height / 64);

	// Create a matrix to scale the path,
	// a bit narrower than the height

	matrix.setScale(height / 24, height / 8);

	// Scale the path

	path.transform(matrix);
    }

    // OnDraw

    @SuppressLint("DefaultLocale")
    protected void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);

	// Reset the paint to black

	paint.setColor(Color.BLACK);
	paint.setStrokeWidth(1);
	paint.setStyle(Style.FILL_AND_STROKE);

	// Translate the canvas down
	// and to the centre

	canvas.translate(width / 2, medium);

	// Calculate x scale

	float xscale = width / 11;

	// Draw the scale legend

	for (int i = 0; i <= 5; i++)
	{
	    String s = String.format("%d", i * 10);
	    float x = i * xscale;

	    paint.setTextAlign(Align.CENTER);
	    canvas.drawText(s, x, 0, paint);
	    canvas.drawText(s, -x, 0, paint);
	}

	// Wider lines for the scale

	paint.setStrokeWidth(3);
	paint.setStyle(Style.STROKE);
	canvas.translate(0, medium / 1.5f);

	// Draw the scale

	for (int i = 0; i <= 5; i++)
	{
	    float x = i * xscale;

	    canvas.drawLine(x, 0, x, -medium / 2, paint);
	    canvas.drawLine(-x, 0, -x, -medium / 2, paint);
	}

	// Draw the fine scale

	for (int i = 0; i <= 25; i++)
	{
	    float x = i * xscale / 5;

	    canvas.drawLine(x, 0, x, -medium / 4, paint);
	    canvas.drawLine(-x, 0, -x, -medium / 4, paint);
	}

	// Transform the canvas down
	// for the meter pointer

	canvas.translate(0, medium / 2.0f);

	// Set the paint colour to grey

	paint.setColor(Color.GRAY);

	// Draw the bar outline

	paint.setStyle(Style.STROKE);
	paint.setStrokeWidth(2);
	canvas.drawRect(barRect, paint);

	// Do the inertia calculation

	if (audio != null)
	    cents = (float)(((cents * 9.0) + audio.cents) / 10.0);

	// Translate the canvas to
	// the scaled cents value

	canvas.translate(cents * (xscale / 10), -height / 64);

	// Set up the paint for
	// rounded corners

	paint.setStrokeCap(Cap.ROUND);
	paint.setStrokeJoin(Join.ROUND);
	
	// Set fill style and fill
	// the thumb

	paint.setColor(Color.WHITE);
	paint.setStyle(Style.FILL);
	canvas.drawPath(path, paint);

	paint.setStrokeWidth(3);
	paint.setColor(Color.BLACK);
	paint.setStyle(Style.STROKE);
	canvas.drawPath(path, paint);
    }
}
