package com.scglab.common.listadapter;

/**
 * Created by shj on 2017. 9. 13..
 */
public interface OnItemClickEventHandler {
	/**
	 * @param item target model
	 */
	void onItemClick(Object item);

	/**
	 * @param item target model
	 */
	void onItemLongClick(Object item);

	/**
	 * @param item target model
	 * @param viewId target View Id
	 */
	void onChildViewClick(Object item, int viewId);

	/**
	 * @param item target model
	 * @param viewId target View
	 */
	void onChildViewLongClick(Object item, int viewId);
}
