package com.scglab.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sunghun on 2017. 12. 8..
 */

public class TestView extends View {
	public TestView(Context context) {
		super(context);
	}

	public TestView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	int[] color = {Color.BLUE, Color.BLACK, Color.CYAN, Color.GREEN, Color.RED};
	int count = 1;
	Paint paint = new Paint();

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		paint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		paint.setColor(color[count++ % color.length]);

//		if (null != dirty) canvas.clipRect(dirty);
//		canvas.drawColor(color[count++ % color.length]);

		if(count % 2 ==0)
		canvas.drawRect(canvas.getWidth() / 2, 0, canvas.getWidth(), canvas.getHeight(), paint);
		else
		canvas.drawRect(0, 0, canvas.getWidth() / 2, canvas.getHeight(), paint);
	}

	private Point startPoint = new Point();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startPoint.set((int) event.getX(), (int) event.getY());
				break;

			case MotionEvent.ACTION_UP:
				invalidate(new Rect(startPoint.x, startPoint.y, (int) event.getX(), (int) event.getY()));
				break;
		}
		return true;
	}

	Rect dirty;

	@Override
	public void invalidate() {
		dirty = null;
		super.invalidate();
	}

	@Override
	public void invalidate(Rect dirty) {
		this.dirty = dirty;
		super.invalidate(dirty);
	}

}
