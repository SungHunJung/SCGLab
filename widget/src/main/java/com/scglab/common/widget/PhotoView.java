package com.scglab.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class PhotoView extends View {

	//--------------------------------------------------------------------------
	//
	//	Instance variables
	//
	//--------------------------------------------------------------------------

	private Paint paint;
	private int pointColor;
	private int halfBlackColor;

	private Rect bitmapSrc;
	private Rect bitmapDst;
	private Bitmap bitmap;

	private boolean isCropMode;
	private boolean isRateMode;
	private Rect cropRect;
	private Rect cropMode;
	private PointF cropLeft;
	private PointF cropRight;
	private PointF cropTop;
	private PointF cropBottom;
	private PointF cropRightBottom;

	private double zoomStart = 1;
	private PointF firstZoomPoint;
	private PointF currentZoomPoint;
	private PointF cropTouchPoint;
	private PointF firstMovePoint;
	private Point currentMovePoint;

	private Point displaySize;
	private float zoomLevel = 1.0f;
	private float zoomStartDistance = Float.MIN_VALUE;
	private PointF[] zoomTouchPoints = new PointF[2];

	//--------------------------------------------------------------------------
	//
	// Instance methods
	//
	//--------------------------------------------------------------------------

	//--------------------------------------------------------------------------
	//	Constructor
	//--------------------------------------------------------------------------

	public PhotoView(Context context) {
		super(context);
	}

	public PhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	//--------------------------------------------------------------------------
	//	Override:View
	//--------------------------------------------------------------------------

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		init();
		invalidate();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (null == displaySize) {
			displaySize = new Point(canvas.getWidth(), canvas.getHeight());
		}

		if (null == bitmap) {
			return;
		}

		//size
		if (null == bitmapSrc || null == bitmapDst) {
			bitmapSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			float scale = (float) canvas.getWidth() / (float) bitmapSrc.width();
			int top = (canvas.getHeight() - (int) (bitmap.getHeight() * scale)) / 2;
			int height = top + (int) (bitmap.getHeight() * scale);

			if (height > canvas.getHeight()) {
				scale = (float) canvas.getHeight() / (float) bitmapSrc.height();
				int left = (canvas.getWidth() - (int) (bitmap.getWidth() * scale)) / 2;
				bitmapDst = new Rect(left, 0, left + (int) (bitmap.getWidth() * scale), canvas.getHeight());
			} else {
				bitmapDst = new Rect(0, top, canvas.getWidth(), top + (int) (bitmap.getHeight() * scale));
			}
		}

		//move
		/*
		if (null != currentMovePoint) {
			bitmapDst.left -= currentMovePoint.x;
			bitmapDst.right -= currentMovePoint.x;
			bitmapDst.top -= currentMovePoint.y;
			bitmapDst.bottom -= currentMovePoint.y;
		}
		*/

		//scale
//		canvas.scale(zoomLevel, zoomLevel, canvas.getWidth() / 2, canvas.getHeight() / 2);
//		if (null != currentZoomPoint) {
//			canvas.scale(zoomLevel, zoomLevel, currentZoomPoint.x, currentZoomPoint.y);
//		}

		//bitmap
		canvas.drawBitmap(bitmap, bitmapSrc, bitmapDst, paint);

		//crop
		if (isCropMode) {
			if (null == cropRect) {
				int width = bitmapDst.width();
				int height = bitmapDst.height();
				int size = (int) ((width < height ? width : height) / 1.25f);
				cropRect = new Rect(
						bitmapDst.left + width / 2 - size / 2,
						bitmapDst.top + height / 2 - size / 2,
						bitmapDst.left + width / 2 + size / 2,
						bitmapDst.top + height / 2 + size / 2);

				cropLeft = new PointF(cropRect.left, (cropRect.top + cropRect.bottom) / 2);
				cropRight = new PointF(cropRect.right, (cropRect.top + cropRect.bottom) / 2);
				cropTop = new PointF((cropRect.left + cropRect.right) / 2, cropRect.top);
				cropBottom = new PointF((cropRect.left + cropRect.right) / 2, cropRect.bottom);
				cropRightBottom = new PointF(cropRight.x, cropBottom.y);
			}

			//bg
			paint.setColor(halfBlackColor);
			canvas.drawRect(0, 0, canvas.getWidth(), cropRect.top, paint);
			canvas.drawRect(0, cropRect.top, cropRect.left, cropRect.bottom, paint);
			canvas.drawRect(cropRect.right, cropRect.top, canvas.getWidth(), cropRect.bottom, paint);
			canvas.drawRect(0, cropRect.bottom, canvas.getWidth(), canvas.getHeight(), paint);

			//select(box)
			paint.setStrokeWidth(10);
			paint.setColor(pointColor);
			canvas.drawLine(cropRect.left, cropRect.top, cropRect.right, cropRect.top, paint);
			canvas.drawLine(cropRect.left, cropRect.bottom, cropRect.right, cropRect.bottom, paint);
			canvas.drawLine(cropRect.left, cropRect.top, cropRect.left, cropRect.bottom, paint);
			canvas.drawLine(cropRect.right, cropRect.top, cropRect.right, cropRect.bottom, paint);
			paint.setAlpha(127);
			paint.setStrokeWidth(5);
			canvas.drawLine(cropRect.left + (cropRect.width() / 3), cropRect.top, cropRect.left + (cropRect.width() / 3), cropRect.bottom, paint);
			canvas.drawLine(cropRect.right - (cropRect.width() / 3), cropRect.top, cropRect.right - (cropRect.width() / 3), cropRect.bottom, paint);
			canvas.drawLine(cropRect.left, cropRect.top + (cropRect.height() / 3), cropRect.right, cropRect.top + (cropRect.height() / 3), paint);
			canvas.drawLine(cropRect.left, cropRect.bottom - (cropRect.height() / 3), cropRect.right, cropRect.bottom - (cropRect.height() / 3), paint);
			paint.setAlpha(255);

			//point
			if (isRateMode) {
				canvas.drawCircle(cropRightBottom.x, cropRightBottom.y, 30, paint);
			} else {
				canvas.drawCircle(cropLeft.x, cropLeft.y, 30, paint);
				canvas.drawCircle(cropRight.x, cropRight.y, 30, paint);
				canvas.drawCircle(cropTop.x, cropTop.y, 30, paint);
				canvas.drawCircle(cropBottom.x, cropBottom.y, 30, paint);
			}
			if (null != cropMode) {
				paint.setAlpha(200);
				if (isRateMode) {
					if (cropMode.right == 1 && cropMode.bottom == 1 && cropMode.left == 0 && cropMode.top == 0) canvas.drawCircle(cropRightBottom.x, cropRightBottom.y, 60, paint);
				} else {
					if (cropMode.left == 1) canvas.drawCircle(cropLeft.x, cropLeft.y, 60, paint);
					else if (cropMode.top == 1) canvas.drawCircle(cropTop.x, cropTop.y, 60, paint);
					else if (cropMode.right == 1) canvas.drawCircle(cropRight.x, cropRight.y, 60, paint);
					else if (cropMode.bottom == 1) canvas.drawCircle(cropBottom.x, cropBottom.y, 60, paint);
				}
				paint.setAlpha(255);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (bitmapSrc == null) return false;

		if (isCropMode) {
			cropEventProcess(event);
		} else {
			zoomEventProcess(event);
			/*
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					zoomStart = -1;
					firstMovePoint = new PointF(point1.x, point1.y);
					currentMovePoint = new Point();
					break;

				case MotionEvent.ACTION_MOVE:
					if (event.getPointerCount() == 1) {
						if (null != firstMovePoint) {
							currentMovePoint.x = (int) ((int) (firstMovePoint.x - point1.x) / zoomLevel);
							currentMovePoint.y = (int) ((int) (firstMovePoint.y - point1.y) / zoomLevel);
							firstMovePoint.x = point1.x;
							firstMovePoint.y = point1.y;
						}
					} else if (event.getPointerCount() == 2) {
						firstMovePoint = null;
						currentMovePoint = null;

						PointF point2 = new PointF(event.getX(1), event.getY(1));
						PointF centerPoint = new PointF((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);
						double distance = getDistance(point1, point2);

						if (zoomStart == -1) {
							firstZoomPoint = centerPoint;
							zoomStart = distance;
						} else {
							zoomLevel += (distance - zoomStart) / (bitmapSrc.width() / 2);
							zoomStart = distance;

							if (zoomLevel < 1) zoomLevel = 1;
							else if (zoomLevel > 10) zoomLevel = 10;

							if (null == currentZoomPoint) currentZoomPoint = new PointF(0, 0);
//							currentZoomPoint.x = (int) ((int) (firstZoomPoint.x - centerPoint.x) / 1);
//							currentZoomPoint.y = (int) ((int) (firstZoomPoint.y - centerPoint.y) / 1);
							currentZoomPoint.x = centerPoint.x;
							currentZoomPoint.y = centerPoint.y;
						}
					}

					invalidate();
					break;

				case MotionEvent.ACTION_UP:
					zoomStart = -1;
					firstMovePoint = null;
					currentMovePoint = null;
					invalidate();
					break;
			}
			*/
		}

		return true;
	}

	private void cropEventProcess(MotionEvent event) {
		PointF point = new PointF(event.getX(0), event.getY(0));

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				cropTouchPoint = new PointF(point.x, point.y);

				cropStart(point);
				invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				cropChanged(point);

				cropTouchPoint.x = point.x;
				cropTouchPoint.y = point.y;
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				cropMode = null;
				invalidate();

				break;
		}
	}

	private void zoomEventProcess(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (zoomLevel == 1) {
					setPivotX(0);
					setPivotY(0);
				}

				if (zoomTouchPoints[0] == null) {
					zoomTouchPoints[0] = new PointF(event.getX(0), event.getY(0));
				}

				if (zoomTouchPoints[1] == null && event.getPointerCount() == 2) {
					zoomTouchPoints[1] = new PointF(event.getX(1), event.getY(1));
					return;
				}

				float pivotX = getPivotX();
				float pivotY = getPivotY();

				if (zoomTouchPoints[1] == null && zoomLevel != 1) {
					pivotX = pivotX + zoomTouchPoints[0].x - event.getX(0);
					pivotY = pivotY + zoomTouchPoints[0].y - event.getY(0);
				} else if (zoomTouchPoints[0] != null && zoomTouchPoints[1] != null && event.getPointerCount() >= 2) {
					float distanceX = event.getX(0) - event.getX(1);
					float distanceY = event.getY(0) - event.getY(1);
					float distance = (float) Math.sqrt(Math.abs(distanceX * distanceX) + Math.abs(distanceY * distanceY));

					if (zoomStartDistance == Float.MIN_VALUE) zoomStartDistance = distance;

					zoomLevel += (distance - zoomStartDistance) / 400;
					if (zoomLevel > 10) zoomLevel = 10;
					if (zoomLevel < 1) zoomLevel = 1;
					setScaleX(zoomLevel);
					setScaleY(zoomLevel);

					pivotX += (zoomTouchPoints[0].x - event.getX(0)) + (zoomTouchPoints[1].x - event.getX(1));
					pivotY += (zoomTouchPoints[0].y - event.getY(0)) + (zoomTouchPoints[1].y - event.getY(1));
				}

				pivotX = pivotX < 0 ? 0 : pivotX;
				pivotX = pivotX > displaySize.x ? displaySize.x : pivotX;
				setPivotX(pivotX);

				setPivotY(pivotY);
				break;

			case MotionEvent.ACTION_UP:
				zoomStartDistance = Float.MIN_VALUE;
				zoomTouchPoints[0] = null;
				zoomTouchPoints[1] = null;
				break;
		}
	}

	//--------------------------------------------------------------------------
	//	Public
	//--------------------------------------------------------------------------

	public void clear() {
		bitmap = null;
		invalidate();
	}

	public void setBitmap(Bitmap bitmap) {
		if (null == bitmap) {
			return;
		}

		this.bitmap = bitmap;

		bitmapSrc = null;
		bitmapDst = null;

		isCropMode = false;
		cropRect = null;

		invalidate();
	}

	public float getZoomLevel() {
		return zoomLevel;
	}

	public void startCropMode(boolean isRate) {
		if (isCropMode) {
			return;
		}

		isRateMode = isRate;
		isCropMode = true;
		cropRect = null;

		invalidate();
	}

	public void stopCropMode(boolean isSave) {
		if (!isCropMode) {
			return;
		}

		Bitmap bitmap = null;
		if (isSave) bitmap = getBitmap();

		isCropMode = false;
		cropRect = null;

		if (null != bitmap) setBitmap(bitmap);
		else invalidate();
	}

	public void rotate(int value) {
		stopCropMode(false);

		Matrix matrix = new Matrix();
		matrix.postRotate(value);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		bitmapSrc = null;
		bitmapDst = null;
		invalidate();
	}

	public Bitmap getBitmap() {
		if (isCropMode) {
			float widthScale = (float) cropRect.width() / (float) bitmapDst.width();
			float heightScale = (float) cropRect.height() / (float) bitmapDst.height();
			float newWidth = bitmapSrc.width() * widthScale;
			float newHeight = bitmapSrc.height() * heightScale;

			widthScale = (float) (cropRect.left - bitmapDst.left) / (float) bitmapDst.width();
			heightScale = (float) (cropRect.top - bitmapDst.top) / (float) bitmapDst.height();
			float x = bitmapSrc.width() * widthScale;
			float y = bitmapSrc.height() * heightScale;

			bitmap = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) newWidth, (int) newHeight);
		}

		bitmapSrc = null;
		bitmapDst = null;
		invalidate();

		return bitmap;
	}

	//--------------------------------------------------------------------------
	//	Internal
	//--------------------------------------------------------------------------

	private void init() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(5);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);

		Resources resources = getResources();

		pointColor = resources.getColor(android.R.color.white);
		halfBlackColor = Color.argb(255 / 2, 0, 0, 0);

		/*
		Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
		displaySize = new Point();
		display.getSize(displaySize);
		*/
	}

	private void cropStart(PointF point) {
		final double leftDistance = getDistance(cropLeft, point);
		final double rightDistance = getDistance(cropRight, point);
		final double topDistance = getDistance(cropTop, point);
		final double bottomDistance = getDistance(cropBottom, point);
		final double rightBottomDistance = getDistance(cropRightBottom, point);
		final double minDistance = Math.min(Math.min(Math.min(Math.min(leftDistance, rightDistance), topDistance), bottomDistance), rightBottomDistance);
		final double minDistanceDP = minDistance / getContext().getResources().getDisplayMetrics().density;

		if (minDistanceDP < 50) {
			//리사이즈
			if (isRateMode) {
				if (minDistance == rightBottomDistance) cropMode = new Rect(0, 0, 1, 1);
				else cropMode = new Rect(0, 0, 0, 0);
			} else {
				if (minDistance == leftDistance) cropMode = new Rect(1, 0, 0, 0);
				else if (minDistance == rightDistance) cropMode = new Rect(0, 0, 1, 0);
				else if (minDistance == topDistance) cropMode = new Rect(0, 1, 0, 0);
				else if (minDistance == bottomDistance) cropMode = new Rect(0, 0, 0, 1);
			}
		} else {
			//이동
			cropMode = new Rect(0, 0, 0, 0);
		}
	}

	private void cropChanged(PointF point) {
		if (null == cropMode) return;

		Rect tempRect = new Rect(cropRect);
		final int minSize = 50;

		if (cropMode.right == 1 && cropMode.bottom == 1 && cropMode.left == 0 && cropMode.top == 0) {
			//resize - rate
			float temp = Math.min(cropRightBottom.x - point.x, cropRightBottom.y - point.y);

			float tempRight;
			float tempBottom;
			boolean isValid;
			do {
				isValid = true;

				tempRight = tempRect.right - temp;
				tempBottom = tempRect.bottom - temp;

				if (tempRight > bitmapDst.right) {
					temp = tempRect.right - bitmapDst.right;
					isValid = false;
				}
				if (tempBottom > bitmapDst.bottom) {
					temp = tempRect.bottom - bitmapDst.bottom;
					isValid = false;
				}
				if (tempRight < tempRect.left + minSize) {
					temp -= tempRect.left + minSize - tempRight;
					isValid = false;
				}
			} while (!isValid);

			tempRect.right = (int) (tempRect.right - temp);
			tempRect.bottom = (int) (tempRect.bottom - temp);
		} else if (cropMode.left == 1) {
			//resize - left
			tempRect.left -= (int) (cropTouchPoint.x - point.x);
			if (tempRect.left < bitmapDst.left) tempRect.left = bitmapDst.left;
			if (tempRect.left > tempRect.right - minSize) tempRect.left = tempRect.right - minSize;
		} else if (cropMode.right == 1) {
			//resize - right
			tempRect.right -= (int) (cropTouchPoint.x - point.x);
			if (tempRect.right > bitmapDst.right) tempRect.right = bitmapDst.right;
			if (tempRect.right < tempRect.left + minSize) tempRect.right = tempRect.left + minSize;
		} else if (cropMode.top == 1) {
			//resize - top
			tempRect.top -= (int) (cropTouchPoint.y - point.y);
			if (tempRect.top < bitmapDst.top) tempRect.top = bitmapDst.top;
			if (tempRect.top > tempRect.bottom - minSize) tempRect.top = tempRect.bottom - minSize;
		} else if (cropMode.bottom == 1) {
			//resize - bottom
			tempRect.bottom -= (int) (cropTouchPoint.y - point.y);
			if (tempRect.bottom > bitmapDst.bottom) tempRect.bottom = bitmapDst.bottom;
			if (tempRect.bottom < tempRect.top + minSize) tempRect.bottom = tempRect.top + minSize;
		} else {
			//이동
			tempRect.right -= (int) (cropTouchPoint.x - point.x);
			tempRect.top -= (int) (cropTouchPoint.y - point.y);
			tempRect.left -= (int) (cropTouchPoint.x - point.x);
			tempRect.bottom -= (int) (cropTouchPoint.y - point.y);

			if (tempRect.left < bitmapDst.left) {
				tempRect.left = bitmapDst.left;
				tempRect.right = bitmapDst.left + cropRect.width();
			}
			if (tempRect.right > bitmapDst.right) {
				tempRect.right = bitmapDst.right;
				tempRect.left = bitmapDst.right - cropRect.width();
			}
			if (tempRect.top < bitmapDst.top) {
				tempRect.top = bitmapDst.top;
				tempRect.bottom = bitmapDst.top + cropRect.height();
			}
			if (tempRect.bottom > bitmapDst.bottom) {
				tempRect.bottom = bitmapDst.bottom;
				tempRect.top = bitmapDst.bottom - cropRect.height();
			}
		}

		//적용
		cropRect.left = tempRect.left;
		cropRect.right = tempRect.right;
		cropRect.top = tempRect.top;
		cropRect.bottom = tempRect.bottom;

		//엣지
		cropLeft.x = cropRect.left;
		cropLeft.y = (cropRect.top + cropRect.bottom) / 2;
		cropRight.x = cropRect.right;
		cropRight.y = (cropRect.top + cropRect.bottom) / 2;
		cropTop.x = (cropRect.left + cropRect.right) / 2;
		cropTop.y = cropRect.top;
		cropBottom.x = (cropRect.left + cropRect.right) / 2;
		cropBottom.y = cropRect.bottom;
		cropRightBottom.x = cropRight.x;
		cropRightBottom.y = cropBottom.y;
	}

	private double getDistance(PointF p1, PointF p2) {
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

}