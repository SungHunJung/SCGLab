package com.scglab.common.adapter;

import com.scglab.common.listadapter.ListAdapter;

/**
 * Created by shj on 2017. 9. 11..
 */
@ListAdapter.Item
public class UrlItem {

	private String url;

	public UrlItem(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
