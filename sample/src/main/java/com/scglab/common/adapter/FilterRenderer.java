package com.scglab.common.adapter;

import android.view.View;
import android.widget.TextView;

import com.scglab.common.listadapter.ItemRenderer;

/**
 * Created by shj on 2017. 9. 11..
 */
public class FilterRenderer extends ItemRenderer<FilterItem> {
	private TextView txtLabel;

	public FilterRenderer(View view) {
		super(view);
	}

	@Override
	protected void onBind(final FilterItem item) {
		txtLabel.setText(item.getMessage());
	}
}