package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

public abstract class ElementFactory extends AbstractFactory {

	protected Cache.CacheCategory cacheCategory;

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

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick( AppInfo appInfo, String flag, Class<T> clz) throws Exception {
		if (null == appInfo) {
			return null;
		}
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(clz);
		T t = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(),appInfo.getId(), flag );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
		if (optional.isPresent()) {
			if (null != optional.get()) {
				t = (T) optional.get();
			}
		} else {
			t = this.entityManagerContainer().restrictFlag(flag, clz, CategoryInfo.appId_FIELDNAME,
					appInfo.getId());
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
				CacheManager.put(cacheCategory, cacheKey, t );
			}
		}
		return t;
	}

	/* 取得属于指定CategoryInfo 的设计元素 */
	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> List<T> listWithCategory(Class<T> clz, CategoryInfo categoryInfo) throws Exception {
		List<T> list = new ArrayList<>();
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(clz);
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(),categoryInfo.getId(),clz.getName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
		if (optional.isPresent()) {
			Object obj = optional.get();
			if (null != obj) {
				list = (List<T>) obj;
			}
		} else {
			EntityManager em = this.entityManagerContainer().get(clz);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(clz);
			Root<T> root = cq.from(clz);
			Predicate p = cb.equal(root.get("categoryInfo"), categoryInfo.getId());
			cq.select(root).where(p);
			List<T> os = em.createQuery(cq).getResultList();
			for (T t : os) {
				em.detach(t);
				list.add(t);
			}
			/* 将object改为unmodifiable */
			list = Collections.unmodifiableList(list);
			CacheManager.put(cacheCategory, cacheKey, list );
		}
		return list;
	}

}
