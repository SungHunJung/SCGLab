package com.scglab.common.listadapter.filter;

import android.widget.Filter;

import com.scglab.common.listadapter.FlexAdapter;
import com.scglab.common.listadapter.ItemStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shj on 2017. 9. 20..
 */
public class BaseFilter extends Filter {

	protected FlexAdapter adapter;
	protected ItemStore itemStore;

	public BaseFilter(FlexAdapter adapter, ItemStore itemStore) {
		this.adapter = adapter;
		this.itemStore = itemStore;
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		List<Object> removeList = new ArrayList<>();

		if (null != constraint && constraint.length() > 0) {
			Object object;
			boolean hide;

			for (int index = itemStore.realSize() - 1; index >= 0; index--) {
				object = itemStore.getItemByRealIndex(index);
				if (object instanceof Queryable) {
					hide = ((Queryable) object).onQuery(constraint) == false;
					if (hide) removeList.add(object);
				}
			}
		}

		FilterResults filterResults = new FilterResults();
		filterResults.values = removeList;
		return filterResults;
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		final List<Object> newList = (List<Object>) results.values;
		final List<Object> oldList = itemStore.getHidedItemList();

		//remove
		for (Object object : newList) {
			if (oldList.contains(object) == false) {
				adapter.notifyItemRemoved(object);
				itemStore.addHideItem(object);
			}
		}

		//add
		for (Object object : oldList) {
			if (newList.contains(object) == false) {
				itemStore.removeHideItem(object);
				adapter.notifyItemInserted(object);
			}
		}
	}

}