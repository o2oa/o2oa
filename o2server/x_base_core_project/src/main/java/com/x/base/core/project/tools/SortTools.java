package com.x.base.core.project.tools;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ObjectUtils;

public class SortTools {
	private static final Collator COLLATOR = Collator.getInstance(Locale.getDefault());

	public static void asc(List<?> list, final String... attributes) {
		asc(list, false, attributes);
	}

	public static void asc(List<?> list, final boolean nullGreater, final String... attributes) {
		Collections.sort(list, (Comparator<Object>) (o1, o2) -> {
			int c = 0;
			try {
				for (String attribute : attributes) {
					Object p1 = PropertyUtils.getProperty(o1, attribute);
					Object p2 = PropertyUtils.getProperty(o2, attribute);
					Comparable c1 = null;
					Comparable c2 = null;
					boolean flag = true;
					if (null != p1) {
						if(p1 instanceof String){
							flag = false;
						}
						c1 = (p1 instanceof Comparable) ? (Comparable) p1 : p1.toString();
					}
					if (null != p2) {
						c2 = (p2 instanceof Comparable) ? (Comparable) p2 : p2.toString();
					}else{
						flag = true;
					}
					if(flag) {
						c = ObjectUtils.compare(c1, c2, nullGreater);
					}else {
						c = COLLATOR.compare(c1, c2);
					}
					if (c != 0) {
						return c;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return c;
		});
	}

	public static void desc(List<?> list, final String... attributes) {
		desc(list, false, attributes);
	}

	public static void desc(List<?> list, final boolean nullGreater, final String... attributes) {
		Collections.sort(list, (Comparator<Object>) (o1, o2) -> {
			int c = 0;
			try {
				for (String attribute : attributes) {
					Object p1 = PropertyUtils.getProperty(o1, attribute);
					Object p2 = PropertyUtils.getProperty(o2, attribute);
					Comparable c1 = null;
					Comparable c2 = null;
					boolean flag = true;
					if (null != p1) {
						if(p1 instanceof String){
							flag = false;
						}
						c1 = (p1 instanceof Comparable) ? (Comparable) p1 : p1.toString();
					}
					if (null != p2) {
						c2 = (p2 instanceof Comparable) ? (Comparable) p2 : p2.toString();
					}else{
						flag = true;
					}
					if(flag) {
						c = ObjectUtils.compare(c2, c1, nullGreater);
					}else {
						c = COLLATOR.compare(c2, c1);
					}
					if (c != 0) {
						return c;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return c;
		});
	}

	public static void order(List<?> list, final String attribute, List<?> orders) {
		order(list, false, attribute, orders);
	}

	public static void order(List<?> list, final boolean nullGreater, final String attribute, List<?> orders) {
		Collections.sort(list, (Comparator<Object>) (o1, o2) -> {
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
		});
	}

	public static void asc(List<?> list) {
		asc(list, false);
	}

	public static void asc(List<?> list, boolean nullGreater) {
		Collections.sort(list, (Comparator<Object>) (o1, o2) -> {
			Comparable c1 = null;
			Comparable c2 = null;
			boolean flag = true;
			if (null != o1) {
				if(o1 instanceof String){
					flag = false;
				}
				c1 = (o1 instanceof Comparable) ? (Comparable) o1 : o1.toString();
			}
			if (null != o2) {
				c2 = (o2 instanceof Comparable) ? (Comparable) o2 : o2.toString();
			}else{
				flag = true;
			}
			return flag ? ObjectUtils.compare(c1, c2, nullGreater) : COLLATOR.compare(c1, c2);
		});
	}

	public static void desc(List<?> list) {
		desc(list, false);
	}

	public static void desc(List<?> list, boolean nullGreater) {
		Collections.sort(list, (Comparator<Object>) (o1, o2) -> {
			Comparable c1 = null;
			Comparable c2 = null;
			boolean flag = true;
			if (null != o1) {
				if(o1 instanceof String){
					flag = false;
				}
				c1 = (o1 instanceof Comparable) ? (Comparable) o1 : o1.toString();
			}
			if (null != o2) {
				c2 = (o2 instanceof Comparable) ? (Comparable) o2 : o2.toString();
			}else{
				flag = true;
			}
			return flag ? ObjectUtils.compare(c2, c1, nullGreater) : COLLATOR.compare(c2, c1);
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
