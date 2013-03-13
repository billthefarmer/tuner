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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AboutPreference extends DialogPreference
{

    // Constructor

    public AboutPreference(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    // On bind dialog view

    @Override
    protected void onBindDialogView(View view)
    {
	// Get version text view

	TextView version = (TextView) view.findViewById(R.id.about);

	// Get context and package manager

	Context context = getContext();
	PackageManager manager = context.getPackageManager();

	// Get info

	PackageInfo info = null;
	try
	{
	    info = manager.getPackageInfo("org.billthefarmer.tuner", 0);
	}
		
	catch (NameNotFoundException e)
	{
	    e.printStackTrace();
	}

	// Set version in text view

	if (info != null)
	{
	    String v = (String) version.getText();
	    String s = String.format(v, info.versionName);
	    version.setText(s);
	}
    }
}
