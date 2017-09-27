package com.scglab.common.adapter;

import com.scglab.common.listadapter.FlexAdapter;

/**
 * Created by shj on 2017. 9. 11..
 */
@FlexAdapter.Item
public class UrlItem {

	private String url;

	public UrlItem(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
