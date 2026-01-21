////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2015	Bill Farmer
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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// Help
@SuppressWarnings("deprecation")
public class Help extends Activity
{
    private static final String PREF_DARK = "pref_dark";

    // On create
    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        int theme = Integer.parseInt(preferences.getString(Tuner.PREF_THEME,
                                                           "0"));
        switch (theme)
        {
        case Tuner.LIGHT:
            setTheme(R.style.AppTheme);
            break;

        case Tuner.DARK:
            setTheme(R.style.AppDarkTheme);
            break;

        case Tuner.WHITE:
            setTheme(R.style.AppWhiteTheme);
            break;

        case Tuner.BLACK:
            setTheme(R.style.AppBlackTheme);
            break;
        }

        setContentView(R.layout.help);

        TextView view = findViewById(R.id.help);
        CharSequence text = read(this, R.raw.help);
        if (view != null)
        {
            view.setMovementMethod(LinkMovementMethod.getInstance());
            view.setText(Html.fromHtml(text.toString()));
        }

        // Enable back navigation on action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // On options item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Get id
        int id = item.getItemId();
        switch (id)
        {
        // Home
        case android.R.id.home:
            finish();
            break;

        default:
            return false;
        }

        return true;
    }

    // read
    public static CharSequence read(Context context, int resId)
    {
        StringBuilder text = new StringBuilder();

        try (BufferedReader buffer = new BufferedReader
             (new InputStreamReader
              (context.getResources().openRawResource(resId))))
        {
            String line;
            while ((line = buffer.readLine()) != null)
                text.append(line).append(System.getProperty("line.separator"));
        }

        catch (Exception e) {}

        return text;
    }
}
