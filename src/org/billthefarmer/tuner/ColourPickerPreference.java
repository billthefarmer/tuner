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

import android.preference.DialogPreference;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

// Colour picker preference

public class ColourPickerPreference extends DialogPreference
{
    private int mColour;
    private ColourPicker mForegroundPicker;
    private ColourPicker mBackgroundPicker;

    // Constructor

    public ColourPickerPreference(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On bind dialog view

    @Override
    protected void onBindDialogView(View view)
    {
    	super.onBindDialogView(view);

    	mForegroundPicker =
    			(ColourPicker)view.findViewById(R.id.foreground_picker);
    	mBackgroundPicker =
    			(ColourPicker)view.findViewById(R.id.background_picker);

    	mForegroundPicker.setColour(mColour);
    	mBackgroundPicker.setColour(mColour);

    	setIconColour();
    }

    // On create dialog view

//    @Override
//    protected View onCreateDialogView()
//    {
//	mPicker = new ColourPicker(getContext());
//	return mPicker;
//    }

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
	    mColour = mForegroundPicker.getColour();
	    persistInt(mColour);
	    setIconColour();
	}
    }

    // Get colour

    protected int getColour()
    {
	return mColour;
    }
    
    private void setIconColour()
    {
	// Get the drawable icon

	BitmapDrawable drawable = (BitmapDrawable)getIcon();

	// Check we've got one

	if (drawable != null)
	{
	    // Get the bitmap

	    Bitmap bitmap = drawable.getBitmap();

	    // Copy it

	    bitmap = bitmap.copy(Config.ARGB_8888, true);

	    // Get the dimensions

	    int w = bitmap.getWidth();
	    int h = bitmap.getHeight();

	    // Create a canvas to draw in and some paint to draw with

	    Canvas canvas = new Canvas(bitmap);
	    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	    // Set the colour and fill style

	    paint.setColor(mColour);
	    paint.setStyle(Style.FILL);

	    // Draw a circle in the middle

	    canvas.drawCircle(w / 2, h / 2, w / 6, paint);

	    // Create another drawable from the bitmap using
	    // the picker's resources because we ain't got none

	    drawable = new BitmapDrawable(mForegroundPicker.getResources(), bitmap);

	    // Set the icon, all the above just to change the colour
	    // of the middle circle in the icon

	    setIcon(drawable);
	}
    }
}
