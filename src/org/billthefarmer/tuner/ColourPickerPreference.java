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

import org.billthefarmer.tuner.ColourPicker.ColourChangeListener;
import org.json.JSONArray;
import org.json.JSONException;

import android.preference.DialogPreference;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    // On bind view

    @Override
    protected void onBindView(View view)
    {
    super.onBindView(view);

    // Get the colours

    int foreground = 0;
    int background = 0;

    try
    {
		foreground = mColours.getInt(0);
		background = mColours.getInt(1);
	}

    catch (JSONException e)
    {
		e.printStackTrace();
	}

    // Change the icons

    Resources resources = view.getResources();
	Drawable d = createIcon(resources, foreground, background);
//	setDialogIcon(d);
	setIcon(d);
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

	// Change the icons

//	Drawable d = createIcon(resources.foreground, mStrobe.background);
	Drawable d = getIcon();
	setDialogIcon(d);
//	setIcon(d);

	// Set the listeners

	mForegroundPicker.setListener(new ColourChangeListener()
	    {
		public void onColourChanged(int c)
		{
		    mStrobe.foreground = c;
		    mStrobe.createShaders();
		}
	    });

	mBackgroundPicker.setListener(new ColourChangeListener()
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

	    // Change the icon before the preference fragment gets hold of it

	    Resources resources = mStrobe.getResources();
		Drawable d = createIcon(resources,
				mStrobe.foreground, mStrobe.background);
		setDialogIcon(d);
		setIcon(d);

		mColours = new JSONArray();

		// Save the colours

		mColours.put(mForegroundPicker.getColour());
	    mColours.put(mBackgroundPicker.getColour());

	    persistString(mColours.toString());
	}
    }

    // Create coloured icon

    private Drawable createIcon(Resources resources,
    		int foreground, int background)
    {
	// Get icon size from existing icon

	BitmapDrawable drawable = (BitmapDrawable)getIcon();
	Bitmap bitmap = drawable.getBitmap();
	int w = bitmap.getWidth();
	int h = bitmap.getHeight();

	// Create bitmap

	bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	Canvas canvas = new Canvas(bitmap);
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	RectF rect = new RectF(4, 4, w - 4, h - 4);

	// Draw foreground

	paint.setColor(foreground);
	canvas.clipRect(new Rect(0, 0, w / 2, h / 2), Region.Op.REPLACE);
	canvas.drawRoundRect(rect, 7, 7, paint);
	canvas.clipRect(new Rect(w / 2, h / 2, w, h), Region.Op.REPLACE);
	canvas.drawRoundRect(rect, 7, 7, paint);

	// Draw background

	paint.setColor(background);
	canvas.clipRect(new Rect(w / 2, 0, w, h / 2), Region.Op.REPLACE);
	canvas.drawRoundRect(rect, 7, 7, paint);
	canvas.clipRect(new Rect(0, h / 2, w / 2, h), Region.Op.REPLACE);
	canvas.drawRoundRect(rect, 7, 7, paint);

	// Create a gradient to do shading
	LinearGradient gradient =
	    new LinearGradient(0, 0, 0, h / 2,
			       Color.argb(127, 255, 255, 255),
			       Color.WHITE, TileMode.CLAMP);

	// Create a bitmap to shade with

	paint.setShader(gradient);
	Bitmap shaded = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	Canvas rounded = new Canvas(shaded);
	rounded.drawRoundRect(rect, 7, 7, paint);

	// Create magic paint to shade the icon

	paint.setShader(null);
	paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	canvas.clipRect(new Rect(0, 0, w, h), Region.Op.REPLACE);
	canvas.drawBitmap(shaded, 0, 0, paint);

	// Create drawable

	drawable = new BitmapDrawable(resources, bitmap);
	return drawable;
    }
}
