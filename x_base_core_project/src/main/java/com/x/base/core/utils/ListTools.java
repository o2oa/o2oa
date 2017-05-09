package com.x.base.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

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

	public static <T> boolean contains(List<T> list, T t) {
		if (null == list) {
			return false;
		}
		return list.contains(t);
	}

	public static <T> boolean containsAll(List<T> list, List<T> other) {
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

	public static <T> ArrayList<T> trim(List<T> list, boolean ignoreNull, boolean unique,
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

	public static boolean isEmpty(List<?>... os) {
		for (List<?> o : os) {
			if ((null != o) && (!o.isEmpty())) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(List<?>... os) {
		return !isEmpty(os);
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

	public static <T extends Comparable<?>> T maxCountElement(List<T> list) {
		if (null == list || list.isEmpty()) {
			return null;
		}
		Map<T, Long> group = list.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		LinkedHashMap<T, Long> sort = group.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return sort.entrySet().stream().findFirst().get().getKey();
	}

	public static <T> List<T> includesExcludes(List<T> list, List<T> includes, List<T> excludes) {
		if (isEmpty(list)) {
			return list;
		}
		if (isNotEmpty(includes)) {
			list = ListUtils.intersection(list, includes);
		}
		if (isNotEmpty(excludes)) {
			list = ListUtils.subtract(list, excludes);
		}
		return list;
	}

	/**
	 * 
	 * @param list
	 *            原始字符串List
	 * @param includes
	 *            需要包含的字符串List,可以使用*在结尾作为通配符
	 * @param excludes
	 *            需要剔除的字符串List,可以使用*在结尾作为通配符
	 * @return
	 */
	public static List<String> includesExcludesWildcard(List<String> list, List<String> includes,
			List<String> excludes) {
		if (isEmpty(list)) {
			return list;
		}
		if (ListTools.isNotEmpty(includes)) {
			final List<String> wildcardIncludes = includes.stream().filter(s -> {
				return StringUtils.endsWith(s, "*");
			}).map(s -> {
				return StringUtils.substringBeforeLast(s, "*");
			}).distinct().collect(Collectors.toList());
			list = list.stream().filter(s -> {
				return ListTools.contains(includes, s) || (wildcardIncludes.stream().filter(w -> {
					return StringUtils.startsWith(s, w);
				}).count() > 0);
			}).distinct().collect(Collectors.toList());
		}
		if (ListTools.isNotEmpty(excludes)) {
			final List<String> wildcardExcludes = excludes.stream().filter(s -> {
				return StringUtils.endsWith(s, "*");
			}).map(s -> {
				return StringUtils.substringBeforeLast(s, "*");
			}).distinct().collect(Collectors.toList());
			list = list.stream().filter(s -> {
				return !((ListTools.contains(excludes, s)) || (wildcardExcludes.stream().filter(w -> {
					return StringUtils.startsWith(s, w);
				}).count() > 0));
			}).distinct().collect(Collectors.toList());
		}
		return list;
	}

	public static <T> List<List<T>> batch(List<T> list, Integer size) throws Exception {
		if (null == size || size < 1) {
			throw new Exception("size can not be null or less than 1.");
		}
		List<List<T>> result = new ArrayList<>();
		if (isEmpty(list)) {
			return result;
		}
		List<T> os = null;
		for (int i = 0; i < list.size(); i++) {
			if (i % size == 0) {
				os = new ArrayList<T>();
			}
			os.add(list.get(i));
			if ((i % size == (size - 1)) || (i == list.size() - 1)) {
				result.add(os);
			}
		}
		return result;
	}

	public static <T> List<T> toList(T... ts) throws Exception {
		List<T> list = new ArrayList<>();
		for (T t : ts) {
			list.add(t);
		}
		return list;
	}

}