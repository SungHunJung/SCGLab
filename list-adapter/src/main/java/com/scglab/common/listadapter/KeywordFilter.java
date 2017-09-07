package com.scglab.common.listadapter;

import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by sh on 2016. 1. 22..
 */
public class KeywordFilter<T extends ListItem> extends Filter {

	private final int OLD_SIZE;

	private ListAdapter listAdapter;
	private ArrayList<T> items;

	public KeywordFilter(ListAdapter listAdapter, ArrayList<T> items) {
		OLD_SIZE = listAdapter.getItemCount();
		this.listAdapter = listAdapter;
		this.items = items;
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults results = new FilterResults();

		String matchString = constraint.toString().toLowerCase();
		for (T item : items) {
			item.setVisible(matchString);
			if (item.isVisible()) results.count++;
		}

		return results;
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		listAdapter.notifyItemRangeRemoved(1, OLD_SIZE - 1);
		listAdapter.notifyItemRangeInserted(1, results.count);
	}
}
