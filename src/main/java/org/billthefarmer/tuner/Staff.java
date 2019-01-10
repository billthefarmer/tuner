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

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

// Staff
public class Staff extends TunerView
{
    private static final float tc[][]=
    {
        {-6, 16},  {-8, 13},  {-14, 19},  {-10, 35},  {2, 35},  {8, 37},  
        {21, 30},  {21, 17},  {21, 5},  {10, -1},  {0, -1},  {-12, -1},  
        {-23, 5},  {-23, 22},  {-23, 29},  {-22, 37},  {-7, 49},  {10, 61},  
        {10, 68},  {10, 73},  {10, 78},  {9, 82},  {7, 82},  {2, 78},  
        {-2, 68},  {-2, 62},  {-2, 25},  {10, 18},  {11, -8},  {11, -18}, 
        {5, -23},  {-4, -23},  {-10, -23},  {-15, -18},  {-15, -13},  
        {-15, -8},  {-12, -4},  {-7, -4},  {3, -4},  {3, -20},  {-6, -17},
        {-5, -23},  {9, -20},  {9, -9},  {7, 24},  {-5, 30},  {-5, 67}, 
        {-5, 78},  {-2, 87},  {7, 91},  {13, 87},  {18, 80},  {17, 73},  
        {17, 62},  {10, 54},  {1, 45},  {-5, 38},  {-15, 33},  {-15, 19}, 
        {-15, 7},  {-8, 1},  {0, 1},  {8, 1},  {15, 6},  {15, 14},  {15, 23},
        {7, 26},  {2, 26},  {-5, 26},  {-9, 21},  {-6, 16}
    };

    private static final float bc[][] =
    {
        {-2.3f,3}, {6,7}, {10.5f,12}, {10.5f,16},
        {10.5f,20.5f}, {8.5f,23.5f}, {6.2f,23.3f},
        {5.2f,23.5f}, {2,23.5f}, {0.5f,19.5f},
        {2,20}, {4,19.5f}, {4,18},
        {4,17}, {3.5f,16}, {2,16},
        {1,16}, {0,16.9f}, {0,18.5f},
        {0,21}, {2.1f,24}, {6,24}, 
        {10,24}, {13.5f,21.5f}, {13.5f,16.5f},
        {13.5f,11}, {7,5.5f}, {-2.0f,2.8f},
        {14.9f,21}, 
        {14.9f,22.5f}, {16.9f,22.5f}, {16.9f,21},
        {16.9f,19.5f}, {14.9f,19.5f}, {14.9f,21},
        {14.9f,15},
        {14.9f,16.5f}, {16.9f,16.5f}, {16.9f,15},
        {16.9f,13.5f}, {14.9f,13.5f}, {14.9f,15}
    };

    private Path tclef;
    private Path bclef;

    private float lineHeight; 
    private int margin; 

    // Constructor
    public Staff(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    // On size changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        height = clipRect.bottom - clipRect.top;
        width = clipRect.right - clipRect.left;

        lineHeight = height / 12f;
        margin = width / 12;
    }

    // On draw
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // Don't draw if turned off
        if (audio == null)
            return;

        // Set up paint
        paint.setStrokeWidth(2);
        paint.setColor(textColour);

        // Draw staff
        canvas.translate(0, height / 2f);
        for (int i = 1; i < 6; i++)
        {
            canvas.drawLine(margin, i * lineHeight,
                            width - margin, i * lineHeight, paint);
            canvas.drawLine(margin, i * -lineHeight,
                            width - margin, i * -lineHeight, paint);
        }
    }
}
