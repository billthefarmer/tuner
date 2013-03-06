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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment
    implements OnSharedPreferenceChangeListener
{
    private static final int BLUE = 0;
    private static final int OLIVE = 1;
    private static final int MAGENTA = 2;
    private static final int CUSTOM = 3;

    private static final String KEY_PREF_INPUT = "pref_input";
    private static final String KEY_PREF_COLOUR = "pref_colour";
    private static final String KEY_PREF_REFERENCE = "pref_reference";
    private static final String KEY_PREF_CUSTOM = "pref_custom";

    private String refSummary;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	// Load the preferences from an XML resource

	addPreferencesFromResource(R.xml.preferences);
	
	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(getActivity());

	preferences.registerOnSharedPreferenceChangeListener(this);

	ListPreference preference =
	    (ListPreference)findPreference(KEY_PREF_INPUT);
	preference.setSummary(preference.getEntry());

	preference = (ListPreference)findPreference(KEY_PREF_COLOUR);
	ColourPickerPreference custom =
	    (ColourPickerPreference)findPreference(KEY_PREF_CUSTOM);
	preference.setSummary(preference.getEntry());

	// Disable colour pickers

	custom.setEnabled(false);

	int v = Integer.valueOf(preference.getValue());
	switch (v)
	{
	case BLUE:
	    preference.setIcon(R.drawable.ic_pref_blue);
	    preference.setDialogIcon(R.drawable.ic_pref_blue);
	    break;

	case OLIVE:
	    preference.setIcon(R.drawable.ic_pref_olive);
	    preference.setDialogIcon(R.drawable.ic_pref_olive);
	    break;

	case MAGENTA:
	    preference.setIcon(R.drawable.ic_pref_magenta);
	    preference.setDialogIcon(R.drawable.ic_pref_magenta);
	    break;

	case CUSTOM:
	    Drawable d = custom.getIcon();
	    preference.setDialogIcon(d);
	    preference.setIcon(d);

	    // Enable colour pickers

	    custom.setEnabled(true);
	    break;
	}

	NumberPickerPreference picker = 
	    (NumberPickerPreference)findPreference(KEY_PREF_REFERENCE);
	refSummary = (String)picker.getSummary();

	v = picker.getValue();
	String s = String.format(refSummary, v);
	picker.setSummary(s);
    }

    // On shared preference changed

    public void onSharedPreferenceChanged(SharedPreferences preferences,
					  String key)
    {
	if (key.equals(KEY_PREF_INPUT) || key.equals(KEY_PREF_COLOUR))
	{
	    Preference preference = findPreference(key);

	    // Set summary to be the user-description for the selected value

	    preference.setSummary(((ListPreference) preference).getEntry());
	}

	if (key.equals(KEY_PREF_COLOUR))
	{
	    ListPreference preference = (ListPreference)findPreference(key);
	    ColourPickerPreference custom =
		(ColourPickerPreference)findPreference(KEY_PREF_CUSTOM);

	    custom.setEnabled(false);

	    // Get the value and set the dialog icon

	    int v = Integer.valueOf(((ListPreference)preference).getValue());
	    switch (v)
	    {
	    case BLUE:
		preference.setIcon(R.drawable.ic_pref_blue);
		preference.setDialogIcon(R.drawable.ic_pref_blue);
		break;

	    case OLIVE:
		preference.setIcon(R.drawable.ic_pref_olive);
		preference.setDialogIcon(R.drawable.ic_pref_olive);
		break;

	    case MAGENTA:
		preference.setIcon(R.drawable.ic_pref_magenta);
		preference.setDialogIcon(R.drawable.ic_pref_magenta);
		break;

	    case CUSTOM:
		Drawable d = custom.getIcon();
		preference.setDialogIcon(d);
		preference.setIcon(d);

	    // Enable colour pickers

		custom.setEnabled(true);
		break;
	    }
	}

	if (key.equals(KEY_PREF_CUSTOM))
	{
	    ColourPickerPreference custom =
		(ColourPickerPreference)findPreference(key);

	    // Change the list preference icons

	    Drawable d = custom.getIcon();
	    ListPreference preference =
		(ListPreference)findPreference(KEY_PREF_COLOUR);
	    preference.setDialogIcon(d);
	    preference.setIcon(d);
	}

	if (key.equals(KEY_PREF_REFERENCE))
	{
	    NumberPickerPreference preference =
		(NumberPickerPreference)findPreference(key);

	    // Get the value and set the summary

	    int v = preference.getValue();
	    String s = String.format(refSummary, v);
	    preference.setSummary(s);
	}
    }
}
