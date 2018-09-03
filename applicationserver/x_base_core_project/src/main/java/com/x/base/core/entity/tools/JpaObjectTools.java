package com.x.base.core.entity.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.openjpa.persistence.jdbc.ElementColumn;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public class JpaObjectTools {
	public static boolean isList(Path<?> path) throws Exception {
		return List.class.isAssignableFrom(path.getJavaType());
	}

	public static <T extends JpaObject> Integer definedLength(Class<T> clz, String attribute) throws Exception {
		Field field = FieldUtils.getField(clz, attribute, true);
		return definedLength(clz, field);
	}

	public static <T extends JpaObject> Integer definedLength(Class<T> clz, Field field) throws Exception {
		if (null == field) {
			throw new Exception(
					"can not find field with Class:" + clz + ", attribute:" + Objects.toString(field) + ".");
		}
		Integer length = null;
		for (int i = 0; i < 1; i++) {
			Column column = field.getAnnotation(Column.class);
			if (null != column) {
				length = column.length();
				break;
			}
			ElementColumn elementColumn = field.getAnnotation(ElementColumn.class);
			if (null != elementColumn) {
				length = elementColumn.length();
			}
		}
		if (null == length) {
			throw new Exception("can not find @Column or @ElementColumn with Class:" + clz + ", attribute:"
					+ Objects.toString(field) + ".");
		}
		return length;
	}

	public static <T extends JpaObject> boolean withinDefinedLength(String value, Class<T> clz, Field field)
			throws Exception {
		return StringTools.utf8Length(value) < definedLength(clz, field);
	}

	public static <T extends JpaObject> boolean withinDefinedLength(String value, Class<T> clz, String attribute)
			throws Exception {
		return StringTools.utf8Length(value) < definedLength(clz, attribute);
	}

	public static Date confirm(Date date) throws Exception {
		return (date == null) ? null : new Date(date.getTime());
	}

	// /*
	// * 在使用cb.isMember的情况下,将List<String> 转化为Expression<Set<String>>
	// */
	// public static <T extends JpaObject> Expression<Set<String>>
	// stringValueListToIsMemberExpression(Class<T> cls,
	// String attribute, CriteriaBuilder cb, List<String> values) throws Exception {
	// HashMap<String, String> map = new HashMap<String, String>();
	// StringTools.filterLessThanOrEqualToUtf8Length(values,
	// JpaObjectTools.definedLength(cls, attribute))
	// .forEach(o -> {
	// map.put(o, o);
	// });
	// return cb.keys(map);
	// }
	
	/* 根据一组一组对齐 */
	public static <L extends JpaObject, R extends JpaObject> List<Pair<L, R>> align(List<L> lefts, List<R> rights,
			boolean skipNull, boolean skipEmptyString, String... fields) throws Exception {
		List<Pair<L, R>> list = new ArrayList<>();
		List<L> find_lefts = new ArrayList<>();
		List<R> find_rights = new ArrayList<>();
		loop: for (L l : ListTools.trim(lefts, true, false)) {
			for (R r : ListTools.trim(rights, true, false)) {
				if (StringUtils.isNotEmpty(l.getId()) && StringUtils.equals(l.getId(), r.getId())) {
					list.add(new ImmutablePair<>(l, r));
					find_lefts.add(l);
					find_rights.add(r);
					continue loop;
				}
			}
		}
		for (String field : fields) {
			loop: for (L l : ListUtils.subtract(ListTools.trim(lefts, true, false), find_lefts)) {
				Object lo = l.get(field);
				if (skipNull && (null == lo)) {
					continue;
				}
				if (skipEmptyString && CharSequence.class.isAssignableFrom(lo.getClass())
						&& (StringUtils.isEmpty(Objects.toString(lo, "")))) {
					continue;
				}
				for (R r : ListUtils.subtract(ListTools.trim(rights, true, false), find_rights)) {
					Object ro = r.get(field);
					if (Objects.equals(lo, ro)) {
						list.add(new ImmutablePair<>(l, r));
						find_lefts.add(l);
						find_rights.add(r);
						continue loop;
					}
				}
			}
		}
		for (L l : ListUtils.subtract(ListTools.trim(lefts, true, false), find_lefts)) {
			list.add(new ImmutablePair<>(l, null));
		}
		for (R r : ListUtils.subtract(ListTools.trim(rights, true, false), find_rights)) {
			list.add(new ImmutablePair<>(null, r));
		}
		return list;
	}
}