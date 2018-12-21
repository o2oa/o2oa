package com.x.base.core.project.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public class MapTools {

	public static <T, W> List<String> extractStringList(Map<T, W> map, String path, Boolean ignoreNull, Boolean unique)
			throws Exception {
		List<String> list = new ArrayList<>();
		Object o = PropertyUtils.getProperty(map, path);
		if (null != o) {
			if (o instanceof CharSequence) {
				list.add(o.toString());
			} else if (o instanceof Iterable) {
				for (Object v : (Iterable<?>) o) {
					if ((null != v) && (v instanceof CharSequence)) {
						list.add(v.toString());
					}
				}
			}
		}
		if (ignoreNull || unique) {
			list = ListTools.trim(list, ignoreNull, unique);
		}
		return list;
	}

	 

}