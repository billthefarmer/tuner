/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.billthefarmer.tuner;

import android.preference.DialogPreference;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColourPickerPreference extends DialogPreference
{
    private int mColour;
    private ColourPicker mPicker;

    public ColourPickerPreference(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view)
    {
	super.onBindDialogView(view);

	mPicker = (ColourPicker)view.findViewById(R.id.colour_picker);

	mPicker.setColour(mColour);
    }
    // On get default value

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
	return a.getInteger(index, 0);
    }

    // On set initial value

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
				     Object defaultValue)
    {
	if (restorePersistedValue)
	{
	    // Restore existing state

	    mColour = getPersistedInt(0);
	}

	else
	{
	    // Set default state from the XML attribute

	    mColour = (Integer) defaultValue;
	    persistInt(mColour);
	}
    }

    // On dialog closed

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
	// When the user selects "OK", persist the new value

	if (positiveResult)
	{
	    mColour = mPicker.getColour();
	    persistInt(mColour);
	}
    }

    // Get value

    protected int getValue()
    {
	return mColour;
    }

    public class ColourPicker extends View
    {
	private Paint mPaint;
	private Paint mCenterPaint;
	private final int[] mColours;

	public ColourPicker(Context context, AttributeSet attrs)
	{
	    super(context, attrs);

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

	private boolean mTrackingCenter;
	private boolean mHighlightCenter;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas)
	{
	    float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;

	    canvas.translate(CENTER_X, CENTER_X);

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

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//	{
//	    setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2);
//	}

	private static final int CENTER_X = 100;
	private static final int CENTER_Y = 100;
	private static final int CENTER_RADIUS = 32;

	private void setColour(int c)
	{
		mCenterPaint.setColor(c);
	}

	private int getColour()
	{
		return mCenterPaint.getColor();
	}
	private int floatToByte(float x)
	{
	    int n = java.lang.Math.round(x);
	    return n;
	}
	private int pinToByte(int n)
	{
	    if (n < 0)
	    {
		n = 0;
	    }

	    else if (n > 255)
	    {
		n = 255;
	    }

	    return n;
	}

	private int ave(int s, int d, float p)
	{
	    return s + java.lang.Math.round(p * (d - s));
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

	@SuppressWarnings("unused")
	private int rotateColour(int colour, float rad)
	{
	    float deg = rad * 180 / 3.1415927f;
	    int r = Color.red(colour);
	    int g = Color.green(colour);
	    int b = Color.blue(colour);

	    ColorMatrix cm = new ColorMatrix();
	    ColorMatrix tmp = new ColorMatrix();

	    cm.setRGB2YUV();
	    tmp.setRotate(0, deg);
	    cm.postConcat(tmp);
	    tmp.setYUV2RGB();
	    cm.postConcat(tmp);

	    final float[] a = cm.getArray();

	    int ir = floatToByte(a[0] * r +  a[1] * g +	 a[2] * b);
	    int ig = floatToByte(a[5] * r +  a[6] * g +	 a[7] * b);
	    int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

	    return Color.argb(Color.alpha(colour), pinToByte(ir),
			      pinToByte(ig), pinToByte(ib));
	}

	private static final float PI = 3.1415926f;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
	    float x = event.getX() - CENTER_X;
	    float y = event.getY() - CENTER_Y;

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
		    float unit = angle/(2*PI);
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
//			mListener.colourChanged(mCenterPaint.getColor());
		    }
		    
		    mTrackingCenter = false;	// so we draw w/o halo
		    invalidate();
		}
		break;
	    }

	    return true;
	}
    }
}
