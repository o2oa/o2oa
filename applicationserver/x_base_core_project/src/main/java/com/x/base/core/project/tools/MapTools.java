package com.x.base.core.project.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

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

	@Test
	public void test() throws Exception {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		Map<String, Object> map2 = new HashMap<>();
		map.put("obj1", map1);
		map1.put("obj2", map2);
		map2.put("city", "hagnzhou");
		List<String> address = new ArrayList<>();
		address.add("aaaaaaaaaa");
		address.add("aaaaaaaaaa1");
		address.add("aaaaaaaaaa2");
		map2.put("address", address);
		System.out.println(MapTools.extractStringList(map, "obj1.obj2.city", false, true));
		System.out.println(MapTools.extractStringList(map, "obj1.obj2.address", false, false));
		System.out.println(MapTools.extractStringList(map, "obj1.address", false, false));

	}

}