package com.x.base.core.project.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import com.x.base.core.project.jaxrs.WiDesigner;
import org.apache.commons.lang3.StringUtils;
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

	public static <T> Map<String, String> fieldMatchKeyword(final Class<T> origClass, T t, String keyword, Boolean caseSensitive, Boolean matchWholeWord, Boolean matchRegExp) throws Exception{
		Map<String, String> map = new HashMap<>();
		if(origClass!=null && StringUtils.isNotBlank(keyword)) {
			for (Field field : FieldUtils.getAllFields(origClass)) {
				if (field.getType() == String.class) {
					String content = (String) field.get(t);
					if (StringTools.matchKeyword(keyword, content, caseSensitive, matchWholeWord, matchRegExp)) {
						map.put(field.getName(), content);
					}
				}
			}
		}
		return map;
	}
}
