package com.scglab.common.adapter;

import com.scglab.common.listadapter.FlexAdapter;

/**
 * Created by shj on 2017. 9. 11..
 */
@FlexAdapter.Item
public class LabelItem {

	private String message;

	public LabelItem(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
