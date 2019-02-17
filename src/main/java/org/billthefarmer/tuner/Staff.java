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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

// Staff
public class Staff extends TunerView
{
    private static final String TAG = "Staff";

    private static final int OCTAVE = 12;

    private static final int NATURAL = 0;
    private static final int SHARP   = 1;
    private static final int FLAT    = 2;

    // Treble clef
    private static final float tc[][]=
    {
        {-6, 16}, {-8, 13},
        {-14, 19}, {-10, 35}, {2, 35},
        {8, 37}, {21, 30}, {21, 17},
        {21, 5}, {10, -1}, {0, -1},
        {-12, -1}, {-23, 5}, {-23, 22},
        {-23, 29}, {-22, 37}, {-7, 49},
        {10, 61}, {10, 68}, {10, 73},
        {10, 78}, {9, 82}, {7, 82},
        {2, 78}, {-2, 68}, {-2, 62},
        {-2, 25}, {10, 18}, {11, -8},
        {11, -18}, {5, -23}, {-4, -23},
        {-10, -23}, {-15, -18}, {-15, -13},
        {-15, -8}, {-12, -4}, {-7, -4},
        {3, -4}, {3, -20}, {-6, -17},
        {-5, -23}, {9, -20}, {9, -9},
        {7, 24}, {-5, 30}, {-5, 67},
        {-5, 78}, {-2, 87}, {7, 91},
        {13, 87}, {18, 80}, {17, 73},
        {17, 62}, {10, 54}, {1, 45},
        {-5, 38}, {-15, 33}, {-15, 19},
        {-15, 7}, {-8, 1}, {0, 1},
        {8, 1}, {15, 6}, {15, 14},
        {15, 23}, {7, 26}, {2, 26},
        {-5, 26}, {-9, 21}, {-6, 16}
    };

    // Bass clef
    private static final float bc[][] =
    {
        {-2.3f,3},
        {6,7}, {10.5f,12}, {10.5f,16},
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

    // Note head
    private static final float hd[][] =
    {
        {8.0f, 0.0f},
        {8.0f, 8.0f}, {-8.0f, 8.0f}, {-8.0f, 0.0f},
        {-8.0f, -8.0f}, {8.0f, -8.0f}, {8.0f, 0.0f}
    };

    // Sharp symbol
    private static final float sp[][] =
    {
        {35, 35}, // 0
        {8, 22}, // 1
        {8, 46}, // 2
        {35, 59}, // 3
        {35, 101}, // 4
        {8, 88}, // 5
        {8, 111}, // 6
        {35, 125}, // 7
        {35, 160}, // 8
        {44, 160}, // 9
        {44, 129}, // 10
        {80, 147}, // 11
        {80, 183}, // 12
        {89, 183}, // 13
        {89, 151}, // 14
        {116, 165}, // 15
        {116, 141}, // 16
        {89, 127}, // 17
        {89, 86}, // 18
        {116, 100}, // 19
        {116, 75}, // 20
        {89, 62}, // 21
        {89, 19}, // 22
        {80, 19}, // 23
        {80, 57}, // 23
        {44, 39}, // 25
        {44, -1}, // 26
        {35, -1}, // 27
        {35, 35}, // 28
        {44, 64}, // 29
        {80, 81}, // 30
        {80, 123}, // 31
        {44, 105}, // 32
        {44, 64}, // 33
    };

    // Flat symbol
    private static final float ft[][] =
    {
        {20, 86}, // 0
        {28, 102.667f}, {41.6667f, 111}, {61, 111}, // 3
        {71.6667f, 111}, {80.3333f, 107.5f}, {87, 100.5f}, // 6
        {93.6667f, 93.5f}, {97, 83.6667f}, {97, 71}, // 9
        {97, 53}, {89, 36.6667f}, {73, 22}, // 12
        {57, 7.33333f}, {35.3333f, -1.33333f}, {8, -4}, // 15
        {8, 195}, // 16
        {20, 195}, // 17
        {20, 86}, // 18
        {20, 7}, // 19
        {35.3333f, 9}, {47.8333f, 15.6667f}, {57.5f, 27}, // 22
        {67.1667f, 38.3333f}, {72, 51.6667f}, {72, 67}, // 25
        {72, 75.6667f}, {70.1667f, 82.3333f}, {66.5f, 87}, // 28
        {62.8333f, 91.6667f}, {57.3333f, 94}, {50, 94}, // 31
        {41.3333f, 94}, {34.1667f, 90.3333f}, {28.5f, 83}, // 34
        {22.8333f, 75.6667f}, {20, 64.6667f}, {20, 50}, // 37
        {20, 7}, // 38
    };

    private static final int sharps[] =
    {
        NATURAL, SHARP, NATURAL, FLAT, NATURAL, NATURAL,
        SHARP, NATURAL, FLAT, NATURAL, FLAT, NATURAL
    };

    // Scale offsets
    private static final int offset[] =
    {
        0, 0, 1, 2, 2, 3,
        3, 4, 5, 5, 6, 6
    };

    private Path tclef;
    private Path bclef;
    private Path hnote;
    private Path sharp;
    private Path flat;

    private Matrix matrix;

    private float lineHeight;
    private float lineWidth;
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

        lineHeight = height / 14f;
        lineWidth = width / 16f;
        margin = width / 32;

        // Treble clef
        tclef = new Path();
        tclef.moveTo(tc[0][0], tc[0][1]);
        tclef.lineTo(tc[1][0], tc[1][1]);
        for (int i = 2; i < tc.length; i += 3)
            tclef.cubicTo(tc[i][0], tc[i][1],
                          tc[i + 1][0], tc[i + 1][1],
                          tc[i + 2][0], tc[i + 2][1]);

        // Bass clef
        bclef = new Path();
        bclef.moveTo(bc[0][0], bc[0][1]);
        for (int i = 1; i < 28; i += 3)
            bclef.cubicTo(bc[i][0], bc[i][1],
                          bc[i + 1][0], bc[i + 1][1],
                          bc[i + 2][0], bc[i + 2][1]);

        bclef.moveTo(bc[28][0], bc[28][1]);
        for (int i = 29; i < 35; i += 3)
            bclef.cubicTo(bc[i][0], bc[i][1],
                          bc[i + 1][0], bc[i + 1][1],
                          bc[i + 2][0], bc[i + 2][1]);

        bclef.moveTo(bc[35][0], bc[35][1]);
        for (int i = 36; i < bc.length; i += 3)
            bclef.cubicTo(bc[i][0], bc[i][1],
                          bc[i + 1][0], bc[i + 1][1],
                          bc[i + 2][0], bc[i + 2][1]);

        // Note head
        hnote = new Path();
        hnote.moveTo(hd[0][0], hd[0][1]);
        for (int i = 1; i < hd.length; i += 3)
            hnote.cubicTo(hd[i][0], hd[i][1],
                          hd[i + 1][0], hd[i + 1][1],
                          hd[i + 2][0], hd[i + 2][1]);

        // Sharp
        sharp = new Path();
        sharp.moveTo(sp[0][0], sp[0][1]);
        for (int i = 1; i < 28; i++)
            sharp.lineTo(sp[i][0], sp[i][1]);
        sharp.moveTo(sp[28][0], sp[28][1]);
        for (int i = 29; i < sp.length; i++)
            sharp.lineTo(sp[i][0], sp[i][1]);

        // Flat
        flat = new Path();
        flat.moveTo(ft[0][0], ft[0][1]);
        for (int i = 1; i < 15; i += 3)
            flat.cubicTo(ft[i][0], ft[i][1],
                         ft[i + 1][0], ft[i + 1][1],
                         ft[i + 2][0], ft[i + 2][1]);
        for (int i = 15; i < 19; i++)
            flat.lineTo(ft[i][0], ft[i][1]);
        flat.moveTo(ft[19][0], ft[19][1]);
        for (int i = 20; i < 37; i += 3)
            flat.cubicTo(ft[i][0], ft[i][1],
                         ft[i + 1][0], ft[i + 1][1],
                         ft[i + 2][0], ft[i + 2][1]);
        flat.lineTo(ft[38][0], ft[38][1]);

        RectF bounds = new RectF();

        // Scale treble clef
        tclef.computeBounds(bounds, false);
        matrix = new Matrix();
        matrix.setTranslate(-(bounds.left + bounds.right) / 2,
                            -(bounds.top + bounds.bottom) / 2);
        tclef.transform(matrix);
        float scale = (height / 2) / (bounds.top - bounds.bottom);
        matrix.setScale(-scale, scale);
        matrix.postTranslate(margin + lineWidth / 2, - lineHeight * 3);
        tclef.transform(matrix);

        // Scale bass clef
        bclef.computeBounds(bounds, false);
        matrix.setTranslate(-(bounds.left + bounds.right) / 2,
                            -(bounds.top + bounds.bottom) / 2);
        bclef.transform(matrix);
        scale = (lineHeight * 4) / (bounds.top - bounds.bottom);
        matrix.setScale(-scale, scale);
        matrix.postTranslate(margin + lineWidth / 2, lineHeight * 3);
        bclef.transform(matrix);

        // Scale note head
        hnote.computeBounds(bounds, false);
        scale = (lineHeight * 1.5f) / (bounds.top - bounds.bottom);
        matrix.setScale(-scale, scale);
        hnote.transform(matrix);

        // Scale sharp
        sharp.computeBounds(bounds, false);
        matrix.setTranslate(-(bounds.left + bounds.right) / 2,
                            -(bounds.top + bounds.bottom) / 2);
        sharp.transform(matrix);
        scale = (lineHeight * 3) / (bounds.top - bounds.bottom);
        matrix.setScale(-scale, scale);
        matrix.postTranslate(-lineWidth / 2, 0);
        sharp.transform(matrix);

        // Scale flat
        flat.computeBounds(bounds, false);
        matrix.setTranslate(-(bounds.left + bounds.right) / 2,
                            -(bounds.top + bounds.bottom) / 2);
        flat.transform(matrix);
        scale = (lineHeight * 3) / (bounds.top - bounds.bottom);
        matrix.setScale(-scale, scale);
        matrix.postTranslate(-lineWidth / 2, -lineHeight / 2);
        flat.transform(matrix);
    }

    // On draw
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // Don't draw if no audio
        if (audio == null)
            return;

        // Set up paint
        paint.setStrokeWidth(2);
        paint.setColor(textColour);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(lineHeight * 4);
        paint.setTextAlign(Paint.Align.LEFT);

        // Draw staff
        canvas.translate(0, height / 2f);
        for (int i = 1; i < 6; i++)
        {
            canvas.drawLine(margin, i * lineHeight,
                            width - margin, i * lineHeight, paint);
            canvas.drawLine(margin, i * -lineHeight,
                            width - margin, i * -lineHeight, paint);
        }

        // Draw leger lines
        canvas.drawLine((width / 2) - (lineWidth * 0.5f), 0,
                        (width / 2) + (lineWidth * 0.5f), 0, paint);

        canvas.drawLine((width / 2) + (lineWidth * 5.5f),
                        -lineHeight * 6,
                        (width / 2) + (lineWidth * 6.5f),
                        -lineHeight * 6, paint);

        canvas.drawLine((width / 2) - (lineWidth * 5.5f),
                        lineHeight * 6,
                        (width / 2) - (lineWidth * 6.5f),
                        lineHeight * 6, paint);

        // Draw treble and bass clef
        canvas.drawPath(tclef, paint);
        canvas.drawPath(bclef, paint);

        // Calculate transform for note
        float xBase = lineWidth * 14;
        float yBase = lineHeight * 14;
        int note = audio.note - audio.transpose;
        int index = (note + OCTAVE) % OCTAVE;
        int octave = note / OCTAVE;

        // Wrap top two octaves
        if (octave >= 6)
            octave -= 2;

        // Wrap C0
        else if (octave == 0 && index <= 1)
            octave += 4;

        // Wrap bottom two octaves
        else if (octave <= 1 || octave == 2 && index <= 1)
            octave += 2;

        float dx = (octave * lineWidth * 3.5f) +
            (offset[index] * (lineWidth / 2));
        float dy = (octave * lineHeight * 3.5f) +
            (offset[index] * (lineHeight / 2));

        // Translate canvas
        canvas.translate((width / 2) - xBase + dx, yBase - dy);

        // Draw note and accidental
        canvas.drawPath(hnote, paint);
        switch (sharps[index])
        {
            // Natural
        case NATURAL:
            // Do nothing
            break;

            // Sharp
        case SHARP:
            canvas.drawPath(sharp, paint);
            break;

            // Flat
        case FLAT:
            canvas.drawPath(flat, paint);
            break;
        }
    }
}
