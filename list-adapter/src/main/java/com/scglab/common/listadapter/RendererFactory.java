package com.scglab.common.listadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

/**
 * Created by shj on 2017. 9. 12..
 */
public class RendererFactory extends Hashtable<Integer, RendererFactory.StructureObject> {

	public synchronized void put(final Class<? extends ItemRenderer> rendererClass, final int layoutId) {
		int type = TypeStore.getInstance().getType(rendererClass);
		if (type != -1) put(type, new StructureObject(layoutId, rendererClass));
		else throw new RuntimeException("Failed to add");
	}

	@Override
	public synchronized StructureObject put(Integer key, StructureObject value) {
		remove(key);
		return super.put(key, value);
	}

	ItemRenderer createItemRenderer(ViewGroup parent, int viewType) {
		RendererFactory.StructureObject structureObject = get(viewType);
		if (null == structureObject) throw new RuntimeException(TypeStore.getInstance().getModelClass(viewType) + " is not found in StructureTable.");

		ItemRenderer itemRenderer = createItemRenderer(parent, structureObject);
		autoBinding(itemRenderer, itemRenderer.getClass().getDeclaredFields());
		autoBinding(itemRenderer, itemRenderer.getClass().getFields());
		return itemRenderer;
	}

	private ItemRenderer createItemRenderer(ViewGroup parent, RendererFactory.StructureObject structureObject) {
		try {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			View view = layoutInflater.inflate(structureObject.getLayout(), parent, false);
			Constructor constructor = getItemRendererConstructor(structureObject);
			return (ItemRenderer) constructor.newInstance(view);
		} catch (InstantiationException e) {
			throw new RuntimeException("abstract class or interface", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("constructor method no access", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("checkout package-name", e);
		}
	}

	private Constructor getItemRendererConstructor(RendererFactory.StructureObject structureObject) {
		Constructor constructor;
		Class holderClass = structureObject.getViewHolder();

		try {
			constructor = holderClass.getConstructor(View.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("constructor cannot be found", e);
		}

		return constructor;
	}

	private void autoBinding(ItemRenderer itemRenderer, Field[] fields) {
		View view;

		for (Field field : fields) {
			try {
//				Class klass = field.getType().getClass();
//				while (klass.getSuperclass() != null) {
//					if (View.class.isAssignableFrom(klass)) {
//						break;
//					}
//					klass = klass.getSuperclass();
//				}

//				if (View.class.isAssignableFrom(field.getType().getClass()) == false && View.class.isAssignableFrom(field.getType().getSuperclass()) == false) {
//					continue;
//				}

				view = itemRenderer.findViewById(field);
				if (null != view) {
					field.setAccessible(true);
					field.set(itemRenderer, view);
				}
			} catch (Exception ignored) {
			}
		}
	}

	public static class StructureObject {
		private final int LAYOUT;
		private final Class VIEW_HOLDER;

		public StructureObject(int layout, Class viewHolder) {
			LAYOUT = layout;
			VIEW_HOLDER = viewHolder;
		}

		Class getViewHolder() {
			return VIEW_HOLDER;
		}

		int getLayout() {
			return LAYOUT;
		}
	}
}
