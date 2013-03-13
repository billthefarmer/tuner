////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;

// Colour picker

public class ColourPicker extends PreferenceView
{
    private Paint mCirclePaint;
    private Paint mCentrePaint;
    private final static int[] mColours =
    {Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN,
     Color.GREEN, Color.YELLOW, Color.RED};

    private boolean mTrackingCentre;
    private int mCircleRadius;
    private int mStrokeWidth;
    private int mCentreRadius;
    private int mOffset;

    private float hsv[] = {0, 1, 1};

    // Colour change listener

    private ColourChangeListener listener;

    protected interface ColourChangeListener
    {
	void onColourChanged(int colour);
    }

    // Constructor

    public ColourPicker(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	Shader shader = new SweepGradient(0, 0, mColours, null);

	mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mCirclePaint.setShader(shader);
	mCirclePaint.setStyle(Paint.Style.STROKE);

	mCentrePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mCentrePaint.setStrokeWidth(8);
    }

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	// Get the max width from the superclass

	setMeasuredDimension(maxWidth / 3, maxWidth / 3);
    }

    // On size changed

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	// Calculate the dimensions based on the given values

	mCircleRadius = h / 2;
	mStrokeWidth = w / 5;
	mCentreRadius = w / 6;
	mOffset = w / 2;

	mCirclePaint.setStrokeWidth(mStrokeWidth);
    }

    // On draw

    @Override
    protected void onDraw(Canvas canvas)
    {
	float r = mCircleRadius - mStrokeWidth * 0.5f;

	canvas.translate(mOffset, mCircleRadius);

	canvas.drawCircle(0, 0, r, mCirclePaint);
	canvas.drawCircle(0, 0, mCentreRadius, mCentrePaint);
    }

    // Set listener

    protected void setListener(ColourChangeListener l)
    {
	listener = l;
    }

    // Set colour

    protected void setColour(int c)
    {
	mCentrePaint.setColor(c);
    }

    // Get colour

    protected int getColour()
    {
	return mCentrePaint.getColor();
    }

    // On touch event

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
	float x = event.getX() - mOffset;
	float y = event.getY() - mCircleRadius;

	boolean inCentre = Math.sqrt(x * x + y * y) <= mCentreRadius;

	switch (event.getAction())
	{
	case MotionEvent.ACTION_DOWN:
	    mTrackingCentre = inCentre;
	    if (inCentre)
	    {
		if (listener != null)
		    listener.onColourChanged(mCentrePaint.getColor());

		break;
	    }

	case MotionEvent.ACTION_MOVE:
	    if (!mTrackingCentre)
	    {
		float angle = (float)Math.toDegrees(Math.atan2(y, -x));

		// Need to turn angle +-180 to 0-360

		hsv[0] = angle + 180;

		mCentrePaint.setColor(Color.HSVToColor(hsv));
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
