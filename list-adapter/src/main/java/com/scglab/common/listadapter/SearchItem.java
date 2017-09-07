package com.scglab.common.listadapter;

/**
 * Created by sh on 2016. 5. 12..
 */
public class SearchItem extends ListItem {

	private String hint;

	public SearchItem(String hint) {
		this.hint = hint;
	}


	public String getHint() {
		return hint;
	}


	@Override
	public int getType() {
		return Type.SEARCH;
	}

	@Override
	public boolean onFiltering(String query) {
		return true;
	}
}
