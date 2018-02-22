package com.scglab.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by sunghun on 2017.
 */

public class RoundedImageView extends View {

	private Source source;
	private CornerSize cornerSize;

	public RoundedImageView(Context context) {
		super(context);
		init(null);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, 0, 0));
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, 0));
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, defStyleRes));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

		if (source.hasSource()) {
			if (getLayoutParams().width == WRAP_CONTENT) {
				width = source.getSourceWidth(getResources());
			}

			if (getLayoutParams().height == WRAP_CONTENT) {
				height = source.getSourceHeight(getResources());
			}

			if (getLayoutParams().width != WRAP_CONTENT && getLayoutParams().height == WRAP_CONTENT) {
				float rate = (float) source.getSourceHeight(getResources()) / (float) source.getSourceWidth(getResources());
				height = Math.min((int) (rate * (float) width), height);
			} else if (getLayoutParams().width == WRAP_CONTENT && getLayoutParams().height != WRAP_CONTENT) {
				float rate = (float) source.getSourceWidth(getResources()) / (float) source.getSourceHeight(getResources());
				width = Math.min((int) (rate * (float) height), width);
			}
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		source.setCanvasRect(new Rect(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom()));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (null == source) return;
		source.draw(canvas, getResources());
	}

	public void setResource(int resId) {
		setBitmap(BitmapFactory.decodeResource(getResources(), resId));
	}

	public void setBitmap(@NonNull Bitmap bitmap) {
		source.setSource(bitmap);
		requestLayout();
		invalidate();
	}

	public boolean isScaleUp() {
		return source.isScaleUp();
	}

	public void setScaleUp(boolean value) {
		source.setScaleUp(value);
	}

	public CornerSize getCornerSize() {
		try {
			return cornerSize.clone();
		} catch (CloneNotSupportedException ignored) {
		}

		return null;
	}

	public void setCornerSize(CornerSize value) {
		cornerSize.set(value);
		source.setCornerSize(cornerSize.toArray());
	}

	private void init(TypedArray typedArray) {
		float density = getResources().getDisplayMetrics().density;

		float base = typedArray.getFloat(R.styleable.RoundedImageView_corner_size, 0);
		cornerSize = new CornerSize();
		cornerSize.setTopLeft(typedArray.getFloat(R.styleable.RoundedImageView_top_left_corner_size, base) * density);
		cornerSize.setTopRight(typedArray.getFloat(R.styleable.RoundedImageView_top_left_corner_size, base) * density);
		cornerSize.setBottomRight(typedArray.getFloat(R.styleable.RoundedImageView_bottom_right_corner_size, base) * density);
		cornerSize.setBottomLeft(typedArray.getFloat(R.styleable.RoundedImageView_bottom_left_corner_size, base) * density);

		source = new Source(typedArray.getResourceId(R.styleable.RoundedImageView_default_image, -1));
		source.setScaleUp(typedArray.getBoolean(R.styleable.RoundedImageView_scale_up, true));
		source.setCornerSize(cornerSize.toArray());

		try {
			typedArray.recycle();
		} catch (Exception ignored) {
		}
	}

	public static class CornerSize implements Cloneable {
		private float topLeft;
		private float topRight;
		private float bottomLeft;
		private float bottomRight;

		private CornerSize() {
		}

		private void set(CornerSize value) {
			topLeft = value.topLeft;
			topRight = value.topRight;
			bottomLeft = value.bottomLeft;
			bottomRight = value.bottomRight;
		}

		public void setAll(float value) {
			topLeft = topRight = bottomRight = bottomLeft = value;
		}

		public float getTopLeft() {
			return topLeft;
		}

		public void setTopLeft(float value) {
			topLeft = value;
		}

		public float getTopRight() {
			return topRight;
		}

		public void setTopRight(float value) {
			topRight = value;
		}

		public float getBottomLeft() {
			return bottomLeft;
		}

		public void setBottomLeft(float value) {
			bottomLeft = value;
		}

		public float getBottomRight() {
			return bottomRight;
		}

		public void setBottomRight(float value) {
			bottomRight = value;
		}

		private float[] toArray() {
			return new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft};
		}

		@Override
		protected CornerSize clone() throws CloneNotSupportedException {
			return (CornerSize) super.clone();
		}
	}

	private class Source {
		private final Paint PAINT;
		private final int DEFAULT_RES;

		private Bitmap bitmap;

		private boolean invalidateResize;
		private boolean invalidateCanvasRect;
		private boolean invalidateClipPath;

		private boolean isScaleUp;
		private float[] cornerSize;

		private Rect canvasRect;
		private RectF drawRect;
		private Rect bitmapRect;
		private Path clipPath;

		Source(int defaultRes) {
			PAINT = new Paint();
			PAINT.setAntiAlias(true);

			DEFAULT_RES = defaultRes;

			cornerSize = new float[8];
		}

		boolean hasSource() {
			return null != bitmap || DEFAULT_RES != -1;
		}

		int getSourceWidth(@NonNull Resources resources) {
			return getSource(resources).getWidth();
		}

		int getSourceHeight(@NonNull Resources resources) {
			return getSource(resources).getHeight();
		}

		void setCanvasRect(Rect rect) {
			if (null == canvasRect || canvasRect.equals(rect) == false) {
				canvasRect = rect;

				invalidateResize = true;
				invalidateCanvasRect = true;
				invalidateClipPath = true;
			}
		}

		boolean isScaleUp() {
			return isScaleUp;
		}

		void setScaleUp(boolean value) {
			if (value != isScaleUp) {
				isScaleUp = value;

				invalidateCanvasRect = true;
				invalidateClipPath = true;
			}
		}

		void setCornerSize(float[] value) {
			if (Arrays.equals(cornerSize, value) == false) {
				cornerSize = Arrays.copyOf(value, value.length);

				invalidateClipPath = true;
			}
		}

		void setSource(@NonNull Bitmap bitmap) {
			clear();
			this.bitmap = bitmap;
		}

		void draw(@NonNull Canvas canvas, @NonNull Resources resources) {
			if (hasSource() == false) return;

			invalidate(resources);

			canvas.save();
			canvas.clipPath(clipPath);
			canvas.drawBitmap(bitmap, bitmapRect, drawRect, PAINT);
			canvas.restore();
		}

		private void invalidate(Resources resources) {
			if (bitmap == null) {
				getSource(resources);
			}

			if (invalidateResize) {
				invalidateResize = false;
				resize();
			}

			if (null == bitmapRect) {
				bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			}

			if (invalidateCanvasRect) {
				invalidateCanvasRect = false;
				forceScale();
			}

			if (invalidateClipPath) {
				invalidateClipPath = false;
				refreshClipPath();
			}
		}

		private void resize() {
			if (null == canvasRect) return;
			if (null == bitmap) return;

			if (canvasRect.width() < bitmap.getWidth() || canvasRect.height() < bitmap.getHeight()) {
				float rate = Math.min((float) canvasRect.width() / (float) bitmap.getWidth(), (float) canvasRect.height() / (float) bitmap.getHeight());
				if (rate < 1 && rate > 0)
					bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * rate), (int) (bitmap.getHeight() * rate), true);
			}
		}

		private void forceScale() {
			if (null == drawRect) drawRect = new RectF();

			if (isScaleUp) {
				final float rate = Math.min((float) canvasRect.height() / (float) bitmapRect.height(), (float) canvasRect.width() / (float) bitmapRect.width());
				int targetWidth = (int) ((canvasRect.width() - ((float) bitmapRect.width() * rate)) / 2f);
				int targetHeight = (int) ((canvasRect.height() - ((float) bitmapRect.height() * rate)) / 2f);

				drawRect.left = canvasRect.left + targetWidth;
				drawRect.top = canvasRect.top + targetHeight;
				drawRect.right = canvasRect.right - targetWidth;
				drawRect.bottom = canvasRect.bottom - targetHeight;
			} else {
				drawRect.left = canvasRect.left + (int) ((canvasRect.width() - bitmap.getWidth()) / 2f);
				drawRect.top = canvasRect.top + (int) ((canvasRect.height() - bitmap.getHeight()) / 2f);
				drawRect.right = drawRect.left + bitmap.getWidth();
				drawRect.bottom = drawRect.top + bitmap.getHeight();
			}
		}

		private void refreshClipPath() {
			if (null == clipPath) clipPath = new Path();
			else clipPath.reset();

			clipPath.addRoundRect(drawRect, cornerSize, Path.Direction.CW);
		}

		private Bitmap getSource(Resources resources) {
			if (null == bitmap && hasSource()) {
				bitmap = BitmapFactory.decodeResource(resources, DEFAULT_RES);
			}

			return bitmap;
		}

		private void clear() {
			bitmapRect = null;

			invalidateResize = true;
			invalidateCanvasRect = true;
			invalidateClipPath = true;

			// TODO: 2018. 2. 21. pool으로 관리할 경우.. recycle하면 안됨
//			try {
//				bitmap.recycle();
//			} catch (Exception ignored) {
//			} finally {
//				bitmap = null;
//			}
			bitmap = null;
		}

	}

}
