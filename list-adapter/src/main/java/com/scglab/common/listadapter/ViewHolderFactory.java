package com.scglab.common.listadapter;

import java.util.Hashtable;

/**
 * Created by sh on 2016. 1. 14..
 */
public class ViewHolderFactory {
	private final Hashtable<Integer, ViewHolderWrapper> table;

	public ViewHolderFactory() {
		table = new Hashtable<>();
	}

	public void set(int type, Class viewHolder, int layout) {
		set(type, new ViewHolderWrapper(viewHolder, layout));
	}

	public void set(int type, ViewHolderWrapper viewHolderWrapper) {
		table.remove(type);
		table.put(type, viewHolderWrapper);
	}

	public ViewHolderWrapper get(int type) {
		return table.get(type);
	}

	public static class ViewHolderWrapper {
		private final Class VIEW_HOLDER;
		private final int LAYOUT;

		public ViewHolderWrapper(Class viewHolder, int layout) {
			VIEW_HOLDER = viewHolder;
			LAYOUT = layout;
		}

		public Class getViewHolder() {
			return VIEW_HOLDER;
		}

		public int getLayout() {
			return LAYOUT;
		}
	}

}
