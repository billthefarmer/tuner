package org.billthefarmer.tuner;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

abstract public class AAnimatedView extends View
    implements ValueAnimator.AnimatorUpdateListener
{
    /**
     * Update the internal value used used for draw
     * @param isAnimatorWorking is true if device animations are enabled
     * @return return true if a invalidate method should be call
     */
    protected abstract boolean updateInternalValues(boolean isAnimatorWorking);

    protected ValueAnimator animator;
    private boolean isAnimatorWorking;

    public AAnimatedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        isAnimatorWorking = false;
    }

    public void startAnimator()
    {
        // Create animator
        animator = ValueAnimator.ofInt(0, 10000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(10000);

        // Update the display
        animator.addUpdateListener(this);

        // Start the animator
        animator.start();
    }

    /**
     * Manually trig animation
     */
    public void forceAnimationTrigger()
    {
        if (!isAnimatorWorking)
        {
            if (updateInternalValues(isAnimatorWorking))
                postInvalidate();
        }
    }

    // Animation update
    @Override
    public void onAnimationUpdate(ValueAnimator animator)
    {
        // Only two shots at value min and max if animator not working
        int animatedValue = ((Integer)animator.getAnimatedValue()).intValue();
        isAnimatorWorking |= (animatedValue > 0 && animatedValue < 10000);

        if (updateInternalValues(isAnimatorWorking))
            invalidate();
    }
}
