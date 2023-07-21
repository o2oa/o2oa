package com.x.cms.assemble.control.factory.element;

import java.util.Optional;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;

public abstract class ElementFactory extends AbstractFactory {

	public ElementFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(String flag, Class<T> clz) throws Exception {
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(clz);
		T t = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), flag );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
		if (optional.isPresent()) {
			if (null != optional.get()) {
				t = (T) optional.get();
			}
		} else {
			t = this.entityManagerContainer().flag(flag, clz);
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
				CacheManager.put(cacheCategory, cacheKey, t );
			}
		}
		return t;
	}

}
