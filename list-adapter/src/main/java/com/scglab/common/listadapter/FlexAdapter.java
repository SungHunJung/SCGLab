package com.scglab.common.listadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.scglab.common.listadapter.filter.BaseFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by shj on 2017. 9. 13..
 */
public class FlexAdapter extends RecyclerView.Adapter<ItemRenderer> implements Filterable {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Item {
	}

	private final RendererFactory RENDERER_FACTORY;

	private RecyclerView recyclerView;

	public FlexAdapter(RendererFactory rendererFactory) {
		RENDERER_FACTORY = rendererFactory;
		ITEM_STORAGE = new ItemStore();
	}

	//----------------------------------------
	// view holder
	//----------------------------------------

	@Override
	public ItemRenderer onCreateViewHolder(ViewGroup parent, int viewType) {
		recyclerView = (RecyclerView) parent;
		return RENDERER_FACTORY.createItemRenderer(parent, viewType);
	}

	@Override
	public void onBindViewHolder(ItemRenderer holder, int position) {
		holder.flexAdapter = this;
		holder.onClickListener = onClickListener;
		holder.onLongClickListener = onLongClickListener;

		if (position == 0) holder.setPrevItem(null);
		else holder.setPrevItem(getItem(position - 1));

		if (position + 1 < getItemCount()) holder.setNextItem(getItem(position + 1));
		else holder.setNextItem(null);

		holder.itemView.setOnClickListener(null);
		holder.itemView.setOnLongClickListener(null);

		holder.setCurrentItem(getItem(position));

		if (holder.itemView.hasOnClickListeners() == false) {
			holder.itemView.setOnClickListener(onClickListener);
			holder.itemView.setOnLongClickListener(onLongClickListener);
		}
	}

	@Override
	public void onViewAttachedToWindow(ItemRenderer holder) {
		super.onViewAttachedToWindow(holder);
		holder.onAttachedRenderer();
	}

	@Override
	public void onViewDetachedFromWindow(ItemRenderer holder) {
		super.onViewDetachedFromWindow(holder);
		holder.onDetachedRenderer();
	}

	@Override
	public boolean onFailedToRecycleView(ItemRenderer holder) {
		holder.onIdle();
		return super.onFailedToRecycleView(holder);
	}

	@Override
	public void onViewRecycled(ItemRenderer holder) {
		super.onViewRecycled(holder);
		holder.onIdle();
	}

	@Override
	public int getItemViewType(int position) {
		final Object item = getItem(position);

		for (Method method : item.getClass().getMethods()) {
			if (method.isAnnotationPresent(TypeStore.DefineItem.class)) {
				try {
					return (int) method.invoke(item);
				} catch (Exception ignored) {
				}
			}
		}

		return TypeStore.getInstance().getType(item.getClass());
	}

	public int getViewPosition(View view) {
		view = getItemView(view);
		if (null != view) {
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
			return layoutParams.getViewAdapterPosition();
		}

		return -1;
	}

	private View getItemView(View childView) {
		while (childView != null) {
			if (childView.getLayoutParams() instanceof RecyclerView.LayoutParams) {
				return childView;
			}

			childView = (View) childView.getParent();
		}

		return null;
	}

	//----------------------------------------
	// model
	//----------------------------------------

	private final ItemStore ITEM_STORAGE;

	public void setModels(List<Object> list) {
		ITEM_STORAGE.clear();
		ITEM_STORAGE.addAll(list);
		notifyDataSetChanged();
	}

	public void clear() {
		ITEM_STORAGE.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return ITEM_STORAGE.size();
	}

	public Object getItem(int position) {
		return ITEM_STORAGE.get(position);
	}

	public int getItemPosition(Object listItem) {
		return ITEM_STORAGE.indexOfVisible(listItem);
	}

	public void clearAllItem() {
		if (null != ITEM_STORAGE) {
			ITEM_STORAGE.clear();
			notifyDataSetChanged();
		}
	}

	public void addItem(int position, Object item) {
		ITEM_STORAGE.add(position, item);
		notifyItemInserted(position);
	}

	public void addItem(Object item) {
		ITEM_STORAGE.add(item);
		notifyItemInserted(ITEM_STORAGE.size());
	}

	public void removeItem(Object item) {
		int position = getItemPosition(item);
		removeItem(position);
	}

	public void removeItem(int position) {
		if (position != -1) {
			ITEM_STORAGE.remove(position);
			notifyItemRemoved(position);
		}
	}

	public void removeItems(int start, int end) {
		final int removeCount = end - start + 1;
		if (removeCount <= 0) return;

		while (start <= end) {
			ITEM_STORAGE.remove(start);
			end--;
		}

		notifyItemRangeRemoved(start, removeCount);
	}

	public void replaceItem(Object oldItem, Object newItem) {
		int position = getItemPosition(oldItem);
		replaceItem(position, newItem);
	}

	public void replaceItem(int position, Object newItem) {
		if (position != -1) {
			ITEM_STORAGE.remove(position);
			ITEM_STORAGE.add(position, newItem);
			notifyItemChanged(position);
		}
	}

	//----------------------------------------
	// notify
	//----------------------------------------

	public void notifyItemChanged(Object object) {
		int position = getItemPosition(object);
		if (position == -1) return;

		if (null != recyclerView) {
			View view;
			for (int index = 0; index < recyclerView.getChildCount(); index++) {
				view = recyclerView.getChildAt(index);
				if (position == getViewPosition(view)) {
					ItemRenderer itemRenderer = (ItemRenderer) recyclerView.getChildViewHolder(view);
					onBindViewHolder(itemRenderer, position);
					return;
				}
			}
		}

		notifyItemChanged(position);
	}

	public void notifyItemInserted(Object object) {
		int position = getItemPosition(object);
		if (position == -1) return;
		notifyItemInserted(position);
	}

	public void notifyItemRemoved(Object object) {
		int position = getItemPosition(object);
		if (position == -1) return;
		notifyItemRemoved(position);
	}

	//----------------------------------------
	// select
	//----------------------------------------

	private boolean isSelectMode;

	public boolean isSelectMode() {
		return isSelectMode;
	}

	public void setSelectMode(boolean mode) {
		setSelectMode(mode, false);
	}

	public void setSelectMode(boolean mode, boolean clearSelected) {
		int removeSize = 0;
		if (clearSelected) removeSize = ITEM_STORAGE.clearSelectedList();

		boolean oldMode = isSelectMode;
		isSelectMode = mode;

		if (removeSize > 0 || isSelectMode != oldMode) notifyDataSetChanged();
	}

	public void addSelectItem(Object object) {
		if (ITEM_STORAGE.contains(object))
			ITEM_STORAGE.addSelectItem(object);
	}

	public void removeSelectItem(Object object) {
		if (ITEM_STORAGE.contains(object))
			ITEM_STORAGE.removeSelectItem(object);
	}

	public boolean isSelected(Object object) {
		return ITEM_STORAGE.isSelected(object);
	}

	public boolean toggleSelectItem(Object object) {
		boolean result = false;

		if (ITEM_STORAGE.contains(object)) {
			result = ITEM_STORAGE.toggleSelectItem(object);
			notifyItemChanged(object);
		}

		return result;
	}

	public List<Object> getSelectedItemList() {
		return ITEM_STORAGE.getSelectedItemList();
	}

	//----------------------------------------
	// filter
	//----------------------------------------

	private Class filterClass = BaseFilter.class;

	public <F extends BaseFilter> void setFilter(Class<F> filterClass) {
		this.filterClass = filterClass;
	}

	@Override
	public Filter getFilter() {
		if (null == filterClass) {
			throw new RuntimeException("Set filter class (e.g., adapter.setFilter(class))");
		}

		try {
			Constructor constructor = filterClass.getConstructor(getClass(), ItemStore.class);
			return (Filter) constructor.newInstance(this, ITEM_STORAGE);

		} catch (NoSuchMethodException e) {
			throw new RuntimeException("constructor cannot be found", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("abstract class or interface", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("constructor method no access", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("checkout package-name", e);
		}
	}

	//----------------------------------------
	// click event
	//----------------------------------------

	private OnItemClickEventHandler onItemClickEventHandler;

	public void setOnItemClickEventHandler(OnItemClickEventHandler handler) {
		onItemClickEventHandler = handler;
	}

	private final View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (null == onItemClickEventHandler) return;

			final Object item = getItem(getViewPosition(view));

			if (isItemView(view)) onItemClickEventHandler.onItemClick(item);
			else onItemClickEventHandler.onChildViewClick(item, view.getId());
		}
	};

	private final View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View view) {
			if (null == onItemClickEventHandler) return true;

			final Object item = getItem(getViewPosition(view));

			if (isItemView(view)) onItemClickEventHandler.onItemLongClick(item);
			else onItemClickEventHandler.onChildViewLongClick(item, view.getId());

			return true;
		}
	};

	private boolean isItemView(View view) {
		return view.getLayoutParams() instanceof RecyclerView.LayoutParams;
	}
}
