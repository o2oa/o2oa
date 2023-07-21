package com.x.base.core.project.tools;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

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

	public static String getString(Map<?, ?> map, Object key) {
		return getString(map, key, null);
	}

	public static String getString(Map<?, ?> map, Object key, String defaultValue) {
		String value = null;
		if (null != map) {
			Object o = map.get(key);
			if (!Objects.isNull(o)) {
				value = o.toString();
			} else {
				value = defaultValue;
			}
		}
		return value;
	}

	public static Double getDouble(Map<?, ?> map, Object key) {
		return getDouble(map, key, null);
	}

	public static Double getDouble(Map<?, ?> map, Object key, Double defaultValue) {
		Double value = defaultValue;
		if (null != map) {
			Object o = map.get(key);
			if (!Objects.isNull(o)) {
				if (o instanceof Number) {
					Number n = (Number) o;
					value = n.doubleValue();
				} else if (o instanceof String) {
					String t = (String) o;
					if (NumberUtils.isParsable(t)) {
						try {
							Number n = NumberFormat.getInstance().parse(t);
							value = n.doubleValue();
						} catch (ParseException e) {
						}
					}
				}
			}
		}
		return value;
	}

	public static Float getFloat(Map<?, ?> map, Object key) {
		return getFloat(map, key, null);
	}

	public static Float getFloat(Map<?, ?> map, Object key, Float defaultValue) {
		Float value = defaultValue;
		if (null != map) {
			Object o = map.get(key);
			if (!Objects.isNull(o)) {
				if (o instanceof Number) {
					Number n = (Number) o;
					value = n.floatValue();
				} else if (o instanceof String) {
					String t = (String) o;
					if (NumberUtils.isParsable(t)) {
						try {
							Number n = NumberFormat.getInstance().parse(t);
							value = n.floatValue();
						} catch (ParseException e) {
						}
					}
				}
			}
		}
		return value;
	}

	public static Integer getInteger(Map<?, ?> map, Object key) {
		return getInteger(map, key, null);
	}

	public static Integer getInteger(Map<?, ?> map, Object key, Integer defaultValue) {
		Integer value = defaultValue;
		if (null != map) {
			Object o = map.get(key);
			if (!Objects.isNull(o)) {
				if (o instanceof Number) {
					Number n = (Number) o;
					value = n.intValue();
				} else if (o instanceof String) {
					String t = (String) o;
					if (NumberUtils.isParsable(t)) {
						try {
							Number n = NumberFormat.getInstance().parse(t);
							value = n.intValue();
						} catch (ParseException e) {
						}
					}
				}
			}
		}
		return value;
	}

	public static Long getLong(Map<?, ?> map, Object key) {
		return getLong(map, key, null);
	}

	public static Long getLong(Map<?, ?> map, Object key, Long defaultValue) {
		Long value = defaultValue;
		if (null != map) {
			Object o = map.get(key);
			if (!Objects.isNull(o)) {
				if (o instanceof Number) {
					Number n = (Number) o;
					value = n.longValue();
				} else if (o instanceof String) {
					String t = (String) o;
					if (NumberUtils.isParsable(t)) {
						try {
							Number n = NumberFormat.getInstance().parse(t);
							value = n.longValue();
						} catch (ParseException e) {
						}
					}
				}
			}
		}
		return value;
	}

	public static Boolean getBoolean(Map<?, ?> map, Object key) {
		return getBoolean(map, key, null);
	}

	public static Boolean getBoolean(Map<?, ?> map, Object key, Boolean defaultValue) {
		Boolean value = defaultValue;
		if (null != map) {
			Object o = map.get(key);
			if (!Objects.isNull(o)) {
				if (o instanceof Boolean) {
					value = (Boolean) o;
				} else if (o instanceof String) {
					String t = (String) o;
					value = BooleanUtils.toBooleanObject(t);
					if (null == value) {
						value = defaultValue;
					}
				}
			}
		}
		return value;
	}

	/**
	 * 将Map转换为String
	 * @param map
	 * @return
	 */
	public static String mapToString(Map<String, Object> map) {
		if (null == map || map.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			sb.append(entry.getValue());
		}

		return sb.toString();
	}

}
