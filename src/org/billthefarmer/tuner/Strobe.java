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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;

public class Strobe extends TunerView
{
    protected Audio audio;

    private Matrix matrix;

    private BitmapShader smallShader;
    private BitmapShader mediumShader;
    private BitmapShader largeShader;
    private BitmapShader largerShader;
    private LinearGradient smallGradient;
    private LinearGradient mediumGradient;
    private LinearGradient largeGradient;
    private LinearGradient smallGreyGradient;

    private int size;
    private double scale;
    private double offset;
    private double cents;

    private static final int DELAY = 40;

    // Constructor

    public Strobe(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On size changed

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);

	size = height / 4;
	scale = width / 500.0;

	matrix = new Matrix();

	smallShader = new BitmapShader(createShaderBitmap(size, size),
				       TileMode.REPEAT, TileMode.CLAMP);
	mediumShader = new BitmapShader(createShaderBitmap(size * 2, size),
					TileMode.REPEAT, TileMode.CLAMP);
	largeShader = new BitmapShader(createShaderBitmap(size * 4, size),
				       TileMode.REPEAT, TileMode.CLAMP);
	largerShader = new BitmapShader(createShaderBitmap(size * 8, size),
					TileMode.REPEAT, TileMode.CLAMP);

	smallGradient = new LinearGradient(0, 0, size, 0,
					   Color.CYAN, Color.BLUE,
					   TileMode.MIRROR);
	mediumGradient = new LinearGradient(0, 0, size * 2, 0,
					    Color.CYAN, Color.BLUE,
					    TileMode.MIRROR);
	largeGradient = new LinearGradient(0, 0, size * 4, 0,
					   Color.CYAN, Color.BLUE,
					   TileMode.MIRROR);
	smallGreyGradient = new LinearGradient(0, 0, size, 0,
					       Color.CYAN, Color.rgb(0, 127, 255),
					       TileMode.MIRROR);
    }

    // Create shader bitmap

    private Bitmap createShaderBitmap(int width, int height)
    {
	Bitmap bitmap = Bitmap.createBitmap(width * 2, height, Config.RGB_565);
	Canvas canvas = new Canvas(bitmap);
	Paint paint = new Paint();

	int colour = getResources().getColor(android.R.color.background_light);
	canvas.drawColor(Color.CYAN);
	colour = getResources().getColor(android.R.color.primary_text_light);
	paint.setColor(Color.BLUE);
	canvas.drawRect(0, 0, width, height, paint);

	return bitmap;
    }

    // On draw

    protected void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);

	// Post invalidate after delay

	postInvalidateDelayed(DELAY);

	// Don't draw if turned off

	if (audio == null || !audio.strobe)
	    return;

	// Do inertia calculation

	if (audio != null)
	    cents = (cents * 19.0 + audio.cents) / 20.0;

	// Calculate offset

	offset = offset + (cents * scale);

	if (offset > size * 16)
	    offset = 0.0;

	if (offset < 0.0)
	    offset = size * 16;

	// Reset the paint to black

	paint.setStrokeWidth(1);
	paint.setAntiAlias(true);
	paint.setColor(Color.BLACK);
	paint.setStyle(Style.FILL);

	// Translate

	matrix.setTranslate((float)offset, 0);

	// Draw the strobe chequers

	if (Math.abs(cents) < 25.0)
	{
	    smallShader.setLocalMatrix(matrix);
	    paint.setShader(smallShader);
	    canvas.drawRect(0, 0, width, size, paint);
	}

	else if (Math.abs(cents) < 35.0)
	{
	    smallGradient.setLocalMatrix(matrix);
	    paint.setShader(smallGradient);
	    canvas.drawRect(0, 0, width, size, paint);
	}

	else

	{
	    smallGreyGradient.setLocalMatrix(matrix);
	    paint.setShader(smallGreyGradient);
	    canvas.drawRect(0, 0, width, size, paint);
	}

	if (Math.abs(cents) < 35.0)
	{
	    mediumShader.setLocalMatrix(matrix);
	    paint.setShader(mediumShader);
	    canvas.drawRect(0, size, width, size * 2, paint);
	}

	else
	{
	    mediumGradient.setLocalMatrix(matrix);
	    paint.setShader(mediumGradient);
	    canvas.drawRect(0, size, width, size * 2, paint);
	}

	if (Math.abs(cents) < 45)
	{
	    largeShader.setLocalMatrix(matrix);
	    paint.setShader(largeShader);
	    canvas.drawRect(0, size * 2, width, size * 3, paint);
	}

	else
	{
	    largeGradient.setLocalMatrix(matrix);
	    paint.setShader(largeGradient);
	    canvas.drawRect(0, size * 2, width, size * 3, paint);
	}

	largerShader.setLocalMatrix(matrix);
	paint.setShader(largerShader);
	canvas.drawRect(0, size * 3, width, size * 4, paint);

	paint.setShader(null);
    }
}
