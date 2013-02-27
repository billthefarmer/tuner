package org.billthefarmer.tuner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class PreferenceView extends View
{

	protected static int maxWidth;

	// Constructor

	public PreferenceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		maxWidth = 0;
	}

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
    // Get the largest width offered so a valid calculation can be made

    int w = MeasureSpec.getSize(widthMeasureSpec);

    if (maxWidth < w)
    	maxWidth = w;

	setMeasuredDimension(maxWidth / 4, maxWidth / 4);
    }
}
