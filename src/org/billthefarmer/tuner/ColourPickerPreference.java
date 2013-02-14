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
import android.util.AttributeSet;
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
    protected View onCreateDialogView()
    {
    	mPicker = new ColourPicker(getContext());
    	mPicker.setColour(mColour);

    	return mPicker;
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
}
