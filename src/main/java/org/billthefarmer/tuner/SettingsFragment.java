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

import android.app.ActionBar;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

// SettingsFragment
@SuppressWarnings("deprecation")
public class SettingsFragment extends android.preference.PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final int BLUE = 0;
    private static final int OLIVE = 1;
    private static final int MAGENTA = 2;
    private static final int CUSTOM = 3;

    private static final String TAG = "Tuner";
    private static final String CUSTOM_FILE = "Custom.txt";
    private static final String CUSTOM_PATH = "Tuner/Custom.txt";

    private String summary;

    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference preference =
            (ListPreference) findPreference(Tuner.PREF_INPUT);
        preference.setSummary(preference.getEntry());

        preference = (ListPreference) findPreference(Tuner.PREF_COLOUR);
        ColourPickerPreference custom =
            (ColourPickerPreference) findPreference(Tuner.PREF_CUSTOM);
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
            preference.setIcon(R.drawable.ic_pref_spectrum);
            preference.setDialogIcon(R.drawable.ic_pref_spectrum);

            // Enable colour pickers
            custom.setEnabled(true);
            break;
        }

        NumberPickerPreference picker =
            (NumberPickerPreference) findPreference(Tuner.PREF_REFER);
        summary = picker.getSummary().toString();

        // Set number picker summary
        float f = picker.getValue();
        String s = String.format(summary, f);
        picker.setSummary(s);

        // Load custom temperaments
        loadCustomTemperaments();

        // Reset bach preference on solfa
        CheckBoxPreference check =
            (CheckBoxPreference) findPreference(Tuner.PREF_SOLFA);
        if (check.isChecked())
        {
            check = (CheckBoxPreference) findPreference(Tuner.PREF_BACH);
            check.setChecked(false);
        }

        // Disable key preference on equal temperament
        preference = (ListPreference) findPreference(Tuner.PREF_TEMPER);
        v = Integer.valueOf(preference.getValue());
        preference = (ListPreference) findPreference(Tuner.PREF_KEY);
        if (v == getActivity().getResources()
            .getInteger(R.integer.default_temper))
        {
            preference.setEnabled(false);
            preference.setValueIndex(0);
        }

        else
            preference.setEnabled(true);

        // Get about summary
        Preference about = findPreference(Tuner.PREF_ABOUT);
        String sum = about.getSummary().toString();

        // Set version in text view
        s = String.format(sum, BuildConfig.VERSION_NAME);
        about.setSummary(s);
    }

    // on Resume
    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
    }

    // on Pause
    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
    }

    // On preference tree click
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference)
    {
        boolean result =
            super.onPreferenceTreeClick(preferenceScreen, preference);

        if (preference instanceof PreferenceScreen)
        {
            Dialog dialog = ((PreferenceScreen) preference).getDialog();
            ActionBar actionBar = dialog.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        return result;
    }

    // On shared preference changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key)
    {
        if (key.equals(Tuner.PREF_INPUT) ||
            key.equals(Tuner.PREF_COLOUR))
        {
            Preference preference = findPreference(key);

            // Set summary to be the user-description for the selected value
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        if (key.equals(Tuner.PREF_COLOUR))
        {
            ListPreference preference = (ListPreference) findPreference(key);
            ColourPickerPreference custom =
                (ColourPickerPreference) findPreference(Tuner.PREF_CUSTOM);

            custom.setEnabled(false);

            // Get the value and set the dialog icon
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
                preference.setIcon(R.drawable.ic_pref_spectrum);
                preference.setDialogIcon(R.drawable.ic_pref_spectrum);

                // Enable colour pickers
                custom.setEnabled(true);
                break;
            }
        }

        if (key.equals(Tuner.PREF_CUSTOM))
        {
            ColourPickerPreference custom =
                (ColourPickerPreference) findPreference(key);

            // Change the list preference icons
            Drawable d = custom.getIcon();
            ListPreference preference =
                (ListPreference) findPreference(Tuner.PREF_COLOUR);
            preference.setDialogIcon(d);
            preference.setIcon(d);
        }

        if (key.equals(Tuner.PREF_REFER))
        {
            NumberPickerPreference preference =
                (NumberPickerPreference) findPreference(key);

            // Get the value and set the summary
            float v = preference.getValue();
            String s = String.format(summary, v);
            preference.setSummary(s);
        }

        // Reset bach preference on solfa
        if (key.equals(Tuner.PREF_SOLFA))
        {
            CheckBoxPreference check =
                (CheckBoxPreference) findPreference(key);
            if (check.isChecked())
            {
                check = (CheckBoxPreference) findPreference(Tuner.PREF_BACH);
                check.setChecked(false);
            }
        }

        // Disable key preference on equal temperament
        if (key.equals(Tuner.PREF_TEMPER))
        {
            ListPreference preference =
                (ListPreference) findPreference(key);
            int v = Integer.valueOf(preference.getValue());
            preference = (ListPreference) findPreference(Tuner.PREF_KEY);
            if (v == getActivity().getResources()
                .getInteger(R.integer.default_temper))
            {
                preference.setEnabled(false);
                preference.setValueIndex(0);
            }

            else
                preference.setEnabled(true);
        }

        if (key.equals(Tuner.PREF_THEME))
        {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                getActivity().recreate();
        }
    }

    // loadCustomTemperaments
    private void loadCustomTemperaments()
    {
        // Check custom temperaments file
        File custom = new File(getActivity().getExternalFilesDir(null),
                               CUSTOM_FILE);
        if (!custom.canRead())
            custom = new File(Environment.getExternalStorageDirectory(),
                              CUSTOM_PATH);
        if (!custom.canRead())
            return;

        // Read into properties
        Properties props = new Properties();
        try (FileReader reader = new FileReader(custom))
        {
            props.load(reader);
        }

        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }

        // Store in preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();

        try (StringWriter writer = new StringWriter())
        {
            props.store(writer, "Custom Temperaments");
            editor.putString(Tuner.PREF_PROPS, writer.toString());
            editor.apply();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        // Sort the entries
        String order[] = props.stringPropertyNames().toArray(new String[0]);
        Arrays.sort(order);

        // Get the temperament entries and entry values
        ListPreference preference =
            (ListPreference) findPreference(Tuner.PREF_TEMPER);
        List<CharSequence> entries = new
            ArrayList<CharSequence>(Arrays.asList(preference.getEntries()));
        List<CharSequence> values = new
            ArrayList<CharSequence>(Arrays.asList(preference.getEntryValues()));

        // Add custom entries and entry values
        int value = values.size();
        for (String entry: order)
        {
            entries.add(entry);
            values.add(String.valueOf(value++));
        }

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Properties " + props);

        // Set the new entries and entry values
        preference.setEntries(entries.toArray(new CharSequence[0]));
        preference.setEntryValues(values.toArray(new CharSequence[0]));
    }
}
