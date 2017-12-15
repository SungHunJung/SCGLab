package com.scglab.common.listadapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shj on 2017. 9. 13..
 */
public class TypeStore {

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DefineRenderer {
		int value();
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DefineItem {
	}

	private static TypeStore ourInstance = new TypeStore();

	public static TypeStore getInstance() {
		return ourInstance;
	}

	private final AtomicInteger TYPE_COUNTER;
	private final ConcurrentHashMap<String, Integer> TYPE_MAP;

	private TypeStore() {
		TYPE_COUNTER = new AtomicInteger(1024);
		TYPE_MAP = new ConcurrentHashMap<>();
	}

	public boolean isInstance(Class type, Object item) {
		return type.isInstance(item);
	}

	public <T> T tryCast(Class<T> t, Object i) {
		if (isInstance(t, i)) {
			return (T) i;
		} else {
			return null;
		}
	}

	public int getType(final Class<? extends ItemRenderer> rendererClass) {
		TypeStore.DefineRenderer annotation = rendererClass.getAnnotation(TypeStore.DefineRenderer.class);
		if (null != annotation) {
			int type = annotation.value();
			if (type >= 1024) {
				throw new RuntimeException("The value of TypeStore.DefineRenderer can not exceed 1024.");
			}
			return type;
		}

		Type genericSuperclass = null;
		Class klass = rendererClass;
		do {
			genericSuperclass = klass.getGenericSuperclass();
			klass = klass.getSuperclass();
		} while (genericSuperclass instanceof ParameterizedType == false);

		if (null == genericSuperclass)
			throw new RuntimeException("ItemRenderer must have generic type (e.g., ItemRenderer<ListItem>)");

		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		return getType(parameterizedType.getActualTypeArguments()[0]);
	}

	public int getType(Type value) {
		String key = value.toString();
		key = key.replace("class ", "");
		return getType(key);
	}

	public int getType(String key) {
		if (TYPE_MAP.containsKey(key)) {
			return TYPE_MAP.get(key);
		} else {
			int value = TYPE_COUNTER.incrementAndGet();
			TYPE_MAP.put(key, value);
			return value;
		}
	}

	public String getModelClass(final int type) {
		if (TYPE_MAP.containsValue(type)) {
			for (String key : TYPE_MAP.keySet()) {
				if (TYPE_MAP.get(key) == type) {
					return key;
				}
			}
		}

		return null;
	}
}
