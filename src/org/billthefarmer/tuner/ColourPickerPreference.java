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

import org.json.JSONArray;
import org.json.JSONException;

import android.preference.DialogPreference;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

// Colour picker preference

public class ColourPickerPreference extends DialogPreference
{
    private JSONArray mColours;
    private StrobeView mStrobe;
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

	// Get the views

	mStrobe = (StrobeView)view.findViewById(R.id.strobe);
	mForegroundPicker =
	    (ColourPicker)view.findViewById(R.id.foreground_picker);
	mBackgroundPicker =
	    (ColourPicker)view.findViewById(R.id.background_picker);

	// Set the picker colours

	try
	{
	    mForegroundPicker.setColour(mColours.getInt(0));
	    mBackgroundPicker.setColour(mColours.getInt(1));
	}

	catch (JSONException e)
	{
	    e.printStackTrace();
	}

	// Set the dummy strobe colours

	mStrobe.foreground = mForegroundPicker.getColour();
	mStrobe.background = mBackgroundPicker.getColour();

	// Set the listeners

	mForegroundPicker.setListener(new ColourPicker.ColourChangeListener()
	    {
		public void onColourChanged(int c)
		{
		    mStrobe.foreground = c;
		    mStrobe.createShaders();
		}
	    });

	mBackgroundPicker.setListener(new ColourPicker.ColourChangeListener()
	    {
		public void onColourChanged(int c)
		{
		    mStrobe.background = c;
		    mStrobe.createShaders();
		}
	    });
    }

    // On get default value

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
	return a.getString(index);
    }

    // On set initial value

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
				     Object defaultValue)
    {
	if (restorePersistedValue)
	{
	    // Restore existing state

	    try
	    {
		mColours = new JSONArray(getPersistedString(null));
	    }

	    catch (JSONException e)
	    {
		e.printStackTrace();
	    }
	}

	else
	{
	    // Set default state from the XML attribute

	    try
	    {
		mColours = new JSONArray((String)defaultValue);
	    }

	    catch (JSONException e)
	    {
		e.printStackTrace();
	    }

	    persistString(mColours.toString());
	}
    }

    // On dialog closed

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
	// When the user selects "OK", persist the new value

	if (positiveResult)
	{
	    // Change the dummy strobe colours

	    mStrobe.foreground = mForegroundPicker.getColour();
	    mStrobe.background = mBackgroundPicker.getColour();

	    mColours = new JSONArray();

	    // Save the colours

	    mColours.put(mForegroundPicker.getColour());
	    mColours.put(mBackgroundPicker.getColour());

	    persistString(mColours.toString());
	}
    }
}
