package com.scglab.common.listadapter;

public interface IListItemEventHandler<T extends ListItem> {
	/**
	 * @param model
	 * @return if (@return) notifyItemChanged(model)
	 */
	boolean onClick(T model, int viewId);

	/**
	 * @param model
	 * @return if (@return) notifyItemChanged(model)
	 */
	boolean onLongClick(T model, int viewId);
}
