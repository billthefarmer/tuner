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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import java.util.Locale;

// Spectrum
public class Spectrum extends Graticule
{
    private Path path;
    private Path fillPath;
    private float max;

    // Constructor
    public Spectrum(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        path = new Path();
        fillPath = new Path();
    }

    // Draw trace
    @Override
    protected void drawTrace(Canvas canvas)
    {

        // Check for data
        if (audio == null || audio.xa == null)
            return;

        // Draw D if downsample
        if (audio.downsample)
        {
            // Color yellow
            paint.setStrokeWidth(1);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.YELLOW);

            float height = paint.getFontMetrics(null);
            canvas.drawText("D", 4, height - 2, paint);
        }

        // Draw NF if note filter
        if (audio.filters)
        {
            // Color yellow
            paint.setStrokeWidth(1);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.YELLOW);

            canvas.drawText("NF", 4, height - 4, paint);
        }

        // Translate canvas
        canvas.translate(0, height);

        // Check max value
        if (max < 1.0f)
            max = 1.0f;

        // Calculate the scaling
        float yscale = (height / max);

        max = 0.0f;

        // Rewind path
        path.rewind();
        path.moveTo(0, 0);

        // Copy path for fill
        fillPath.set(path);

        // If zoomed
        if (audio.zoom)
        {
            // Calculate limits
            double lower = audio.lower / audio.fps;
            double higher = audio.higher / audio.fps;
            double nearest = audio.nearest / audio.fps;

            // Calculate scale
            float xscale = (float) ((width / (nearest - lower)) / 2.0);

            int lo = (int) Math.floor(lower);
            int hi = (int) Math.ceil(higher);

            // Create trace
            for (int i = lo; i <= hi; i++)
            {
                if (i > 0 && i < audio.xa.length)
                {
                    float value = (float) audio.xa[i];

                    // Get max value
                    if (max < value)
                        max = value;

                    float y = -value * yscale;
                    float x = (float) ((i - lower) * xscale);

                    path.lineTo(x, y);
                    fillPath.lineTo(x, y);
                    path.addCircle(x, y, 1, Path.Direction.CW);
                }
            }

            // Complete path for fill
            fillPath.lineTo(width, 0);
            fillPath.close();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(63, 0, 255, 0));
            canvas.drawPath(fillPath, paint);

            // Create centre line
            path.moveTo(width / 2, 0);
            path.lineTo(width / 2, -height);

            // Color green
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);

            // Draw trace
            canvas.drawPath(path, paint);
            path.rewind();

            // Yellow pen for frequency trace
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(false);
            paint.setStrokeWidth(1);

            // Create lines for each frequency
            for (int i = 0; i < audio.count; i++)
            {
                // Draw line for each that are in range
                if (audio.maxima.f[i] > audio.lower &&
                        audio.maxima.f[i] < audio.higher)
                {
                    float x =
                        (float) ((audio.maxima.f[i] - audio.lower) /
                                 audio.fps * xscale);

                    path.moveTo(x, 0);
                    path.lineTo(x, -height);

                    double f = audio.maxima.f[i];

                    // Reference freq
                    double fr = audio.maxima.r[i];
                    double c = -12.0 * log2(fr / f);

                    // Ignore silly values
                    if (Double.isNaN(c))
                        continue;

                    // Draw cents value
                    String s = String.format(Locale.getDefault(),
                                             "%+1.0f", c * 100.0);
                    canvas.drawText(s, x, 0, paint);
                }
            }

            // Yellow pen for frequency trace
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);

            // Draw path
            canvas.drawPath(path, paint);
        }

        // Not zoomed
        else
        {
            // Calculate x scale
            float xscale = (float) Math.log(audio.xa.length) / width;

            // Create trace
            int last = 1;
            for (int x = 0; x < width; x++)
            {
                float value = 0.0f;

                int index = (int) Math.round(Math.pow(Math.E, x * xscale));
                for (int i = last; i <= index; i++)
                {
                    // Don't show DC component and don't overflow
                    if (i > 0 && i < audio.xa.length)
                    {
                        // Find max value for each vertex
                        if (value < audio.xa[i])
                            value = (float) audio.xa[i];
                    }
                }

                // Update last index
                last = index + 1;

                // Get max value
                if (max < value)
                    max = value;

                float y = -value * yscale;

                path.lineTo(x, y);
                fillPath.lineTo(x, y);
            }

            // Complete path for fill
            fillPath.lineTo(width, 0);
            fillPath.close();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(63, 0, 255, 0));
            canvas.drawPath(fillPath, paint);

            // Color green
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);

            // Draw path
            canvas.drawPath(path, paint);
            path.rewind();

            // Yellow pen for frequency trace
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(false);
            paint.setStrokeWidth(1);

            // Create lines for each frequency
            for (int i = 0; i < audio.count; i++)
            {
                // Draw line for each
                float x =
                    (float) Math.log(audio.maxima.f[i] / audio.fps) / xscale;

                path.moveTo(x, 0);
                path.lineTo(x, -height);

                double f = audio.maxima.f[i];

                // Reference freq
                double fr = audio.maxima.r[i];
                double c = -12.0 * log2(fr / f);

                // Ignore silly values
                if (Double.isNaN(c))
                    continue;

                // Draw cents value
                String s = String.format(Locale.getDefault(),
                                         "%+1.0f", c * 100.0);
                canvas.drawText(s, x, 0, paint);
            }

            // Yellow pen for frequency trace
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);

            // Draw path
            canvas.drawPath(path, paint);
        }
    }

    // Log2
    protected double log2(double d)
    {
        return Math.log(d) / Math.log(2.0);
    }
}
