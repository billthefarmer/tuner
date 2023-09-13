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
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

// Signal view
public class SignalView extends View
{
    protected Tuner.Audio audio;

    private int height;
    private int width;
    private int margin;

    private double signal;

    private Paint paint;
    private RectF rect;

    private static final float SCALE = (float) Math.log(0.01);

    // Constructor
    public SignalView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    // On measure
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // Get offered height
        int h = MeasureSpec.getSize(heightMeasureSpec);

        // Set size to offered height
        setMeasuredDimension(h / 2, h);
    }

    // On size changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // Get dimensions
        height = h;
        width = w;
        margin = w / 4;

        // Colours for gradient
        int colours[] =
        {Color.RED, Color.YELLOW, Color.GREEN};

        // Coloured gradient
        LinearGradient gradient =
            new LinearGradient(0, 0, 0, h * 3 / 4,
                               colours, null, Shader.TileMode.CLAMP);

        // Bitmap to draw coloured bars in
        Bitmap bitmap = Bitmap.createBitmap(w / 2, h * 3 / 4,
                                            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(gradient);
        paint.setStrokeWidth(2);

        // Draw coloured bars
        for (int i = 0; i < bitmap.getHeight(); i += 4)
            canvas.drawLine(0, i, bitmap.getWidth(), i, paint);

        // Create shader from coloured bars
        BitmapShader shader = new BitmapShader(bitmap,
                                               Shader.TileMode.CLAMP,
                                               Shader.TileMode.CLAMP);

        paint.setShader(shader);

        // Rect to draw the coloured bars
        rect = new RectF(margin, margin,
                         margin + w / 2, margin + height * 3 / 4);

        // Schedule update
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                post(() -> update());
            }
        }, 10, 10);
    }

    // update
    private void update()
    {
        // Do VU meter style calculation
        if (audio != null)
        {
            if (signal < audio.signal)
                signal = ((signal * 4) + audio.signal) / 5;

            else
                signal = ((signal * 9) + audio.signal) / 10;
        }

        invalidate();
    }

    // onDraw
    @Override
    @SuppressWarnings("deprecation")
    protected void onDraw(Canvas canvas)
    {
        // Draw the coloured column
        paint.setStyle(Paint.Style.FILL);
        int max = height * 3 / 4;
        float v = (float) (Math.log(signal) / SCALE);

        rect.top = margin + (max * v);
        if (rect.top < margin)
            rect.top = margin;

        canvas.clipRect(rect);
        canvas.drawRoundRect(rect, 3, 3, paint);
    }
}
