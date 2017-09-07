package com.scglab.common.listadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sh on 2016. 1. 5..
 */
public abstract class ItemViewHolder<T extends ListItem> extends RecyclerView.ViewHolder {

	ListAdapter<T> listAdapter;
	int position;
	private float density;


	View.OnClickListener onClickListener;
	View.OnLongClickListener onLongClickListener;

	public ItemViewHolder(View view) {
		super(view);

		density = view.getResources().getDisplayMetrics().density;
	}

	protected void setClickListener(View view) {
		if (null == view) return;

		view.setTag(itemView.getTag());
		if (null != onClickListener) view.setOnClickListener(onClickListener);
	}

	protected void setLongClickListener(View view) {
		if (null == view) return;

		view.setTag(itemView.getTag());
		if (null != onLongClickListener) view.setOnLongClickListener(onLongClickListener);
	}

	protected final void invalidate() {
		listAdapter.notifyItemChanged(position);
	}

	public float getDensity() {
		return density;
	}

	protected abstract void onBind(T model, boolean isEditMode);

	protected abstract void onNextItem(ListItem model);

	protected void onPrevItem(ListItem model) {
	}


}