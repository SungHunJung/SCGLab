package com.scglab.common.listadapter.filter;

/**
 * Created by shj on 2017. 9. 20..
 */
public interface Queryable {
	/**
	 * @param constraint the constraint used to filter the data
	 *
	 * @return False if the Item should be hided
	 */
	boolean onQuery(CharSequence constraint);
}
