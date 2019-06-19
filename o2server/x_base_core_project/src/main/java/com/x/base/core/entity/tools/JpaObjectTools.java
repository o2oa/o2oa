package com.x.base.core.entity.tools;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.criteria.Path;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ElementColumn;

import com.x.base.core.entity.JpaObject;
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

	public static Set<Class<?>> scanMappedSuperclass(Class<?> clz) throws Exception {
		Set<Class<?>> set = new HashSet<Class<?>>();
		set.add(clz);
		Class<?> s = clz.getSuperclass();
		while (null != s) {
			if (null != s.getAnnotation(MappedSuperclass.class)) {
				set.add(s);
			}
			s = s.getSuperclass();
		}
		return set;
	}

}