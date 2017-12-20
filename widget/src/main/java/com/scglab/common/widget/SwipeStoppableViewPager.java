package com.scglab.common.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by sh on 2016. 5. 26..
 */
public class SwipeStoppableViewPager extends ViewPager {
	private boolean swipe = true;

	public SwipeStoppableViewPager(Context context) {
		super(context);
	}

	public SwipeStoppableViewPager(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if (swipe) {
			try {
				return super.onInterceptTouchEvent(e);
			} catch (IllegalArgumentException ignored) {
			}
		}

		return false;
	}

	public void setSwipeable(boolean value) {
		swipe = value;
	}

}
