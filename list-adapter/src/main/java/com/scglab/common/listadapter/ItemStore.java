package com.scglab.common.listadapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by shj on 2017. 9. 20..
 */
public class ItemStore extends ArrayList<Object> {

	//----------------------------------------
	// List
	//----------------------------------------


	@Override
	public void clear() {
		super.clear();
		HIDE_LIST.clear();
		SELECT_LIST.clear();
	}

	@Override
	public int size() {
		return super.size() - HIDE_LIST.size();
	}

	@Override
	public Object get(int position) {
		position = visiblePositionToDataPosition(position);
		return super.get(position);
	}

	@Override
	public boolean add(Object object) {
		if (hasAnnotation(object)) return super.add(object);
		return false;
	}

	@Override
	public void add(int position, Object object) {
		if (hasAnnotation(object)) {
			position = visiblePositionToDataPosition(position);
			super.add(position, object);
		}
	}

	@Override
	public boolean addAll(Collection<? extends Object> collection) {
		objectFilter(collection);
		return super.addAll(collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> collection) {
		objectFilter(collection);
		return super.addAll(index, collection);
	}

	@Override
	public Object remove(int position) {
		int temp = visiblePositionToDataPosition(position);
		return super.remove(temp);
	}

	@Override
	public int indexOf(Object object) {
		throw new RuntimeException("Try indexOfData or indexOfVisible");
	}

	public int indexOfData(Object object) {
		return super.indexOf(object);
	}

	public int indexOfVisible(Object object) {
		int position = super.indexOf(object);
		return dataPositionToVisiblePosition(position);
	}

	private void objectFilter(Collection<? extends Object> collection) {
		List<Object> removeList = new ArrayList<>();
		for (Object object : collection) {
			if (hasAnnotation(object) == false) removeList.add(object);
		}
		for (Object object : removeList) {
			collection.remove(object);
		}

	}

	private boolean hasAnnotation(Object object) {
		return object.getClass().isAnnotationPresent(ListAdapter.Item.class);
	}

	private int dataPositionToVisiblePosition(int position) {
		if (HIDE_LIST.isEmpty()) return position;
		if (position < 0) return position;

		int result = 0;
		for (int index = 0; index < position; index++) {
			if (HIDE_LIST.contains(super.get(index)) == false) {
				result++;
			}
		}

		return result;
	}

	private int visiblePositionToDataPosition(int position) {
		if (HIDE_LIST.isEmpty()) return position;
		if (position < 0) return position;

		int result = -1;
		int index = 0;
		while (result != position) {
			if (HIDE_LIST.contains(super.get(index)) == false) {
				result++;
			}
			index++;
		}

		if (index > 0) --index;
		return index;
	}

	//----------------------------------------
	// real
	//----------------------------------------

	public int realSize() {
		return super.size();
	}

	public Object getItemByRealIndex(int index) {
		return super.get(index);
	}

	public int realIndexOf(Object object) {
		return super.indexOf(object);
	}

	//----------------------------------------
	// filter
	//----------------------------------------

	private final List<Object> HIDE_LIST = new ArrayList<>();

	public void addHideItem(Object object) {
		if (hasAnnotation(object)) {
			removeHideItem(object);
			HIDE_LIST.add(object);
		}
	}

	public void removeHideItem(Object object) {
		HIDE_LIST.remove(object);
	}

	public void clearHidedItem() {
		HIDE_LIST.clear();
	}

	public boolean isHided(Object object) {
		return HIDE_LIST.contains(object);
	}

	public List<Object> getHidedItemList() {
		List<Object> result = new ArrayList<>();

		for (Object object : HIDE_LIST) {
			result.add(object);
		}

		return result;
	}

	//----------------------------------------
	// select
	//----------------------------------------

	private final List<Object> SELECT_LIST = new ArrayList<>();

	void addSelectItem(Object object) {
		if (hasAnnotation(object)) {
			SELECT_LIST.remove(object);
			SELECT_LIST.add(object);
		}
	}

	void removeSelectItem(Object object) {
		SELECT_LIST.remove(object);
	}

	boolean toggleSelectItem(Object object) {
		if (hasAnnotation(object)) {
			if (SELECT_LIST.contains(object)) {
				SELECT_LIST.remove(object);
			} else {
				SELECT_LIST.add(object);
			}
		}

		return isSelected(object);
	}

	public boolean isSelected(Object object) {
		return SELECT_LIST.contains(object);
	}

	public List<Object> getSelectedItemList() {
		List<Object> result = new ArrayList<>();

		for (Object object : SELECT_LIST) {
			result.add(object);
		}

		return result;
	}

	int clearSelectedList() {
		int removeSize = SELECT_LIST.size();
		SELECT_LIST.clear();

		return removeSize;
	}
}
