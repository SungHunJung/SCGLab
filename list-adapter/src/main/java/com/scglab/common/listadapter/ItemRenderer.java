package com.scglab.common.listadapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Filter;

import com.scglab.common.listadapter.filter.BaseFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Created by shj on 2017. 9. 11..
 */
public abstract class ItemRenderer<T> extends RecyclerView.ViewHolder {

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface FindById {
		int resId();
	}

	FlexAdapter flexAdapter;

	public ItemRenderer(View view) {
		super(view);
	}

	//----------------------------------------
	// life cycle
	//----------------------------------------

	protected abstract void onBind(final T item);

	/**
	 * Called when RecyclerView.Adapter.onViewAttachedToWindow(VH holder)
	 */
	protected void onAttachedRenderer() {
	}

	/**
	 * Called when RecyclerView.Adapter.onViewDetachedFromWindow(VH holder)
	 */
	protected void onDetachedRenderer() {
	}

	void onIdle() {
	}

	//----------------------------------------
	// model
	//----------------------------------------

	private Object currentItem;

	void setCurrentItem(T item) {
		this.currentItem = item;
		onBind(item);
	}

	private Object nextItem;

	void setNextItem(Object item) {
		nextItem = item;
	}

	protected final Object getNextItem() {
		return nextItem;
	}

	private Object prevItem;

	void setPrevItem(Object item) {
		prevItem = item;
	}

	protected final Object getPrevItem() {
		return prevItem;
	}

	//----------------------------------------
	// select
	//----------------------------------------

	protected final boolean isSelectMode() {
		return flexAdapter.isSelectMode();
	}

	protected final boolean isSelected() {
		return flexAdapter.isSelected(currentItem);
	}

	protected final void setSelect(boolean value) {
		if (value) {
			flexAdapter.addSelectItem(currentItem);
		} else {
			flexAdapter.removeItem(currentItem);
		}
	}

	protected final boolean toggleSelect() {
		return flexAdapter.toggleSelectItem(currentItem);
	}

	//----------------------------------------
	// internal
	//----------------------------------------

	/**
	 * Returns the context the itemRenderer is running in, through which it can
	 * access the current theme, resources, etc.
	 *
	 * @return The itemRenderer's Context.
	 */
	protected final Context getContext() {
		return itemView.getContext();
	}

	/**
	 * The logical density of the display.
	 *
	 * @see android.util.DisplayMetrics#density
	 */
	protected final float getDensity() {
		return getContext().getResources().getDisplayMetrics().density;
	}

	final <V extends View> V findViewById(Field field) {
		int resId;

		FindById annotation = field.getAnnotation(FindById.class);
		if (null != annotation) {
			FindById findById = annotation;
			resId = findById.resId();
		} else {
			resId = itemView.getResources().getIdentifier(field.getName(), "id", itemView.getContext().getApplicationContext().getPackageName());
		}

		if (resId > 0) return findViewById(resId);
		else return null;
	}

	/**
	 * @see android.view.View#findViewById
	 */
	protected final <V extends View> V findViewById(int resId) {
		return (V) itemView.findViewById(resId);
	}

	/**
	 * Request to reload the current ItemRenderer.
	 * Just like a ListAdapter.notifyItemChanged(currentPosition)
	 */
	protected final void invalidate() {
		itemView.post(new Runnable() {
			@Override
			public void run() {
				flexAdapter.notifyItemChanged(currentItem);
			}
		});
	}

	protected final Filter getFilter() {
		return flexAdapter.getFilter();
	}

	protected final <F extends BaseFilter> void setFilter(Class<F> filterClass) {
		flexAdapter.setFilter(filterClass);
	}

	protected final int getRecyclerViewWidth() {
		return flexAdapter.getRecyclerViewWidth();
	}

	protected final int getRecyclerViewHeight() {
		return flexAdapter.getRecyclerViewHeight();
	}

	//----------------------------------------
	// click event
	//----------------------------------------

	View.OnClickListener onClickListener;
	View.OnLongClickListener onLongClickListener;

	protected void addChildViewClickListener(int resID) {
		addChildViewClickListener(findViewById(resID));
	}

	protected void addChildViewClickListener(View view) {
		if (null == view) return;
		if (null != onClickListener) view.setOnClickListener(onClickListener);
		if (null != onLongClickListener) view.setOnLongClickListener(onLongClickListener);
	}

	protected void removeChildViewClickListener(int resID) {
		removeChildViewClickListener(findViewById(resID));
	}

	protected void removeChildViewClickListener(View view) {
		if (null == view) return;

		view.setOnClickListener(null);
		view.setClickable(false);

		view.setOnLongClickListener(null);
		view.setLongClickable(false);
	}

	//----------------------------------------
	// drag event
	//----------------------------------------

	private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
				flexAdapter.itemTouchHelper.startDrag(ItemRenderer.this);
			}
			return false;
		}
	};

	private View dragView;

	protected void setDragView(int resId) {
		setDragView(findViewById(resId));
	}

	protected void setDragView(View view) {
		if (null != dragView) dragView.setOnTouchListener(null);

		dragView = view;
		if (null != dragView) dragView.setOnTouchListener(onTouchListener);
	}
}
