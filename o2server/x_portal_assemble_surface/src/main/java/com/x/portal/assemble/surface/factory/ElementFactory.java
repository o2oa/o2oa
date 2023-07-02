package com.x.portal.assemble.surface.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Ehcache;

public abstract class ElementFactory extends AbstractFactory {

	protected Ehcache cache;

	public ElementFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(String flag, Class<T> clz) throws Exception {
		CacheCategory cacheCategory = new CacheCategory(clz);
		CacheKey cacheKey = new CacheKey(flag);
		T t = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
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

	protected <T extends JpaObject> List<T> pick(Collection<String> flags, Class<T> clz) throws Exception {
		List<T> list = new ArrayList<>();
		for (String str : flags) {
			T t = pick(str, clz);
			if (null != t) {
				list.add(t);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(Portal portal, String flag, Class<T> clz) throws Exception {
		if (null == portal) {
			return null;
		}
		CacheCategory cacheCategory = new CacheCategory(clz);
		CacheKey cacheKey = new CacheKey(portal.getId(), flag);
		T t = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			t = (T) optional.get();
		} else {
			t = this.entityManagerContainer().restrictFlag(flag, clz, Page.portal_FIELDNAME,
					portal.getId());
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
				CacheManager.put(cacheCategory, cacheKey, t);
			}
		}
		return t;
	}

}