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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

// Tuner View
public abstract class TunerView extends View {
    protected MainActivity.Audio audio;
    protected Resources resources;

    protected int width;
    protected int height;
    protected int textColour;

    protected BitmapDrawable lock;
    protected BitmapDrawable screen;

    protected Paint paint;
    protected Rect clipRect;
    private RectF outlineRect;

    // Constructor
    @SuppressWarnings("deprecation")
    protected TunerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        resources = getResources();

        final TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.Tuner, 0, 0);

        textColour =
                typedArray.getColor(R.styleable.Tuner_TextColour,
                        resources.getColor(android.R.color.black));
        lock = (BitmapDrawable)
                typedArray.getDrawable(R.styleable.Tuner_lock);
        screen = (BitmapDrawable)
                typedArray.getDrawable(R.styleable.Tuner_screen);
        typedArray.recycle();

        paint = new Paint();
    }

    // On Size Changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Save the new width and height
        width = w;
        height = h;

        // Create some rects for
        // the outline and clipping
        outlineRect = new RectF(1, 1, width - 1, height - 1);
        clipRect = new Rect(3, 3, width - 3, height - 3);
    }

    // On Draw
    @Override
    @SuppressWarnings("deprecation")
    protected void onDraw(Canvas canvas) {
        // Set up the paint and draw the outline
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setColor(resources.getColor(android.R.color.darker_gray));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(outlineRect, 10, 10, paint);

        // Set the clipRect
        canvas.clipRect(clipRect);

        // Translate to the clip rect
        canvas.translate(clipRect.left, clipRect.top);
    }
}
