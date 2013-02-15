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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;

public class ColourPicker extends View
{
    private Paint mPaint;
    private Paint mCenterPaint;
    private final int[] mColours;

    private static final int CENTER_X = 100;
    private static final int CENTER_Y = 100;
    private static final int CENTER_RADIUS = 32;
    private static final int MARGIN_Y = 20;

    private boolean mTrackingCenter;
    private boolean mHighlightCenter;

    private int width;
    @SuppressWarnings("unused")
	private int height;
    private int offset;

    public ColourPicker(Context context)
    {
	super(context);

	mColours = new int[]
	    {0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
	     0xFFFFFF00, 0xFFFF0000};

	Shader s = new SweepGradient(0, 0, mColours, null);

	mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mPaint.setShader(s);
	mPaint.setStyle(Paint.Style.STROKE);
	mPaint.setStrokeWidth(32);

	mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mCenterPaint.setStrokeWidth(5);
    }

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), CENTER_Y * 2 + MARGIN_Y * 2);
    }

    // On size changed

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	width = w;
	height = h;

	offset = width / 2;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
	float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;

	canvas.translate(offset, CENTER_Y + MARGIN_Y);

	canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
	canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);

	if (mTrackingCenter)
	{
	    int c = mCenterPaint.getColor();
	    mCenterPaint.setStyle(Paint.Style.STROKE);

	    if (mHighlightCenter)
	    {
		mCenterPaint.setAlpha(0xFF);
	    }

	    else
	    {
		mCenterPaint.setAlpha(0x80);
	    }

	    canvas.drawCircle(0, 0,
			      CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
			      mCenterPaint);

	    mCenterPaint.setStyle(Paint.Style.FILL);
	    mCenterPaint.setColor(c);
	}
    }

    protected void setColour(int c)
    {
	mCenterPaint.setColor(c);
    }

    protected int getColour()
    {
	return mCenterPaint.getColor();
    }

    private int ave(int s, int d, float p)
    {
	return s + Math.round(p * (d - s));
    }

    private int interpColour(int colours[], float unit)
    {
	if (unit <= 0)
	{
	    return colours[0];
	}

	if (unit >= 1)
	{
	    return colours[colours.length - 1];
	}

	float p = unit * (colours.length - 1);
	int i = (int)p;
	p -= i;

	// now p is just the fractional part [0...1) and i is the index

	int c0 = colours[i];
	int c1 = colours[i+1];
	int a = ave(Color.alpha(c0), Color.alpha(c1), p);
	int r = ave(Color.red(c0), Color.red(c1), p);
	int g = ave(Color.green(c0), Color.green(c1), p);
	int b = ave(Color.blue(c0), Color.blue(c1), p);

	return Color.argb(a, r, g, b);
    }

    private static final float PI = 3.1415926f;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
	float x = event.getX() - offset;
	float y = event.getY() - CENTER_Y - MARGIN_Y;

	boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;

	switch (event.getAction())
	{
	case MotionEvent.ACTION_DOWN:
	    mTrackingCenter = inCenter;
	    if (inCenter)
	    {
		mHighlightCenter = true;
		invalidate();
		break;
	    }

	case MotionEvent.ACTION_MOVE:
	    if (mTrackingCenter)
	    {
		if (mHighlightCenter != inCenter) {
		    mHighlightCenter = inCenter;
		    invalidate();
		}
	    }

	    else
	    {
		float angle = (float)java.lang.Math.atan2(y, x);

		// need to turn angle [-PI ... PI] into unit [0....1]

		float unit = angle / (2 * PI);
		if (unit < 0)
		{
		    unit += 1;
		}

		mCenterPaint.setColor(interpColour(mColours, unit));
		invalidate();
	    }
	    break;

	case MotionEvent.ACTION_UP:
	    if (mTrackingCenter)
	    {
		if (inCenter)
		{
		    // mListener.colourChanged(mCenterPaint.getColor());
		}
		    
		mTrackingCenter = false;	// so we draw w/o halo
		invalidate();
	    }
	    break;
	}

	return true;
    }

}
