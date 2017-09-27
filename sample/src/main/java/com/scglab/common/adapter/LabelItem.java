package com.scglab.common.adapter;

import com.scglab.common.listadapter.ListAdapter;

/**
 * Created by shj on 2017. 9. 11..
 */
@ListAdapter.Item
public class LabelItem {

	private String message;

	public LabelItem(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
