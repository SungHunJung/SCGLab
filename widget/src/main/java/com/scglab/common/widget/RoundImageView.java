package com.scglab.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RoundImageView extends NetworkImageView {

	//--------------------------------------------------------------------------
	//
	//	Instance variables
	//
	//--------------------------------------------------------------------------

	private float cornerSize;
	private float maxSize;
	private float minSize;

	//--------------------------------------------------------------------------
	//
	//	Instance methods
	//
	//--------------------------------------------------------------------------

	//--------------------------------------------------------------------------
	//	Constructor
	//--------------------------------------------------------------------------

	public RoundImageView(Context context) {
		super(context);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	//--------------------------------------------------------------------------
	//	Internal
	//--------------------------------------------------------------------------

	protected float getCornerSize() {
		return cornerSize;
	}

	protected int[] getStyleable() {
		return R.styleable.RoundImageView;
	}

	protected Point getResultSize() {
		if (null == drawImage && emptyImage != null) {
			return new Point(emptyImage.getWidth(), emptyImage.getHeight());
		}

		Point point = super.getResultSize();

		float rate = 1;
		if (point.x > maxSize || point.y > maxSize) {
			if (point.x > point.y) rate = (float) point.x / maxSize;
			else rate = (float) point.y / maxSize;
		}

		if (minSize != -1) {
			if (point.x < minSize || point.y < minSize) {
				if (point.x > point.y) rate = (float) point.x / maxSize;
				else rate = (float) point.y / maxSize;
			}
		}

		return new Point((int) ((float) point.x / rate), (int) ((float) point.y / rate));
	}

	protected void initStyle(TypedArray typedArray) {
		super.initStyle(typedArray);

		float density = getResources().getDisplayMetrics().density;

		cornerSize = typedArray.getFloat(R.styleable.RoundImageView_cornerSize, 10);
		cornerSize = (int) (density * cornerSize);

		maxSize = typedArray.getFloat(R.styleable.RoundImageView_maxSize, 200);
		maxSize = (int) (density * maxSize);

		minSize = typedArray.getFloat(R.styleable.RoundImageView_minSize, -1);
		minSize = (int) (density * minSize);

		Point point = getResultSize();
		emptyImage = Bitmap.createScaledBitmap(emptyImage, point.x, point.y, true);
		emptyImage = roundBitmap(emptyImage);
	}

	protected void retouchDrawImage(Point point) {
		super.retouchDrawImage(point);

		drawImage = roundBitmap(drawImage);
	}

	protected Bitmap roundBitmap(Bitmap bitmap) {
		Path clipPath = new Path();

		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		clipPath.addRoundRect(rect, cornerSize, cornerSize, Path.Direction.CW);

		Bitmap image = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		canvas.save();
		canvas.clipPath(clipPath);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();

		return image;
	}
}
