package com.scglab.common.adapter;

import com.scglab.common.listadapter.FlexAdapter;
import com.scglab.common.listadapter.filter.Queryable;

/**
 * Created by shj on 2017. 9. 11..
 */
@FlexAdapter.Item
public class FilterItem implements Queryable {

	private String message;

	public FilterItem(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public boolean onQuery(CharSequence constraint) {
		return message.contains(constraint);
	}

	@Override
	public String toString() {
		return message;
	}
}
