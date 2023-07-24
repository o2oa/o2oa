package com.x.base.core.project.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ObjectUtils;

public class SortTools {

	public static void asc(List<?> list, final String... attributes) throws Exception {
		asc(list, false, attributes);
	}

	public static void asc(List<?> list, final boolean nullGreater, final String... attributes) throws Exception {
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Object o1, Object o2) {
				int c = 0;
				try {
					for (String attribute : attributes) {
						Object p1 = PropertyUtils.getProperty(o1, attribute);
						Object p2 = PropertyUtils.getProperty(o2, attribute);
						Comparable c1 = null;
						Comparable c2 = null;
						if (null != p1) {
							c1 = (p1 instanceof Comparable) ? (Comparable) p1 : p1.toString();
						}
						if (null != p2) {
							c2 = (p2 instanceof Comparable) ? (Comparable) p2 : p2.toString();
						}
						c = ObjectUtils.compare(c1, c2, nullGreater);
						if (c != 0) {
							return c;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return c;
			}
		});
	}

	public static void desc(List<?> list, final String... attributes) throws Exception {
		desc(list, false, attributes);
	}

	public static void desc(List<?> list, final boolean nullGreater, final String... attributes) throws Exception {
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Object o1, Object o2) {
				int c = 0;
				try {
					for (String attribute : attributes) {
						Object p1 = PropertyUtils.getProperty(o1, attribute);
						Object p2 = PropertyUtils.getProperty(o2, attribute);
						Comparable c1 = null;
						Comparable c2 = null;
						if (null != p1) {
							c1 = (p1 instanceof Comparable) ? (Comparable) p1 : p1.toString();
						}
						if (null != p2) {
							c2 = (p2 instanceof Comparable) ? (Comparable) p2 : p2.toString();
						}
						c = ObjectUtils.compare(c2, c1, nullGreater);
						if (c != 0) {
							return c;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return c;
			}
		});
	}

	public static void order(List<?> list, final String attribute, List<?> orders) throws Exception {
		order(list, false, attribute, orders);
	}

	public static void order(List<?> list, final boolean nullGreater, final String attribute, List<?> orders)
			throws Exception {
		Collections.sort(list, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				int c = 0;
				try {
					Object p1 = PropertyUtils.getProperty(o1, attribute);
					Object p2 = PropertyUtils.getProperty(o2, attribute);
					int c1 = orders.indexOf(p1);
					int c2 = orders.indexOf(p2);
					c = ObjectUtils.compare(c1, c2, nullGreater);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return c;
			}
		});
	}

	public static void asc(List<?> list) {
		asc(list, false);
	}

	public static void asc(List<?> list, boolean nullGreater) {
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

	public static void desc(List<?> list) {
		desc(list, false);
	}

	public static void desc(List<?> list, boolean nullGreater) {
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

	/* 按模板给出的顺序进行排序,不在模板中的排在最前 */
	public static <T> List<T> orderWithTemplateNotFoundFirst(List<T> list, List<T> template) {
		if ((null == list) || template == list) {
			return list;
		}
		return list.stream().sorted(Comparator.comparing(Function.identity(), (x, y) -> {
			int indx = template.indexOf(x);
			int indy = template.indexOf(y);
			if (indx == indy) {
				return 0;
			} else if (indx == -1) {
				return -1;
			} else if (indy == -1) {
				return 1;
			} else {
				return indx - indy;
			}
		})).collect(Collectors.toList());
	}

	/* 按模板给出的顺序进行排序,不在模板中的排在最后 */
	public static <T> List<T> orderWithTemplateNotFoundLast(List<T> list, List<T> template) {
		if ((null == list) || template == list) {
			return list;
		}
		return list.stream().sorted(Comparator.comparing(Function.identity(), (x, y) -> {
			int indx = template.indexOf(x);
			int indy = template.indexOf(y);
			if (indx == indy) {
				return 0;
			} else if (indx == -1) {
				return 1;
			} else if (indy == -1) {
				return -1;
			} else {
				return indx - indy;
			}
		})).collect(Collectors.toList());
	}
}