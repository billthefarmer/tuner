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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;

// Colour picker
public class ColourPicker extends PreferenceView
{
    private Paint circlePaint;
    private Paint centrePaint;
    private final static int[] colours =
    {
        Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN,
        Color.GREEN, Color.YELLOW, Color.RED
    };

    private boolean trackingCentre;
    private int circleRadius;
    private int strokeWidth;
    private int centreRadius;
    private int offset;

    private float hsv[] = {0, 1, 1};

    // Colour change listener
    private ColourChangeListener listener;

    // Constructor
    public ColourPicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        Shader shader = new SweepGradient(0, 0, colours, null);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setShader(shader);
        circlePaint.setStyle(Paint.Style.STROKE);

        centrePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centrePaint.setStrokeWidth(8);
    }

    // On measure
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get the max width from the superclass

        setMeasuredDimension(maxWidth / 3, maxWidth / 3);
    }

    // On size changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // Calculate the dimensions based on the given values
        circleRadius = h / 2;
        strokeWidth = w / 5;
        centreRadius = w / 6;
        offset = w / 2;

        circlePaint.setStrokeWidth(strokeWidth);
    }

    // On draw
    @Override
    protected void onDraw(Canvas canvas)
    {
        float r = circleRadius - strokeWidth * 0.5f;

        canvas.translate(offset, circleRadius);

        canvas.drawCircle(0, 0, r, circlePaint);
        canvas.drawCircle(0, 0, centreRadius, centrePaint);
    }

    // Set listener
    protected void setListener(ColourChangeListener l)
    {
        listener = l;
    }

    // Get colour
    protected int getColour()
    {
        return centrePaint.getColor();
    }

    // Set colour
    protected void setColour(int c)
    {
        centrePaint.setColor(c);
    }

    // On touch event
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX() - offset;
        float y = event.getY() - circleRadius;

        boolean inCentre = Math.sqrt(x * x + y * y) <= centreRadius;

        switch (event.getAction())
        {
        case MotionEvent.ACTION_DOWN:
            trackingCentre = inCentre;
            if (inCentre)
            {
                if (listener != null)
                    listener.onColourChanged(centrePaint.getColor());

                break;
            }

        case MotionEvent.ACTION_MOVE:
            if (!trackingCentre)
            {
                float angle = (float) Math.toDegrees(Math.atan2(y, -x));

                // Need to turn angle +-180 to 0-360

                hsv[0] = angle + 180;

                centrePaint.setColor(Color.HSVToColor(hsv));
                invalidate();
            }
            break;

        case MotionEvent.ACTION_UP:
            if (trackingCentre)
            {
                trackingCentre = false;
                invalidate();
            }
            break;
        }

        return true;
    }

    protected interface ColourChangeListener
    {
        void onColourChanged(int colour);
    }
}
