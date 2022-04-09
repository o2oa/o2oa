package com.x.message.assemble.communicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;

import net.sf.ehcache.Ehcache;

public abstract class AbstractFactory {

	protected Business business;

	protected Ehcache cache;

	protected AbstractFactory(Business business) {
		if (null == business) {
			throw new IllegalArgumentException("business can not be null.");
		}
		this.business = business;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.business.entityManagerContainer();
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(String flag, Class<T> clz) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		CacheCategory cacheCategory = new CacheCategory(clz);
		CacheKey cacheKey = new CacheKey(flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		T t = null;
		if (optional.isPresent()) {
			t = (T) optional.get();
		} else {
			t = this.entityManagerContainer().flag(flag, clz);
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
				CacheManager.put(cacheCategory, cacheKey, t);
			}
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> List<T> pick(List<String> flags, Class<T> clz) throws Exception {
		List<T> list = new ArrayList<>();
		if (null == flags || flags.isEmpty()) {
			return list;
		}
		CacheCategory cacheCategory = new CacheCategory(clz);
		for (String flag : flags) {
			CacheKey cacheKey = new CacheKey(flag);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				list.add((T) optional.get());
			} else {
				T t = this.entityManagerContainer().flag(flag, clz);
				if (null != t) {
					this.entityManagerContainer().get(clz).detach(t);
					list.add(t);
					CacheManager.put(cacheCategory, cacheKey, t);
				}
			}
		}
		return list;
	}
}
