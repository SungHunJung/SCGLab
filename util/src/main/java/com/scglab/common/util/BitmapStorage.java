package com.scglab.common.util;

public class BitmapStorage {
	private static final BitmapStorage instance = new BitmapStorage();

	public static BitmapStorage getInstance() {
		return instance;
	}

	private volatile BitmapLruCache imageCache;

	private BitmapStorage() {
	}

	public BitmapLruCache getBitmapLruCache() {
		if (null == imageCache) {
			synchronized (this) {
				if (null == imageCache) {
					imageCache = new BitmapLruCache();
				}
			}
		}
		return imageCache;
	}
}
