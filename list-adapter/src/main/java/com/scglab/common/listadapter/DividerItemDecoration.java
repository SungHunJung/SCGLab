package com.scglab.common.listadapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sh on 2016. 4. 5..
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
	private final Drawable DIVIDER;

	public DividerItemDecoration(Context context) {
		final TypedArray styledAttributes = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
		DIVIDER = styledAttributes.getDrawable(0);
		styledAttributes.recycle();
	}

	public DividerItemDecoration(Context context, int resId) {
		DIVIDER = ContextCompat.getDrawable(context, resId);
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		int left = parent.getPaddingLeft();
		int right = parent.getWidth() - parent.getPaddingRight();

		View child;
		int length = parent.getChildCount();
		for (int index = 0; index < length; index++) {
			child = parent.getChildAt(index);

			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

			int top = child.getBottom() + params.bottomMargin;
			int bottom = top + DIVIDER.getIntrinsicHeight();

			DIVIDER.setBounds(left, top, right, bottom);
			DIVIDER.draw(c);
		}
	}
}
