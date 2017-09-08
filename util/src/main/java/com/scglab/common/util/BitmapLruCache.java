package com.scglab.common.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, Bitmap> {

	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		return maxMemory / 8;
	}

	private BitmapLruCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	public BitmapLruCache() {
		this(getDefaultLruCacheSize());
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getByteCount() / 1024;
	}
}