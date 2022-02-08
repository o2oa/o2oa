package com.x.base.core.entity.tools;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.criteria.Path;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ElementColumn;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.tools.StringTools;

public class JpaObjectTools {

	public static boolean isList(Path<?> path) {
		return List.class.isAssignableFrom(path.getJavaType());
	}

	public static <T extends JpaObject> Integer definedLength(Class<T> clz, String attribute) {
		Field field = FieldUtils.getField(clz, attribute, true);
		return definedLength(clz, field);
	}

	public static <T extends JpaObject> Integer definedLength(Class<T> clz, Field field) throws IllegalStateException {
		if (null == field) {
			throw new IllegalStateException(
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
			throw new IllegalStateException("can not find @Column or @ElementColumn with Class:" + clz + ", attribute:"
					+ Objects.toString(field) + ".");
		}
		return length;
	}

	public static <T extends JpaObject> boolean withinDefinedLength(String value, Class<T> clz, Field field) {
		return StringTools.utf8Length(value) < definedLength(clz, field);
	}

	public static <T extends JpaObject> boolean withinDefinedLength(String value, Class<T> clz, String attribute) {
		return StringTools.utf8Length(value) < definedLength(clz, attribute);
	}

	public static Date confirm(Date date) {
		return (date == null) ? null : new Date(date.getTime());
	}

	public static Set<Class<?>> scanMappedSuperclass(Class<?> clz) {
		Set<Class<?>> set = new HashSet<>();
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

	/**
	 * 将entity对象detach
	 * 
	 * @param em
	 * @param objects
	 */
	public static <T extends JpaObject> void detach(EntityManager em, Collection<T> objects) {
		for (JpaObject o : objects) {
			em.detach(o);
		}
	}

	/**
	 * 将entity对象detach
	 * 
	 * @param em
	 * @param objects
	 */
	public static void detach(EntityManager em, JpaObject... objects) {
		for (JpaObject o : objects) {
			em.detach(o);
		}
	}

}