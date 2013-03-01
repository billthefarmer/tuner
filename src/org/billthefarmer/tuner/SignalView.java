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

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

// Signal view

public class SignalView extends View
{
    Audio audio;

    private int width;
    private int height;
    private int hMargin;
    private int vMargin;

    private double signal;

    private ValueAnimator animator;
    private BitmapShader shader;
    private Paint paint;
    private RectF rect;

    private static final float YSCALE = (float)Math.log(32768);
    private static final float THRESHOLD = (float)Math.pow(Math.E, 6);

    // Constructor

    public SignalView(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	// Get offered height

	int h = MeasureSpec.getSize(heightMeasureSpec);

	// Set size to offered height

	setMeasuredDimension(h, h);
    }

    // On size changed

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	// Get dimensions

	width = w;
	height = h;

	hMargin = width / 3;
	vMargin = height / 8;


	// Colours for gradient

	int colours[] =
	    {Color.RED, Color.YELLOW, Color.GREEN, Color.BLACK};

	// Coloured gradient

	LinearGradient gradient =
	    new LinearGradient(0, 0, 0, height,
			       colours, null, TileMode.CLAMP);

	// Bitmap to draw coloured bars in

	Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	Canvas canvas = new Canvas(bitmap);
	paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	paint.setShader(gradient);
	paint.setStrokeWidth(2);

	// Draw coloured bars

	for (int i = 0; i <= bitmap.getHeight(); i += 3)
	    canvas.drawLine(0, i, bitmap.getWidth(), i, paint);

	// Create shader from coloured bars

	shader = new BitmapShader(bitmap,
				  TileMode.CLAMP, TileMode.CLAMP);

	// Rect to draw the coloured bars

	rect = new RectF(0, 0, width / 3, height * 3 / 4);

	// Create animator

	animator = ValueAnimator.ofInt(0, 10000);
	animator.setRepeatCount(ValueAnimator.INFINITE);
	animator.setRepeatMode(ValueAnimator.RESTART);
	animator.setDuration(10000);

	// Update the display

	animator.addUpdateListener(new AnimatorUpdateListener()
	    {
		public void onAnimationUpdate(ValueAnimator animator)
		{
		    invalidate();
		}
	    });

	// Start the animator

	animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
	// Do VU meter style calculation

	if (signal < audio.signal)
	    signal = ((signal * 4) + audio.signal) / 5;

	else
	    signal = ((signal * 9) + audio.signal) / 10;

	// Draw the coloured column

	paint.setShader(shader);
	paint.setStyle(Style.FILL);
	canvas.translate(hMargin, vMargin);
	int max = height * 3 / 4;
	float v = (float)(Math.log(signal - THRESHOLD) / YSCALE);
	rect.top = max - max * v;
	canvas.drawRoundRect(rect, 3, 3, paint);

	// Show dead audio

	if (audio.thread == null)
	{
	    paint.setShader(null);
	    paint.setColor(Color.RED);
	    paint.setStyle(Style.STROKE);
	    paint.setStrokeWidth(7);
	    paint.setStrokeCap(Cap.ROUND);
	    canvas.translate(-hMargin, -vMargin);
	    canvas.drawLine(width / 5, height / 5,
			    width * 4 / 5, height * 4 / 5, paint);
	    canvas.drawLine(width * 4 / 5, height / 5,
			    width / 5, height * 4 / 5, paint);
	}
    }
}
