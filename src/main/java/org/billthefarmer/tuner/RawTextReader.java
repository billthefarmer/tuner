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

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// RawTextReader
public class RawTextReader
{
    /* ********************************************************************
     * Read raw text file resource...
     *
     * source: http://stackoverflow.com/questions/4087674/android-read-text-raw-resource-file
     */

    // read
    public static String read(Context context, int resId)
    {
        InputStream stream = context.getResources().openRawResource(resId);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader buff = new BufferedReader(reader);

        String line;
        StringBuilder text = new StringBuilder();

        try
        {
            while ((line = buff.readLine()) != null)
                text.append(line).append("\n");
        }
        catch (Exception e)
        {
            return "";
        }

        return text.toString();
    }
}
