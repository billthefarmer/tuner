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
    protected int colour;
    protected int foreground;
    protected int background;

    private final static int colours[][] =
    {{Color.argb(191, 0, 0, 255), Color.argb(191, 0, 255, 255)},
     {Color.argb(191, 0, 255, 0), Color.argb(191, 191, 255, 0)},
     {Color.argb(191, 255, 0, 0), Color.argb(191, 255, 191, 0)},
     {Color.argb(191, 0, 0, 255), Color.argb(191, 0, 255, 255)}};

    private static final int CUSTOM = 3;
    private static final float SLOW = 20;
    private static final float MEDIUM = 30;
    private static final float FAST = 40;

    private Matrix matrix;

    private BitmapShader smallShader;
    private BitmapShader mediumShader;
    private BitmapShader largeShader;
    private BitmapShader largerShader;
    private LinearGradient smallGradient;
    private LinearGradient mediumGradient;
    private LinearGradient largeGradient;
    private LinearGradient smallBlurGradient;

    private int size;
    private double scale;
    private double offset;
    private double cents;

    protected static final int DELAY = 40;

    // Constructor

    public Strobe(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On size changed

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	super.onSizeChanged(w, h, oldw, oldh);

	// Calculate size and scale

	size = height / 4;
	scale = width / 500.0;

	// Create matrix for translating shaders

	matrix = new Matrix();

	// Create the shaders

	createShaders();
    }

    // Create shaders

    protected void createShaders()
    {
	int foreground;
	int background;

	// Get the colours

	if (colour < CUSTOM)
	{
	    foreground = colours[colour][0];
	    background = colours[colour][1];
	}

	else
	{
	    foreground = this.foreground;
	    background = this.background;
	}

	// Create the bitmap shaders

	smallShader =
	    new BitmapShader(createShaderBitmap(size,
						size, foreground, background),
			     TileMode.REPEAT, TileMode.CLAMP);
	mediumShader =
	    new BitmapShader(createShaderBitmap(size * 2,
						size, foreground, background),
			     TileMode.REPEAT, TileMode.CLAMP);
	largeShader =
	    new BitmapShader(createShaderBitmap(size * 4,
						size, foreground, background),
			     TileMode.REPEAT, TileMode.CLAMP);
	largerShader =
	    new BitmapShader(createShaderBitmap(size * 8,
						size, foreground, background),
			     TileMode.REPEAT, TileMode.CLAMP);

	// Create the gradients

	smallGradient = new LinearGradient(0, 0, size, 0,
					   background, foreground,
					   TileMode.MIRROR);
	mediumGradient = new LinearGradient(0, 0, size * 2, 0,
					    background, foreground,
					    TileMode.MIRROR);
	largeGradient = new LinearGradient(0, 0, size * 4, 0,
					   background, foreground,
					   TileMode.MIRROR);

	// Calculate intermediate colours for the small gradient

	int red = (Color.red(foreground) + Color.red(background)) / 2;
	int green = (Color.green(foreground) + Color.green(background)) / 2;
	int blue = (Color.blue(foreground) + Color.blue(background)) / 2;

	smallBlurGradient =
	    new LinearGradient(0, 0, size, 0,
			       background,
			       Color.argb(191, red, green, blue),
			       TileMode.MIRROR);
    }

    // Create shader bitmap

    private Bitmap createShaderBitmap(int width, int height, int f, int b)
    {
	// Create bitmap twice as wide as the block

	Bitmap bitmap = Bitmap.createBitmap(width * 2, height, Config.ARGB_8888);
	Canvas canvas = new Canvas(bitmap);
	Paint paint = new Paint();

	// Draw the bitmap

	canvas.drawColor(b);
	paint.setColor(f);
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

	// Reset the paint

	paint.setStrokeWidth(1);
	paint.setAntiAlias(true);
	paint.setStyle(Style.FILL);

	// Translate

	matrix.setTranslate((float)offset, 0);

	// Draw the strobe chequers

	if (Math.abs(cents) < SLOW)
	{
	    smallShader.setLocalMatrix(matrix);
	    paint.setShader(smallShader);
	    canvas.drawRect(0, 0, width, size, paint);
	}

	else if (Math.abs(cents) < MEDIUM)
	{
	    smallGradient.setLocalMatrix(matrix);
	    paint.setShader(smallGradient);
	    canvas.drawRect(0, 0, width, size, paint);
	}

	else if (Math.abs(cents) < FAST)
	{
	    smallBlurGradient.setLocalMatrix(matrix);
	    paint.setShader(smallBlurGradient);
	    canvas.drawRect(0, 0, width, size, paint);
	}

	else
	{

	    paint.setShader(null);
	    paint.setColor(colours[colour][1]);
	    canvas.drawRect(0, 0, width, size, paint);
	}

	if (Math.abs(cents) < MEDIUM)
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

	if (Math.abs(cents) < FAST)
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
