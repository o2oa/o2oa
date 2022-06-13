package com.x.base.core.project.cache;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

public interface Cache {

	// public static final String TYPE_EHCACHE = "ehcache";
	public static final String TYPE_REDIS = "redis";
	public static final String TYPE_GUAVA = "guava";

	public abstract void put(CacheCategory category, CacheKey key, Object o);

	public abstract Optional<Object> get(CacheCategory category, CacheKey key);

	public abstract void shutdown();

	public abstract void receive(WrapClearCacheRequest wi);

	public abstract void notify(Class<?> clz, List<Object> keys);

	public abstract String detail();

	public static class CacheCategory {

		public static final String SPLIT = "#";

		private String value = "";

		public CacheCategory(Object... parts) {
			if ((null != parts) && (parts.length > 0)) {
				value = Stream.of(parts).map(Cache::stringify).filter(StringUtils::isNotEmpty)
						.collect(Collectors.joining(SPLIT));
				if (StringUtils.isNotEmpty(value)) {
					value = value + SPLIT;
				}
			}
		}

		public String toString() {
			return value;
		}
	}

	public static class CacheKey {

		public static final String SPLIT = "#";

		private String value = "";

		public CacheKey(Object... parts) {
			if ((null != parts) && (parts.length > 0)) {
				value = Stream.of(parts).map(Cache::stringify).filter(StringUtils::isNotEmpty)
						.collect(Collectors.joining(SPLIT));
				if (StringUtils.isNotEmpty(value)) {
					value = value + SPLIT;
				}
			}
		}

		public String toString() {
			return value;
		}
	}

	static String stringify(Object o) {
		if (null == o) {
			return "";
		}
		if (o instanceof Class<?>) {
			return ((Class<?>) o).getName();
		} else if (o instanceof Collection<?>) {
			return StringUtils.join((Collection<?>) o, ",");
		} else {
			return Objects.toString(o);
		}
	}

}
