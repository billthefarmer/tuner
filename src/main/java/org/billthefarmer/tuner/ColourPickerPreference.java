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

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

// Colour picker preference
public class ColourPickerPreference extends DialogPreference
{
    private JSONArray colours;
    private StrobeView strobe;
    private ColourPicker foregroundPicker;
    private ColourPicker backgroundPicker;

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
        strobe = view.findViewById(R.id.strobe);
        foregroundPicker =
            view.findViewById(R.id.foreground_picker);
        backgroundPicker =
            view.findViewById(R.id.background_picker);

        // Set the picker colours
        try
        {
            foregroundPicker.setColour(colours.getInt(0));
            backgroundPicker.setColour(colours.getInt(1));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        // Set the dummy strobe colours
        strobe.foreground = foregroundPicker.getColour();
        strobe.background = backgroundPicker.getColour();

        // Set the listeners
        foregroundPicker.setListener(c ->
        {
            strobe.foreground = c;
            strobe.createShaders();
        });

        backgroundPicker.setListener(c ->
        {
            strobe.background = c;
            strobe.createShaders();
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
                colours = new JSONArray(getPersistedString(null));
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
                colours = new JSONArray((String) defaultValue);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            persistString(colours.toString());
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

            strobe.foreground = foregroundPicker.getColour();
            strobe.background = backgroundPicker.getColour();

            colours = new JSONArray();

            // Save the colours

            colours.put(foregroundPicker.getColour());
            colours.put(backgroundPicker.getColour());

            persistString(colours.toString());
        }
    }
}
