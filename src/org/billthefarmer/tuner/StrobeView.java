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
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;

public class StrobeView extends TunerView
{

    protected int foreground;
    protected int background;

    private Matrix matrix;

    private BitmapShader smallShader;
    private BitmapShader mediumShader;
    private BitmapShader largeShader;
    private BitmapShader largerShader;

    private int size;
    private double scale;
    private double offset;
    private double cents;

    private static final int DELAY = 40;

    private static final int WIDTH = 128;
    private static final int HEIGHT = 128;

    public StrobeView(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	foreground = Color.BLUE;
	background = Color.CYAN;
    }

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	setMeasuredDimension(WIDTH, HEIGHT);
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

	// Do inertia calculation

	cents = (cents * 19.0 + 10.0) / 20.0;

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

	smallShader.setLocalMatrix(matrix);
	paint.setShader(smallShader);
	canvas.drawRect(0, 0, width, size, paint);

	mediumShader.setLocalMatrix(matrix);
	paint.setShader(mediumShader);
	canvas.drawRect(0, size, width, size * 2, paint);

	largeShader.setLocalMatrix(matrix);
	paint.setShader(largeShader);
	canvas.drawRect(0, size * 2, width, size * 3, paint);

	largerShader.setLocalMatrix(matrix);
	paint.setShader(largerShader);
	canvas.drawRect(0, size * 3, width, size * 4, paint);

	paint.setShader(null);
    }
}
