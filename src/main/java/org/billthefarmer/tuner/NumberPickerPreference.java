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
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.Locale;

// NumberPickerPreference
public class NumberPickerPreference extends DialogPreference
{
    private final int maxValue;
    private final int minValue;

    private int value;

    private NumberPicker picker;

    // Constructor
    public NumberPickerPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a =
            context.obtainStyledAttributes(attrs,
                                           R.styleable.NumberPickerPreference);
        maxValue =
            a.getInt(R.styleable.NumberPickerPreference_maxValue, 0);
        minValue =
            a.getInt(R.styleable.NumberPickerPreference_minValue, 0);
        a.recycle();
    }

    // On create dialog view
    @Override
    protected View onCreateDialogView()
    {
        picker = new NumberPicker(getContext());

        picker.setMaxValue(maxValue);
        picker.setMinValue(minValue);
        picker.setValue(value);

        picker.setFormatter(value -> String.format(Locale.getDefault(),
                            "%dHz", value));

        picker.setWrapSelectorWheel(false);
        picker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        return picker;
    }

    // On get default value

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getInteger(index, value);
    }

    // On set initial value
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue)
    {
        if (restorePersistedValue)
        {
            // Restore existing state
            value = getPersistedInt(0);
        }
        else
        {
            // Set default state from the XML attribute
            value = (Integer) defaultValue;
            persistInt(value);
        }
    }

    // On dialog closed
    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        // When the user selects "OK", persist the new value
        if (positiveResult)
        {
            value = picker.getValue();
            persistInt(value);
        }
    }

    // Get value
    protected int getValue()
    {
        return value;
    }
}
