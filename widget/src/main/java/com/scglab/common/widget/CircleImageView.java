package com.scglab.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.scglab.common.util.BitmapHelper;

/**
 * Created by sunghun on 2016.
 */

/**
 * @deprecated Use {@link RoundedImageView} instead.
 */
public class CircleImageView extends NetworkImageView {

	//--------------------------------------------------------------------------
	//
	//	Instance variables
	//
	//--------------------------------------------------------------------------

	private int frameColor;
	private int frameStroke;
	private int circleSize;

	//--------------------------------------------------------------------------
	//
	//	Instance methods
	//
	//--------------------------------------------------------------------------

	//--------------------------------------------------------------------------
	//	Constructor
	//--------------------------------------------------------------------------

	public CircleImageView(Context context) {
		super(context);
		initEmptyImage();
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initEmptyImage();
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initEmptyImage();
	}

	public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initEmptyImage();
	}

	//--------------------------------------------------------------------------
	//	Override:View
	//--------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(circleSize, circleSize);
	}

	//--------------------------------------------------------------------------
	//	Internal
	//--------------------------------------------------------------------------

	protected int[] getStyleable() {
		return R.styleable.CircleImageView;
	}

	protected void initStyle(TypedArray typedArray) {
		super.initStyle(typedArray);

		float density = getResources().getDisplayMetrics().density;

		frameColor = typedArray.getColor(R.styleable.CircleImageView_frameColor, Color.WHITE);
		frameStroke = typedArray.getInt(R.styleable.CircleImageView_frameStroke, 0);
		frameStroke = (int) (density * (float) frameStroke);

		circleSize = typedArray.getInt(R.styleable.CircleImageView_circleSize, 100);
		circleSize = (int) (density * (float) circleSize);
	}

	protected Point getResultSize() {
		float tempWidth, tempHeight;

		if (null != drawImage) {
			tempWidth = drawImage.getWidth();
			tempHeight = drawImage.getHeight();
		} else if (getEmptyImage().hasImage()) {
			tempWidth = getEmptyImage().getBitmap().getWidth();
			tempHeight = getEmptyImage().getBitmap().getHeight();
		} else {
			return new Point(circleSize, circleSize);
		}

		float rate;
		if (tempWidth > tempHeight) rate = tempHeight / circleSize;
		else rate = tempWidth / circleSize;

		tempWidth = (tempWidth / rate);
		tempHeight = (tempHeight / rate);

		return new Point((int) (tempWidth), (int) (tempHeight));
	}

	protected void retouchDrawImage(Point point) {
		super.retouchDrawImage(point);

		drawImage = retouch.run(drawImage);
	}

	private void initEmptyImage() {
		if (getEmptyImage().hasImage()) {
			getEmptyImage().retouch(retouch);
		}
	}

	private Retouch retouch = new Retouch() {
		@Override
		public Bitmap run(Bitmap bitmap) {
			return BitmapHelper.circle(bitmap, circleSize, frameStroke, frameColor);
		}
	};
}
