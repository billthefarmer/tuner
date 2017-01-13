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
import android.util.AttributeSet;
import android.view.View;

public abstract class PreferenceView extends View
{

    protected static int maxWidth;

    // Constructor

    public PreferenceView(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	maxWidth = 0;
    }

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	// Get the largest width offered so a valid calculation can be made

	int w = MeasureSpec.getSize(widthMeasureSpec);

	if (maxWidth < w)
	    maxWidth = w;

	setMeasuredDimension(maxWidth / 4, maxWidth / 4);
    }
}
