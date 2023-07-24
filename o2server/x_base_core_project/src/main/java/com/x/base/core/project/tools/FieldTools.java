package com.x.base.core.project.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;

public class FieldTools {
	@SuppressWarnings("unchecked")
	public static <T> List<Field> fieldsWithAnnotation(Class<T> cls, Class<? extends Annotation>... annotationClasses) {
		Set<Field> set = new HashSet<>();
		for (Class<? extends Annotation> an : annotationClasses) {
			set.addAll(FieldUtils.getFieldsListWithAnnotation(cls, an));
		}
		return new ArrayList<Field>(set);
	}
}
