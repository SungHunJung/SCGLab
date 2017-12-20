package com.scglab.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class NetworkImageView extends View {

	//--------------------------------------------------------------------------
	//
	//	Instance variables
	//
	//--------------------------------------------------------------------------

	protected Paint paint;

	private int widthMeasureSpec;
	private int heightMeasureSpec;

	private String imageUrl;
	private String currentLoadedUrl;
	private boolean needResize;
	private boolean showRealImageSize;

	protected Bitmap drawImage;
	private EmptyImage emptyImage;

	//--------------------------------------------------------------------------
	//
	//	Instance methods
	//
	//--------------------------------------------------------------------------

	//--------------------------------------------------------------------------
	//	Constructor
	//--------------------------------------------------------------------------

	public NetworkImageView(Context context) {
		super(context);
		init(null);
	}

	public NetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context.getTheme().obtainStyledAttributes(attrs, getStyleable(), 0, 0));
	}

	public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context.getTheme().obtainStyledAttributes(attrs, getStyleable(), defStyleAttr, 0));
	}

	public NetworkImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context.getTheme().obtainStyledAttributes(attrs, getStyleable(), defStyleAttr, defStyleRes));
	}

	//--------------------------------------------------------------------------
	//	Override:View
	//--------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.widthMeasureSpec = widthMeasureSpec;
		this.heightMeasureSpec = heightMeasureSpec;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		Point point = getResultSize();
		Log.v("ROOEX", getMeasuredWidth() + " / " + getMeasuredHeight() + " / " + getWidth() + " / " + getHeight());
		Log.v("ROOEX", point.toString());
		setMeasuredDimension(point.x, point.y);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (needResize) {
			needResize = !resizeImage();
		}

		if (null != drawImage) {
			Log.i("ROOEX", drawImage.getWidth() + " / " + drawImage.getHeight());
			Log.i("ROOEX", canvas.getWidth() + " / " + canvas.getHeight());
			canvas.drawBitmap(drawImage, 0, 0, null);
		} else if (null != getEmptyImage()) {
			canvas.drawBitmap(getEmptyImage().getBitmap(), (canvas.getWidth() - getEmptyImage().getBitmap().getWidth()) / 2, (canvas.getHeight() - getEmptyImage().getBitmap().getHeight()) / 2, null);
		}
	}

	//--------------------------------------------------------------------------
	//	Public
	//--------------------------------------------------------------------------

	public void setImage(int resId) {
		imageUrl = null;
		setImage(BitmapFactory.decodeResource(getResources(), resId));
	}

	public void setImage(Bitmap bitmap) {
		if (null == bitmap) {
			return;
		}

		Log.w("ROOEX", "bitmap : " + bitmap.getWidth() + " / " + bitmap.getHeight());
		currentLoadedUrl = imageUrl;
		drawImage = bitmap;
		needResize = true;
		requestLayout();
	}

	public void clear() {
		currentLoadedUrl = null;
		imageUrl = null;
		drawImage = null;
		requestLayout();
	}

	public void setImageUrl(final String requestUrl) {
		if (null == requestUrl || requestUrl.isEmpty()) {
			clear();
			return;
		}
		if (null != imageUrl && imageUrl.equals(requestUrl)) {
			return;
		}
		if (null != currentLoadedUrl && currentLoadedUrl.equals(requestUrl)) {
			return;
		}

		clear();

		//지난 요청 취소
		Glide.clear(simpleTarget);

		//요청
		imageUrl = requestUrl;

		Glide.with(getContext()).load(imageUrl).asBitmap().into(simpleTarget);
	}

	//--------------------------------------------------------------------------
	//	Internal
	//--------------------------------------------------------------------------

	protected int[] getStyleable() {
		return R.styleable.NetworkImageView;
	}

	protected void initStyle(TypedArray typedArray) {
	}

	protected EmptyImage getEmptyImage() {
		return emptyImage;
	}

	protected Point getResultSize() {
		//width, height가 둘다 wrap_content라면 showRealImageSize는 true
		if (false == showRealImageSize)
			showRealImageSize = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

		//showRealImageSize는 이미지가 있을때 의미 있음
		if (showRealImageSize && null != drawImage) {
			return new Point(drawImage.getWidth(), drawImage.getHeight());
		}

		//이미지는 없음 + 임시 이미지가 있음
		if (null == drawImage && getEmptyImage().hasImage()) {
			return new Point(getEmptyImage().getBitmap().getWidth(), getEmptyImage().getBitmap().getHeight());
		}

		//이미지 있음 + wrap_content대응
		if (null != drawImage) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			final float rate = (float) drawImage.getWidth() / (float) drawImage.getHeight();
			if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) width = (int) (height * rate);
			if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) height = (int) (width / rate);
			return new Point(width, height);
		}

		return new Point(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	protected void retouchDrawImage(Point point) {
		if (point.x != drawImage.getWidth() || point.y != drawImage.getHeight()) {
			drawImage = Bitmap.createScaledBitmap(drawImage, point.x, point.y, true);
		}
	}

	private void init(TypedArray typedArray) {
		paint = new Paint();
		paint.setAntiAlias(true);

		showRealImageSize = typedArray.getBoolean(R.styleable.NetworkImageView_showRealImageSize, false);
		emptyImage = new EmptyImage(typedArray.getResourceId(R.styleable.NetworkImageView_emptyImage, -1));
		initStyle(typedArray);

		try {
			typedArray.recycle();
		} catch (Exception ignored) {
		}
	}

	private boolean resizeImage() {
		if (null == drawImage) {
			return false;
		}

		Point point = getResultSize();
		retouchDrawImage(point);
		return true;
	}

	//--------------------------------------------------------------------------
	//	Handler
	//--------------------------------------------------------------------------

	private final SimpleTarget simpleTarget = new SimpleTarget<Bitmap>() {
		@Override
		public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
			if (null != resource) setImage(resource);
		}
	};

	//--------------------------------------------------------------------------
	//	Inner Class
	//--------------------------------------------------------------------------

	public class EmptyImage {
		private Bitmap bitmap;
		private int res;

		public EmptyImage(int res) {
			this.res = res;
		}

		public Bitmap getBitmap() {
			if (hasImage() == false)
				return null;

			if (null == bitmap)
				bitmap = BitmapFactory.decodeResource(getResources(), res);

			return bitmap;
		}

		public boolean hasImage() {
			return res != -1;
		}

		public void retouch(Retouch retouch) {
			if (null != retouch) {
				bitmap = retouch.run(getBitmap());
			} else {
				Log.e("ROOEX", "retouch is null");
			}
		}
	}

	public interface Retouch {
		Bitmap run(Bitmap bitmap);
	}
}
