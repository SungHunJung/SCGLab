package com.scglab.common.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.scglab.common.listadapter.ItemRenderer;

/**
 * Created by shj on 2017. 9. 11..
 */
public class ImageRenderer extends ItemRenderer<UrlItem> {
	private ImageView imgMain;

	public ImageRenderer(View view) {
		super(view);
	}

	@Override
	protected void onBind(UrlItem item) {
		if (isSelectMode()) {
			imgMain.setAlpha(0.5f);
		} else {
			imgMain.setAlpha(1f);
		}

		Glide.clear(bitmapSimpleTarget);
		Glide.with(getContext()).load(item.getUrl()).asBitmap().into(bitmapSimpleTarget);
	}

	private final SimpleTarget<Bitmap> bitmapSimpleTarget = new SimpleTarget<Bitmap>() {
		@Override
		public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
			imgMain.setImageBitmap(resource);
		}
	};
}
