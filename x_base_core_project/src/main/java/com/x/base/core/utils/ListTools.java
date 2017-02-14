package com.x.base.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.ObjectUtils;

public class ListTools {

	public static <T> List<T> add(List<T> list, boolean ignoreNull, boolean unique,
			@SuppressWarnings("unchecked") T... ts) throws Exception {
		List<T> adds = new ArrayList<>();
		for (T t : ts) {
			adds.add(t);
		}
		return add(list, ignoreNull, unique, adds);
	}

	public static <T> List<T> add(List<T> list, boolean ignoreNull, boolean unique, Collection<T> ts) throws Exception {
		List<T> newList = new ArrayList<>();
		List<T> adds = new ArrayList<>();
		if (isNotEmpty(list)) {
			adds.addAll(list);
		}
		if (null != ts) {
			adds.addAll(ts);
		}
		for (T t : adds) {
			if (null == t && ignoreNull) {
				continue;
			}
			if (!newList.contains(t)) {
				newList.add(t);
			}
		}
		return newList;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> addWithProperty(Object obj, String propertyName, boolean ignoreNull, T... ts)
			throws Exception {
		List<T> list = new ArrayList<>();
		ListOrderedSet<T> set = new ListOrderedSet<T>();
		Object o = PropertyUtils.getProperty(obj, propertyName);
		if (null != o) {
			set.addAll((List<T>) o);
		}
		for (T t : ts) {
			if (null == t && ignoreNull) {
				continue;
			}
			if (!set.contains(t)) {
				set.add(t);
				list.add(t);
			}
		}
		PropertyUtils.setProperty(obj, propertyName, set.asList());
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> addWithProperty(Object obj, String propertyName, boolean ignoreNull, List<T> ts)
			throws Exception {
		List<T> list = new ArrayList<>();
		ListOrderedSet<T> set = new ListOrderedSet<T>();
		Object o = PropertyUtils.getProperty(obj, propertyName);
		if (null != o) {
			set.addAll((List<T>) o);
		}
		if (null != ts) {
			for (T t : ts) {
				if (null == t && ignoreNull) {
					continue;
				}
				if (!set.contains(t)) {
					set.add(t);
					list.add(t);
				}
			}
		}
		PropertyUtils.setProperty(obj, propertyName, set.asList());
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> subtractWithProperty(Object obj, String propertyName, T... ts) throws Exception {
		List<T> list = new ArrayList<>();
		ListOrderedSet<T> set = new ListOrderedSet<T>();
		Object o = PropertyUtils.getProperty(obj, propertyName);
		if (null != o) {
			set.addAll((List<T>) o);
		}
		for (T t : ts) {
			if (set.contains(t)) {
				set.remove(t);
				list.add(t);
			}
		}
		PropertyUtils.setProperty(obj, propertyName, set.asList());
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> subtractWithProperty(Object obj, String propertyName, List<T> ts) throws Exception {
		List<T> list = new ArrayList<>();
		ListOrderedSet<T> set = new ListOrderedSet<T>();
		Object o = PropertyUtils.getProperty(obj, propertyName);
		if (null != o) {
			set.addAll((List<T>) o);
		}
		if (null != ts) {
			for (T t : ts) {
				if (set.contains(t)) {
					set.remove(t);
					list.add(t);
				}
			}
		}
		PropertyUtils.setProperty(obj, propertyName, set.asList());
		return list;
	}

	public static <T> boolean contains(List<T> list, T t) throws Exception {
		if (null == list) {
			return false;
		}
		return list.contains(t);
	}

	public static <T> boolean containsAll(List<T> list, List<T> other) throws Exception {
		if ((null == list) || (null == other)) {
			return false;
		}
		return CollectionUtils.containsAll(list, other);
	}

	public static <T> boolean containsAny(List<T> list, List<T> other) throws Exception {
		if ((null == list) || (null == other)) {
			return false;
		}
		return CollectionUtils.containsAny(list, other);
	}

	public static <T> ArrayList<T> concreteArrayList(List<T> list, boolean ignoreNull, boolean unique,
			@SuppressWarnings("unchecked") T... ts) {
		ArrayList<T> arrayList = new ArrayList<>();
		if (null != list) {
			arrayList.addAll(list);
		}
		for (T t : ts) {
			if (ignoreNull && (null == t)) {
				continue;
			}
			if (unique && arrayList.contains(t)) {
				continue;
			}
			arrayList.add(t);
		}
		return arrayList;
	}

	public static <T> boolean isEmpty(List<T> list) {
		if (null == list || list.isEmpty()) {
			return true;
		}
		return false;
	}

	public static <T> boolean isNotEmpty(List<T> list) {
		return !isEmpty(list);
	}

	public static <T> List<T> nullToEmpty(List<T> list) {
		if (null == list) {
			return new ArrayList<T>();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T, W> List<T> extractProperty(List<W> list, String property, Class<T> clz, Boolean ignoreNull,
			Boolean unique) throws Exception {
		List<T> properties = new ArrayList<>();
		if (isEmpty(list)) {
			return properties;
		}
		for (W w : list) {
			Object o = PropertyUtils.getProperty(w, property);
			if (null == o && ignoreNull) {
				continue;
			}
			if (unique && properties.contains(o)) {
				continue;
			}
			if (null == o) {
				properties.add(null);
			} else {
				properties.add((T) o);
			}
		}
		return properties;
	}

	public static void asc(List<Object> list) {
		asc(list, false);
	}

	public static void asc(List<Object> list, boolean nullGreater) {
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Object o1, Object o2) {
				Comparable c1 = null;
				Comparable c2 = null;
				if (null != o1) {
					c1 = (o1 instanceof Comparable) ? (Comparable) o1 : o1.toString();
				}
				if (null != o2) {
					c2 = (o2 instanceof Comparable) ? (Comparable) o2 : o2.toString();
				}
				return ObjectUtils.compare(c1, c2, nullGreater);
			}
		});
	}

	public static void desc(List<Object> list) {
		desc(list, false);
	}

	public static void desc(List<Object> list, boolean nullGreater) {
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Object o1, Object o2) {
				Comparable c1 = null;
				Comparable c2 = null;
				if (null != o1) {
					c1 = (o1 instanceof Comparable) ? (Comparable) o1 : o1.toString();
				}
				if (null != o2) {
					c2 = (o2 instanceof Comparable) ? (Comparable) o2 : o2.toString();
				}
				return ObjectUtils.compare(c2, c1, nullGreater);
			}
		});
	}
}