package com.scglab.common.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scglab.common.listadapter.ItemRenderer;

/**
 * Created by shj on 2017. 9. 11..
 */
public class ItemClickRenderer extends ItemRenderer<LabelItem> {
	private TextView txtLabel;
	private Button btnRemove;

	public ItemClickRenderer(View view) {
		super(view);
	}

	@Override
	protected void onBind(final LabelItem item) {
		txtLabel.setText(item.getMessage());
	}

	@Override
	protected void onAttachedRenderer() {
		addChildViewClickListener(btnRemove);
	}

	@Override
	protected void onDetachedRenderer() {
		removeChildViewClickListener(btnRemove);
	}
}