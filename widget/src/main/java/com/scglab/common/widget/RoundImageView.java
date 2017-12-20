package com.scglab.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

public class RoundImageView extends NetworkImageView {

	//--------------------------------------------------------------------------
	//
	//	Instance variables
	//
	//--------------------------------------------------------------------------

	private float[] cornerSize;

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
		initEmptyImage();
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initEmptyImage();
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initEmptyImage();
	}

	public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initEmptyImage();
	}

	//--------------------------------------------------------------------------
	//	Internal
	//--------------------------------------------------------------------------

	protected float[] getCornerSize() {
		return cornerSize;
	}

	protected int[] getStyleable() {
		return R.styleable.RoundImageView;
	}

	protected void initStyle(TypedArray typedArray) {
		super.initStyle(typedArray);

		float density = getResources().getDisplayMetrics().density;

		cornerSize = new float[8];
		float base = typedArray.getFloat(R.styleable.RoundImageView_cornerSize, 0);
		cornerSize[0] = cornerSize[1] = typedArray.getFloat(R.styleable.RoundImageView_cornerSizeTopLeft, base) * density;
		cornerSize[2] = cornerSize[3] = typedArray.getFloat(R.styleable.RoundImageView_cornerSizeTopRight, base) * density;
		cornerSize[4] = cornerSize[5] = typedArray.getFloat(R.styleable.RoundImageView_cornerSizeBottomRight, base) * density;
		cornerSize[6] = cornerSize[7] = typedArray.getFloat(R.styleable.RoundImageView_cornerSizeBottomLeft, base) * density;
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
			Path clipPath = new Path();

			RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
			clipPath.addRoundRect(rect, cornerSize, Path.Direction.CW);

			Bitmap image = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(image);
			canvas.save();
			canvas.clipPath(clipPath);
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.restore();

			return image;
		}
	};
}
