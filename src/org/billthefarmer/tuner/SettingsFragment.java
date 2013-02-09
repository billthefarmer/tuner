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

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment
    implements OnSharedPreferenceChangeListener
{
    public static final String KEY_PREF_INPUT = "pref_input";
    public static final String KEY_PREF_SAMPLE = "pref_sample";
    public static final String KEY_PREF_REFERENCE = "pref_reference";

    private String refSummary;

    @SuppressLint("DefaultLocale")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	// Load the preferences from an XML resource

	addPreferencesFromResource(R.xml.preferences);
	
	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(getActivity());

	preferences.registerOnSharedPreferenceChangeListener(this);

	Preference preference = findPreference(KEY_PREF_INPUT);
	preference.setSummary(((ListPreference)preference).getEntry());

	preference = findPreference(KEY_PREF_SAMPLE);
	preference.setSummary(((ListPreference)preference).getEntry());

	preference = findPreference(KEY_PREF_REFERENCE);
	refSummary = (String)preference.getSummary();

	int value = ((NumberPickerPreference)preference).getValue();
	String s = String.format(refSummary, value);
	preference.setSummary(s);
    }

    // On shared preference changed

    public void onSharedPreferenceChanged(SharedPreferences preferences,
					  String key)
    {
	if (key.equals(KEY_PREF_INPUT) || key.equals(KEY_PREF_SAMPLE))
	{
	    Preference preference = findPreference(key);
	    // Set summary to be the user-description for the selected value
	    preference.setSummary(((ListPreference) preference).getEntry());
	}

	if (key.equals(KEY_PREF_REFERENCE))
	{
	    Preference preference = findPreference(key);

	    int value = ((NumberPickerPreference)preference).getValue();
		String s = String.format(refSummary, value);
		preference.setSummary(s);
	}
    }
}
