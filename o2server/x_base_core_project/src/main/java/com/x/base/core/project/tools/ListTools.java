package com.x.base.core.project.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

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
			for (T t : list) {
				if (ignoreNull && (null == t)) {
					continue;
				}
				if (unique && arrayList.contains(t)) {
					continue;
				}
				arrayList.add(t);
			}
		}
		if (null != ts) {
			for (T t : ts) {
				if (ignoreNull && (null == t)) {
					continue;
				}
				if (unique && arrayList.contains(t)) {
					continue;
				}
				arrayList.add(t);
			}
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

	@SuppressWarnings("unchecked")
	public static <T, W> List<T> extractField(List<W> list, String name, Class<T> clz, Boolean ignoreNull,
			Boolean unique) throws Exception {
		List<T> values = new ArrayList<>();
		if (isEmpty(list)) {
			return values;
		}
		for (W w : list) {
			Object o = FieldUtils.readField(w, name, true);
			if (null == o && ignoreNull) {
				continue;
			}
			if (unique && values.contains(o)) {
				continue;
			}
			if (null == o) {
				values.add(null);
			} else {
				values.add((T) o);
			}
		}
		return values;
	}

	public static <T> T findWithProperty(List<T> list, String property, Object value) throws Exception {
		T t = null;
		if (isNotEmpty(list)) {
			t = list.stream().filter(o -> {
				Object v = null;
				try {
					v = PropertyUtils.getProperty(o, property);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return Objects.equals(v, value);
			}).findFirst().orElse(null);
		}
		return t;
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
	 * @param list     原始字符串List
	 * @param includes 需要包含的字符串List,可以使用*在结尾作为通配符
	 * @param excludes 需要剔除的字符串List,可以使用*在结尾作为通配符
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

	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(T... ts) {
		List<T> list = new ArrayList<>();
		for (T t : ts) {
			list.add(t);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(List<T> list, T... ts) {
		List<T> os = new ArrayList<>();
		if (!isEmpty(list)) {
			os.addAll(list);
		}
		for (T t : ts) {
			os.add(t);
		}
		return os;
	}

	public static <T> T last(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	public static <T> Integer size(List<T> list) {
		if (isNotEmpty(list)) {
			return list.size();
		}
		return 0;
	}

	/*
	 * 将数组按指定的分类值进行分类
	 */
	public static <T, W> List<T> groupStick(List<T> sourceList, List<W> groupList, String sourceProperty,
			String groupProperty, String stickProperty) {
		if (isEmpty(sourceList) || isEmpty(groupList)) {
			return sourceList;
		}
		Map<Object, List<W>> map = groupList.stream().collect(Collectors.groupingBy(o -> {
			try {
				return PropertyUtils.getProperty(o, groupProperty);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}, Collectors.mapping(o -> o, Collectors.toList())));
		sourceList.stream().forEach(o -> {
			try {
				Object key = PropertyUtils.getProperty(o, sourceProperty);
				List<W> entries = map.get(key);
				if (null != entries) {
					PropertyUtils.setProperty(o, stickProperty, entries);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return sourceList;
	}

	public static <T> Boolean same(List<T> list) {
		if (ListTools.isEmpty(list)) {
			return true;
		}
		T t = list.get(0);
		for (T o : list) {
			if (!Objects.equals(t, o)) {
				return false;
			}
		}
		return true;
	}

	public static <T, W> W parallel(List<T> ts, T t, List<W> ws) {
		if (ListTools.isEmpty(ts) || ListTools.isEmpty(ws) || null == t) {
			return null;
		}
		int index = ts.indexOf(t);
		if (index < 0 || index >= ws.size()) {
			return null;
		}
		return ws.get(index);
	}

	public static <T> List<T> randomWithRate(List<T> list, Double rate) {
		if (ListTools.isEmpty(list) || (rate >= 1.0)) {
			return list;
		}
		List<T> os = new ArrayList<>();
		if (rate == null || (rate <= 0)) {
			return os;
		}
		int size = (int) (list.size() * rate);
		if (size > 0) {
			Random r = new Random();
			HashSet<Integer> set = new HashSet<>();
			for (;;) {
				set.add(r.nextInt(list.size()));
				if (set.size() >= size) {
					break;
				}
			}
			set.stream().sorted().forEach(i -> {
				os.add(list.get(i));
			});
		}
		return os;
	}

	public static <T, W> List<T> removePropertyNotIn(List<T> list, String propertyName, Collection<W> values)
			throws Exception {
		List<T> os = new ArrayList<>();
		if (ListTools.isEmpty(list)) {
			return list;
		}
		for (T t : list) {
			if (values.contains(PropertyUtils.getProperty(t, propertyName))) {
				os.add(t);
			}
		}
		return os;
	}

	/**
	 * 判断两个字符串集合是否内容一致
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static boolean isSameList(List<String> list1, List<String> list2) {
		if (list1 == null) {
			list1 = new ArrayList<>();
		}
		if (list2 == null) {
			list2 = new ArrayList<>();
		}
		if (list1 == list2)
			return true;
		if (list1.size() != list2.size())
			return false;
		for (String str : list1) {
			if (!list2.contains(str)) {
				return false;
			}
		}
		for (String str : list2) {
			if (!list1.contains(str)) {
				return false;
			}
		}
		return true;
	}

	/* 判断是否是List对象 */
	public static boolean isList(Object obj) {
		if (null == obj) {
			return true;
		} else if (List.class.isAssignableFrom(obj.getClass())) {
			return true;
		}
		return false;
	}

	public static String toStringJoin(Object obj, String separator) {
		if (isList(obj)) {
			return StringUtils.join((List<?>) obj, separator);
		} else {
			return obj.toString();
		}
	}

	public static String toStringJoin(Object obj) {
		if (isList(obj)) {
			return StringUtils.join((List<?>) obj, ",");
		} else {
			return obj.toString();
		}
	}

	/* 根据属性进行配对 */
	public static <T, W> Map<T, W> pairWithProperty(List<T> ts, String tProperty, List<W> ws, String wProperty)
			throws Exception {
		Map<T, W> map = new LinkedHashMap<>();
		if ((null != ts) && (null != ws) && StringUtils.isNotEmpty(tProperty) && StringUtils.isNotEmpty(wProperty)) {
			for (T t : ts) {
				if (null != t) {
					W w = findWithProperty(ws, wProperty, PropertyUtils.getProperty(t, tProperty));
					if (null != w) {
						map.put(t, w);
					}
				}
			}
		}
		return map;
	}

	public static List<String> addStringToList(String source, List<String> targetList) {
		if (targetList == null) {
			targetList = new ArrayList<>();
		}
		if (StringUtils.isEmpty(source)) {
			return targetList;
		}
		if (!targetList.contains(source)) {
			targetList.add(source);
		}
		return targetList;
	}

	public static List<String> removeStringFromList(String source, List<String> targetList) {
		if (targetList == null) {
			targetList = new ArrayList<>();
		}
		if (StringUtils.isEmpty(source)) {
			return targetList;
		}
		if (targetList.contains(source)) {
			targetList.remove(source);
		}
		return targetList;
	}

}