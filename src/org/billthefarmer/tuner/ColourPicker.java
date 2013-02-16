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
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;

// Colourpicker

public class ColourPicker extends View
{
    private Paint mPaint;
    private Paint mCentrePaint;
    private final int[] mColours;

    private static final int MARGIN = 8;

    private static final float PI = (float)Math.PI;

    private boolean mTrackingCentre;
    private int mCircleRadius;
    private int mStrokeWidth;
    private int mCentreRadius;
    private int mOffset;

    // Constructor

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

	mCentrePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mCentrePaint.setStrokeWidth(8);
    }

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	// Go for full width so we can be in the middle

	int w = MeasureSpec.getSize(widthMeasureSpec);

	// Derive the circle radius from the width

	int r = w / 6;

	// Derive the height from the circle radius and the margin

	setMeasuredDimension(w, r * 2 + MARGIN * 2);
    }

    // On size changed

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	// Calculate the dimensions based on the given values

	mCircleRadius = (h - (MARGIN * 2)) / 2;
	mStrokeWidth = w / 15;
	mCentreRadius = w / 18;
	mOffset = w / 2;

	mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
	float r = mCircleRadius - mStrokeWidth * 0.5f;

	canvas.translate(mOffset, mCircleRadius + MARGIN);

	canvas.drawCircle(0, 0, r, mPaint);
	canvas.drawCircle(0, 0, mCentreRadius, mCentrePaint);

	if (mTrackingCentre)
	{
	    int c = mCentrePaint.getColor();
	    mCentrePaint.setStyle(Paint.Style.STROKE);

	    canvas.drawCircle(0, 0,
			      mCentreRadius + mStrokeWidth,
			      mCentrePaint);

	    mCentrePaint.setStyle(Paint.Style.FILL);
	    mCentrePaint.setColor(c);
	}
    }

    protected void setColour(int c)
    {
	mCentrePaint.setColor(c);
    }

    protected int getColour()
    {
	return mCentrePaint.getColor();
    }

    private int avg(int s, int d, float p)
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
	int a = avg(Color.alpha(c0), Color.alpha(c1), p);
	int r = avg(Color.red(c0), Color.red(c1), p);
	int g = avg(Color.green(c0), Color.green(c1), p);
	int b = avg(Color.blue(c0), Color.blue(c1), p);

	return Color.argb(a, r, g, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
	float x = event.getX() - mOffset;
	float y = event.getY() - mCircleRadius - MARGIN;

	boolean inCentre = Math.sqrt(x*x + y*y) <= mCentreRadius;

	switch (event.getAction())
	{
	case MotionEvent.ACTION_DOWN:
	    mTrackingCentre = inCentre;
	    if (inCentre)
		break;

	case MotionEvent.ACTION_MOVE:
	    if (!mTrackingCentre)
	    {
		float angle = (float)Math.atan2(y, x);

		// need to turn angle [-PI ... PI] into unit [0....1]

		float unit = angle / (2 * PI);
		if (unit < 0)
		{
		    unit += 1;
		}

		mCentrePaint.setColor(interpColour(mColours, unit));
		invalidate();
	    }
	    break;

	case MotionEvent.ACTION_UP:
	    if (mTrackingCentre)
	    {
		mTrackingCentre = false;
		invalidate();
	    }
	    break;
	}

	return true;
    }
}
