package com.scglab.common.listadapter;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter<T extends ListItem> extends RecyclerView.Adapter<ItemViewHolder<T>> implements Filterable {

	private static final int EVENT_TAP = 1024;
	private static final int EVENT_LONG_TAP = 1025;

	private final ViewHolderFactory VIEW_HOLDER_FACTORY;

	private IListItemEventHandler<T> itemEventHandler;
	private ArrayList<T> models;

	private boolean isEditMode;

	public ListAdapter(ArrayList<T> models, ViewHolderFactory viewHolderFactory) {
		VIEW_HOLDER_FACTORY = viewHolderFactory;
		setModels(models);
	}

	@Override
	public ItemViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolderFactory.ViewHolderWrapper viewHolderWrapper = VIEW_HOLDER_FACTORY.get(viewType);
		if (null == viewHolderWrapper) {
			throw new RuntimeException("Not found ViewHolder and Layout in ViewHolderFactory (viewType : " + viewType + ")");
		}

		try {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			Class holderClass = viewHolderWrapper.getViewHolder();
			Constructor constructor = holderClass.getConstructor(View.class);

			return (ItemViewHolder<T>) constructor.newInstance(layoutInflater.inflate(viewHolderWrapper.getLayout(), parent, false));
		} catch (Exception ignored) {
			throw new RuntimeException(ignored.getLocalizedMessage());
		}
	}

	@Override
	public int getItemViewType(int position) {
		return getItemAtIndex(position).getType();
	}

	@Override
	public int getItemCount() {
		int size = 0;
		for (ListItem item : models) {
			if (item.isVisible()) size++;
		}

		return size;
	}

	@Override
	public void onBindViewHolder(ItemViewHolder<T> holder, int position) {
		T item = getItemAtIndex(position);

		switch (getItemViewType(position)) {
			case ListItem.Type.SEARCH:
				holder.itemView.setOnClickListener(null);
				holder.itemView.setOnLongClickListener(null);
				break;

			default:
				holder.itemView.setTag(item);
				holder.itemView.setOnClickListener(onClickListener);
				holder.itemView.setOnLongClickListener(onLongClickListener);

				holder.onClickListener = onClickListener;
				holder.onLongClickListener = onLongClickListener;
				break;
		}

		holder.position = position;
		holder.listAdapter = this;

		holder.onBind(item, isEditMode);

		if (position == 0) holder.onPrevItem(null);
		else holder.onPrevItem(getItemAtIndex(position - 1));

		if (position + 1 < getItemCount()) holder.onNextItem(getItemAtIndex(position + 1));
		else holder.onNextItem(null);
	}

	@Override
	public Filter getFilter() {
		return new KeywordFilter<>(this, models);
	}

	public void setItemEventHandler(IListItemEventHandler<T> handler) {
		itemEventHandler = handler;
	}

	public boolean isEditMode() {
		return isEditMode;
	}

	public void setEditMode(boolean mode, boolean releaseAll) {
		if (isEditMode == mode) {
			return;
		}

		if (releaseAll) {
			for (ListItem item : models) {
				item.setSelected(false);
			}
		}

		isEditMode = mode;
		notifyDataSetChanged();
	}

	public List<T> getSelectedItemList() {
		List<T> selectedList = new ArrayList<>();

		for (ListItem item : models) {
			if (item.isSelected()) selectedList.add((T) item);
		}

		return selectedList;
	}

	public void clear() {
		if (null != models) {
			models.clear();
			notifyDataSetChanged();
		}
	}

	public void setModels(ArrayList<T> list) {
		if (null != list) {
			models = (ArrayList<T>) list.clone();
		} else {
			models = new ArrayList<>();
		}

		notifyDataSetChanged();
	}

	public void addModel(int index, final T item) {
		models.add(index, item);
		notifyItemInserted(index);
	}

	public void addModel(final T item) {
		models.add(item);
		notifyItemInserted(models.size());
	}

	public void removeModel(final T item) {
		int index = getIndexAtItem(item);
		removeModel(index);
	}

	public void removeModel(final int index) {
		if (index != -1) {
			models.remove(index);
			notifyItemRemoved(index);
		}
	}

	public void removeModels(int start, int end) {
		final int removeCount = end - start + 1;
		if (removeCount <= 0) return;

		while (start <= end) {
			models.remove(start);
			end--;
		}

		notifyItemRangeRemoved(start, removeCount);
	}

	public int replaceModel(final T item) {
		int index = getIndexAtItem(item);
		if (index != -1) {
			models.remove(index);
			models.add(index, item);
			notifyItemChanged(index);
		}

		return index;
	}

	public int getIndexAtItem(final T item) {
		return models.indexOf(item);
	}

	public T getItemAtIndex(int position) {
		if (getItemCount() <= position) return null;

		int index = 0;
		while (0 < position || !models.get(index).isVisible()) {
			if (models.get(index).isVisible()) position--;
			index++;
		}

		return models.get(index);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (null == itemEventHandler) return;

			Message message = Message.obtain();
			message.what = EVENT_TAP;
			message.arg1 = v.getId();
			message.obj = v.getTag();

			handler.sendMessage(message);
		}
	};

	private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {

			if (null == itemEventHandler) return true;

			Message message = Message.obtain();
			message.what = EVENT_LONG_TAP;
			message.arg1 = v.getId();
			message.obj = v.getTag();
			handler.sendMessage(message);
			return true;
		}
	};

	@SuppressWarnings("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (null == itemEventHandler) return;
			final T item = (T) msg.obj;
			int position = 0;
			int unVisibleCount = 0;

			while (position < models.size()) {
				T t = models.get(position);

				if (t.isVisible()) {
					if (t.equals(item)) break;
				} else {
					unVisibleCount++;
				}

				position++;
			}

			position -= unVisibleCount;

			switch (msg.what) {
				case EVENT_TAP:
					if (itemEventHandler.onClick(item, msg.arg1)) {
						notifyItemChanged(position);
					}
					break;

				case EVENT_LONG_TAP:
					if (itemEventHandler.onLongClick(item, msg.arg1)) {
						notifyItemChanged(position);
					}
					break;
			}
		}
	};

}