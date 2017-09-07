package com.scglab.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;

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
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
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

		Point point = getResultSize();
		emptyImage = Bitmap.createScaledBitmap(emptyImage, point.x, point.y, true);
//		emptyImage = createCircleBitmap(emptyImage);

		//1 emptyImage = BitmapHelper.circle(emptyImage, circleSize, frameStroke, frameColor);
	}

	protected Point getResultSize() {
		float tempWidth,tempHeight;

		if (null != drawImage) {
			  tempWidth = drawImage.getWidth();
			  tempHeight = drawImage.getHeight();
		} else {
			  tempWidth = emptyImage.getWidth();
			  tempHeight = emptyImage.getHeight();
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

		//2 drawImage = BitmapHelper.circle(drawImage, circleSize, frameStroke, frameColor);
	}

	/*
	private Bitmap createCircleBitmap(Bitmap bitmap) {
		Bitmap image = Bitmap.createBitmap(circleSize, circleSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		canvas.save();

		float temp;
		final float x = (int) ((canvas.getWidth() - circleSize) / 2.0f);

		//이미지 영역 잡기
		paint.setColor(frameColor);
		paint.setStyle(Paint.Style.FILL);
		temp = ((float) (circleSize) / 2.0f - frameStroke);
		canvas.drawCircle(x + temp + frameStroke, temp + frameStroke, temp, paint);

		//이미지
		if (null != bitmap) {
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, x + (float) (circleSize - image.getWidth()) / 2.0f, (float) (circleSize - image.getHeight()) / 2.0f, paint);
		}

		//프레임
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		if (circleSize > 0) {
			paint.setColor(frameColor);
			paint.setStyle(Paint.Style.FILL);
			temp = ((float) (circleSize) / (2.0f));
			canvas.drawCircle(x + temp, temp, temp, paint);
		}

		canvas.restore();

		return image;
	}
	*/
}
