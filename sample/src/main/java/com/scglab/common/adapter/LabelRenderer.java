package com.scglab.common.adapter;

import android.view.View;
import android.widget.TextView;

import com.scglab.common.listadapter.ItemRenderer;

/**
 * Created by shj on 2017. 9. 11..
 */
public class LabelRenderer extends ItemRenderer<LabelItem> {
	private TextView txtLabel;

	public LabelRenderer(View view) {
		super(view);
	}

	@Override
	protected void onBind(final LabelItem item) {
		txtLabel.setText(item.getMessage());
	}

}