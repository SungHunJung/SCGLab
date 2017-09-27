package com.scglab.common.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.scglab.common.R;
import com.scglab.common.listadapter.ItemRenderer;

/**
 * Created by shj on 2017. 9. 11..
 */
public class SelectModeRenderer extends ItemRenderer<LabelItem> {

	private TextView txtLabel;

	@ItemRenderer.FindById(resId = R.id.cbSelect)
	private CheckBox checkBox;

	public SelectModeRenderer(View view) {
		super(view);
	}

	@Override
	protected void onBind(final LabelItem item) {
		txtLabel.setText(item.getMessage());

		checkBox.setChecked(isSelected());
		if (isSelectMode()) checkBox.setVisibility(View.VISIBLE);
		else checkBox.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onAttachedRenderer() {
		addChildViewClickListener(checkBox);
	}

	@Override
	protected void onDetachedRenderer() {
		removeChildViewClickListener(checkBox);
	}
}