package com.scglab.common.listadapter;

/**
 * Created by sh on 2016. 4. 4..
 */
public abstract class ListItem {
	/*
	public enum Type {
		ITEM(0), HEADER(1), SEARCH(2), ETC(3), MEMBER(4), PLACE(5), FOOT(6), MAP(7);

		private final int VALUE;

		Type(int value) {
			VALUE = value;
		}

		public int getValue() {
			return VALUE;
		}
	}
	*/

	public final static class Type {
		public static final int ITEM = 0;
		public static final int HEADER = 1;
		public static final int SEARCH = 2;
		public static final int ETC = 3;
	}

	private boolean isVisible = true;

	public final boolean isVisible() {
		return isVisible;
	}

	public final void setVisible(boolean value) {
		isVisible = value;
	}

	public final void setVisible(String query) {
		isVisible = onFiltering(query);
	}

	private boolean isSelected = false;

	public final boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public boolean toggleSelected() {
		isSelected = !isSelected;
		return isSelected;
	}

	public abstract int getType();

	public abstract boolean onFiltering(String query);
}