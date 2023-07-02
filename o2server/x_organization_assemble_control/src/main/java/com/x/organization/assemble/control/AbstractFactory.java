package com.x.organization.assemble.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;

public abstract class AbstractFactory {

	protected Business business;

	protected CacheCategory cache;

	public AbstractFactory(Business business) throws Exception {
		try {
			if (null == business) {
				throw new Exception("business can not be null.");
			}
			this.business = business;
		} catch (Exception e) {
			throw new Exception("can not instantiating factory.");
		}
	}

	public EntityManagerContainer entityManagerContainer() throws Exception {
		return this.business.entityManagerContainer();
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(String flag, Class<T> clz, String... attributes) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		CacheCategory cacheCategory = new CacheCategory(clz);
		T t = null;
		CacheKey cacheKey = new CacheKey(flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			t = (T) optional.get();
		} else {
			t = this.entityManagerContainer().flag(flag, clz);
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
			}
			CacheManager.put(cacheCategory, cacheKey, t);
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
		boolean hasCache = true;
		for(String flag : flags){
			CacheKey cacheKey = new CacheKey(flag);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				list.add ((T) optional.get());
			}else{
				hasCache = false;
				break;
			}
		}
		if(hasCache){
			return list;
		} else {
			List<T> os = this.entityManagerContainer().flag(flags, clz);
			EntityManager em = this.entityManagerContainer().get(clz);
			os.stream().forEach(o -> {
				em.detach(o);
				list.add(o);
				CacheKey cacheKey = new CacheKey(o.getId());
				CacheManager.put(cacheCategory, cacheKey, o);
			});
		}
		return list;
	}

}