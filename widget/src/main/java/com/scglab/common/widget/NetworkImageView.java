package com.scglab.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.security.ProviderException;

public class NetworkImageView extends View {

	//--------------------------------------------------------------------------
	//
	//	Instance variables
	//
	//--------------------------------------------------------------------------

	protected Paint paint;

	private String imageUrl;
	private String currentLoadedUrl;
	private boolean needResize;

	protected Bitmap drawImage;
	protected Bitmap emptyImage;

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
		init(context.getTheme().obtainStyledAttributes(attrs, getStyleable(), 0, 0));
	}

	//--------------------------------------------------------------------------
	//	Override:View
	//--------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		Point point = getResultSize();
		setMeasuredDimension(point.x, point.y);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (needResize) {
			needResize = !resizeImage();
//			requestLayout();
//			return;
		}

		if (null != drawImage) canvas.drawBitmap(drawImage, 0, 0, null);
		else if (null != emptyImage) canvas.drawBitmap(emptyImage, 0, 0, null);
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

		currentLoadedUrl = imageUrl;
		drawImage = bitmap;
		needResize = true;
		requestLayout();
		invalidate();
	}

	public void clear() {
		currentLoadedUrl = null;
		imageUrl = null;
		drawImage = null;
		requestLayout();
		invalidate();
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
		int emptyImageResource = typedArray.getResourceId(R.styleable.NetworkImageView_emptyImage, -1);
		if (emptyImageResource != -1)
			emptyImage = BitmapFactory.decodeResource(getResources(), emptyImageResource);
		else
			throw new ProviderException("Not found empty image : Must have image of empty situation.");
	}

	protected Point getResultSize() {
		if (null != drawImage)
			return new Point(drawImage.getWidth(), drawImage.getHeight());

		int tempWidth = getWidth();
		int tempHeight = getHeight();

		if (getWidth() == 0 || getHeight() == 0) {
			tempWidth = emptyImage.getWidth();
			tempHeight = emptyImage.getHeight();
		}

		return new Point(tempWidth, tempHeight);
	}

	protected void retouchDrawImage(Point point) {
		if (point.x != drawImage.getWidth() || point.y != drawImage.getHeight()) {
			drawImage = Bitmap.createScaledBitmap(drawImage, point.x, point.y, true);
		}
	}

	private void init(TypedArray typedArray) {
		paint = new Paint();
		paint.setAntiAlias(true);

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
}
