package com.scglab.common.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

/**
 * Created by sh on 2016. 5. 23..
 */
public class BitmapHelper {

	public static void decodeSampledBitmapFromPath(final String path, final int reqWidth, final int reqHeight, final CallBack callBack) {
		final Handler handler = new Handler();

		new Thread() {
			@Override
			public void run() {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, options);

				options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
				options.inJustDecodeBounds = false;
				final Bitmap bitmap = normalize(path, BitmapFactory.decodeFile(path, options));

				handler.post(new Runnable() {
					@Override
					public void run() {
						callBack.onResult(bitmap);
					}
				});
			}
		}.run();
	}

	public interface CallBack {
		void onResult(Bitmap bitmap);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap normalize(String path, Bitmap bitmap) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException ignored) {
		}

		if (null == exif) return bitmap;

		int degree = 0;
		switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
		}

		if (degree != 0) {
			Matrix matrix = new Matrix();
			matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}

		return bitmap;
	}

	public static Point getSize(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		return new Point(options.outWidth, options.outHeight);
	}

	public static Point getSize(Resources res, int id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, id, options);
		return new Point(options.outWidth, options.outHeight);
	}

	public static Bitmap circle(Bitmap bitmap, Resources resources, int circleSizeDp) {
		circleSizeDp = (int) (resources.getDisplayMetrics().density * circleSizeDp);
		return circle(bitmap, circleSizeDp);
	}

	public static Bitmap circle(Bitmap bitmap, int circleSize) {
		return circle(bitmap, circleSize, 0, -1);
	}

	public static Bitmap circle(Bitmap bitmap, Resources resources, int circleSizeDp, int frameStrokeDp, int frameColor) {
		circleSizeDp = (int) (resources.getDisplayMetrics().density * circleSizeDp);
		frameStrokeDp = (int) (resources.getDisplayMetrics().density * frameStrokeDp);
		return circle(bitmap, circleSizeDp, frameStrokeDp, frameColor);
	}

	public static Bitmap circle(Bitmap bitmap, int circleSize, int frameStroke, int frameColor) {

		Bitmap image = Bitmap.createBitmap(circleSize, circleSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		canvas.save();

		float temp;
		final float x = (int) ((canvas.getWidth() - circleSize) / 2.0f);

		//이미지 영역 잡기
		Paint paint = new Paint();
		paint.setColor(frameColor);
		paint.setStyle(Paint.Style.FILL);
		temp = ((float) (circleSize) / 2.0f - frameStroke);
		canvas.drawCircle(x + temp + frameStroke, temp + frameStroke, temp, paint);

		//이미지
		if (null != bitmap) {
			bitmap = Bitmap.createScaledBitmap(bitmap, circleSize, circleSize, true);
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


	/*protected Bitmap roundBitmap(Bitmap bitmap) {
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
	}*/

	public static Bitmap rect(Bitmap bitmap, float cornerSize, float maxSize) {
		Rect rect = new Rect(0, 0, (int) maxSize, (int) maxSize);
		RectF rects = new RectF(rect);

		Bitmap image = Bitmap.createBitmap((int) maxSize, (int) maxSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		canvas.save();

		final Rect topRightRect = new Rect((int) maxSize / 2, 0, (int) maxSize, (int) maxSize / 2);

		//이미지 영역 잡기
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);

		canvas.drawRoundRect(rects, cornerSize, cornerSize, paint);

		// Fill in upper right corner
		canvas.drawRect(topRightRect, paint);


		//이미지
		if (null != bitmap) {
			bitmap = Bitmap.createScaledBitmap(bitmap, (int) maxSize, (int) maxSize, true);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, 0, 0, paint);
		}

		//프레임
		/*paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		if (circleSize > 0) {
			paint.setColor(frameColor);
			paint.setStyle(Paint.Style.FILL);
			temp = ((float) (circleSize) / (2.0f));
			canvas.drawCircle(x + temp, temp, temp, paint);
		}*/

		canvas.restore();

		return image;
	}

	public static Bitmap blur(Bitmap sentBitmap, int radius) {
		// Stack Blur v1.0 from
		// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
		//
		// Java Author: Mario Klingemann <mario at quasimondo.com>
		// http://incubator.quasimondo.com
		// created Feburary 29, 2004
		// Android port : Yahel Bouaziz <yahel at kayenko.com>
		// http://www.kayenko.com
		// ported april 5th, 2012

		// This is a compromise between Gaussian Blur and Box blur
		// It creates much better looking blurs than Box Blur, but is
		// 7x faster than my Gaussian Blur implementation.
		//
		// I called it Stack Blur because this describes best how this
		// filter works internally: it creates a kind of moving stack
		// of colors whilst scanning through the image. Thereby it
		// just has to add one new block of color to the right side
		// of the stack and remove the leftmost color. The remaining
		// colors on the topmost layer of the stack are either added on
		// or reduced by one, depending on if they are on the right or
		// on the left side of the stack.
		//
		// If you are using this algorithm in your code please add
		// the following line:
		//
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}
}
