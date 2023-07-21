package com.x.base.core.project.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public class PropertyObjectFactory {

	private PropertyObjectFactory() {
		// nothing
	}

	private static final Map<Key, PropertyObjectDescriptor> MAPPEDPROPERTYOBJECTDESCRIPTOR = new ConcurrentHashMap<>();

	public static PropertyObjectDescriptor descriptor(Object orig, Object dest, Collection<String> excludes) {
		return MAPPEDPROPERTYOBJECTDESCRIPTOR.compute(new Key(orig.getClass(), dest.getClass(), excludes),
				(k, v) -> (null == v) ? create(orig, dest, excludes) : v);
	}

	private static PropertyObjectDescriptor create(Object orig, Object dest, Collection<String> excludes) {
		List<String> origFields = new ArrayList<>();
		List<String> destFields = new ArrayList<>();
		PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
		FieldUtils.getAllFieldsList(orig.getClass()).stream().forEach(o -> origFields.add(o.getName()));
		FieldUtils.getAllFieldsList(dest.getClass()).stream().forEach(o -> destFields.add(o.getName()));
		List<String> intersectionNames = ListUtils.intersection(origFields, destFields);
		Iterator<String> iter = intersectionNames.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			if ((!propertyUtilsBean.isReadable(orig, name)) || (!propertyUtilsBean.isWriteable(dest, name))) {
				iter.remove();
			}
		}
		return new PropertyObjectDescriptor(ListUtils.subtract(intersectionNames, new ArrayList<>(excludes)),
				propertyUtilsBean);
	}

	private static class Key {

		private Class<?> origClass;
		private Class<?> destClass;
		private String joinExclude;

		private Key(Class<?> origClass, Class<?> destClass, Collection<String> excludes) {
			this.origClass = origClass;
			this.destClass = destClass;
			if (excludes == null || excludes.isEmpty()) {
				this.joinExclude = "";
			} else {
				this.joinExclude = excludes.stream().sorted().collect(Collectors.joining(","));
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((origClass == null) ? 0 : origClass.hashCode());
			result = prime * result + ((destClass == null) ? 0 : destClass.hashCode());
			result = prime * result + ((joinExclude == null) ? 0 : joinExclude.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (!Objects.equals(this.origClass, other.origClass)) {
				return false;
			}
			if (!Objects.equals(this.destClass, other.destClass)) {
				return false;
			}
			return StringUtils.equals(this.joinExclude, other.joinExclude);
		}
	}
}