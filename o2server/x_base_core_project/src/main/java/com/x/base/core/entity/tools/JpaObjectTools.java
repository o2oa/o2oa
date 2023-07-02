package com.x.base.core.entity.tools;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.criteria.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ElementColumn;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.tools.StringTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

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
		if (null != objects) {
			for (JpaObject o : objects) {
				if (null != o) {
					em.detach(o);
				}
			}
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
			if (null != o) {
				em.detach(o);
			}
		}
	}

	public static Collection<String> scanContainerEntityNames(ClassLoader classLoader) throws ClassNotFoundException {
		Set<String> set = new TreeSet<>();
		try (ScanResult sr = new ClassGraph().addClassLoader(classLoader).enableAnnotationInfo().scan()) {
			for (ClassInfo info : sr.getClassesWithAnnotation(ContainerEntity.class.getName())) {
				@SuppressWarnings("unchecked")
				Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader.loadClass(info.getName());
				set.add(cls.getName());
			}
		}
		return set;
	}

	public static Collection<Class<? extends JpaObject>> scanContainerEntities(ClassLoader classLoader)
			throws ClassNotFoundException {
		Set<Class<? extends JpaObject>> set = new TreeSet<>();
		try (ScanResult sr = new ClassGraph().addClassLoader(classLoader).enableAnnotationInfo().scan()) {
			for (ClassInfo info : sr.getClassesWithAnnotation(ContainerEntity.class.getName())) {
				@SuppressWarnings("unchecked")
				Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader.loadClass(info.getName());
				set.add(cls);
			}
		}
		return set;
	}

	public static String type(Field field) {
		String value = singleType(field);
		if (StringUtils.isNotEmpty(value)) {
			return value;
		}
		if (Collection.class.isAssignableFrom(field.getType())) {
			value = collectionType(field);
			if (StringUtils.isNotEmpty(value)) {
				return value;
			}
		}
		if (Map.class.isAssignableFrom(field.getType())) {
			value = mapType(field);
			if (StringUtils.isNotEmpty(value)) {
				return value;
			}
		}
		return value;
	}

	private static String singleType(Field field) {
		if (String.class.isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_STRING;
		}
		if (Integer.class.isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_INTEGER;
		}
		if (Long.class.isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_LONG;
		}
		if (Float.class.isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_FLOAT;
		}
		if (Double.class.isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_DOUBLE;
		}
		if (Boolean.class.isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_BOOLEAN;
		}
		if (JsonProperties.class.isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_JSONPROPERTIES;
		}
		if ((new byte[] {}).getClass().isAssignableFrom(field.getType())) {
			return JpaObject.TYPE_BYTEARRAY;
		}
		if (Date.class.isAssignableFrom(field.getType())) {
			Temporal temporal = field.getAnnotation(Temporal.class);
			if ((null != temporal) && (Objects.equals(temporal.value(), TemporalType.DATE))) {
				return JpaObject.TYPE_DATE;
			}
			return JpaObject.TYPE_DATETIME;
		}
		return null;
	}

	private static String collectionType(Field field) {
		ParameterizedType parameterized = (ParameterizedType) field.getGenericType();
		Class<?> actualClass = (Class<?>) parameterized.getActualTypeArguments()[0];
		if (String.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_STRINGLIST;
		}
		if (Integer.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_INTEGERLIST;
		}
		if (Long.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_LONGLIST;
		}
		if (Double.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_DOUBLELIST;
		}
		if (Float.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_FLOATLIST;
		}
		if (Date.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_DATETIMELIST;
		}
		if (Boolean.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_BOOLEANLIST;
		}
		return null;
	}

	private static String mapType(Field field) {
		ParameterizedType parameterized = (ParameterizedType) field.getGenericType();
		Class<?> actualClass = (Class<?>) parameterized.getActualTypeArguments()[1];
		if (String.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_STRINGMAP;
		}
		if (Integer.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_INTEGERMAP;
		}
		if (Long.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_LONGMAP;
		}
		if (Float.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_FLOATMAP;
		}
		if (Double.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_DOUBLEMAP;
		}
		if (Date.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_DATETIMEMAP;
		}
		if (Boolean.class.isAssignableFrom(actualClass)) {
			return JpaObject.TYPE_BOOLEANMAP;
		}
		return null;
	}

}