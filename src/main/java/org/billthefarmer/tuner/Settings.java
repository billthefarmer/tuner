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
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Properties;

// Settings
public class Settings extends Activity
{
    public final static String TEXT_WILD = "text/*";
    public final static int OPEN_DOCUMENT   = 1;

    // On create
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        int theme = Integer.parseInt(preferences.getString(Tuner.PREF_THEME,
                                                           "0"));

        Configuration config = getResources().getConfiguration();
        int night = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (theme)
        {
        case Tuner.LIGHT:
            setTheme(R.style.AppTheme);
            break;

        case Tuner.DARK:
            setTheme(R.style.AppDarkTheme);
            break;

        case Tuner.SYSTEM:
            switch (night)
            {
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);
                break;

            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppDarkTheme);
                break;
            }
            break;

        case Tuner.WHITE:
            setTheme(R.style.AppWhiteTheme);
            break;

        case Tuner.BLACK:
            setTheme(R.style.AppBlackTheme);
            break;
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();

        // Enable back navigation on action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings);
    }

    // On create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom, menu);

        return true;
    }

    // On options item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Switch on item id
        switch (item.getItemId())
        {
        case android.R.id.home:
            // app icon in action bar clicked; go home
            finish();
            return true;

        case android.R.id.custom:
            // load custom temperaments
            loadCustom();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    // Load custom
    private void loadCustom()
    {
        // Use storage
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(TEXT_WILD);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, OPEN_DOCUMENT);
    }

    // On activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (resultCode == RESULT_CANCELED)
            return;

        switch (requestCode)
        {
        case OPEN_DOCUMENT:
            Uri content = data.getData();
            readCustom(content);
            break;
        }
    }

    private void readCustom(Uri content)
    {
        // Read into properties
        Properties props = new Properties();
        try (InputStreamReader reader = new InputStreamReader
             (getContentResolver().openInputStream(content)))
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
            PreferenceManager.getDefaultSharedPreferences(this);
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

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
            recreate();
    }
}
