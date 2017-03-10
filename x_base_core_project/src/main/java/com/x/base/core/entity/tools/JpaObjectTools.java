package com.x.base.core.entity.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Path;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.junit.Test;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.utils.StringTools;

public class JpaObjectTools {
	public static boolean isList(Path<?> path) throws Exception {
		return List.class.isAssignableFrom(path.getJavaType());
	}

	public static <T extends JpaObject> boolean withinDefinedLength(String value, Class<T> clz, String attribute)
			throws Exception {
		Field field = FieldUtils.getField(clz, attribute, true);
		if (null == field) {
			throw new Exception("can not find field with Class:" + clz + ", attribute:" + attribute + ".");
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
			throw new Exception(
					"can not find @Column or @ElementColumn with Class:" + clz + ", attribute:" + attribute + ".");
		}
		return StringTools.utf8Length(value) < length;
	}

	public static Date confirm(Date date) throws Exception {
		return (date == null) ? null : new Date(date.getTime());
	}

}
