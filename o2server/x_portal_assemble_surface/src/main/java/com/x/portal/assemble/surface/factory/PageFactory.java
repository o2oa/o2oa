package com.x.portal.assemble.surface.factory;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Page_;
import com.x.portal.core.entity.Portal;

public class PageFactory extends AbstractFactory {

	static CacheCategory cache = new CacheCategory(Page.class);

	public PageFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.equal(root.get(Page_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Page_.id)).where(p)).getResultList();
		return list;
	}

	public Page pick(String id) throws Exception {
		CacheKey cacheKey = new CacheKey(id);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			return (Page) optional.get();
		} else {
			Page o = this.business().entityManagerContainer().find(id, Page.class);
			if (null != o) {
				this.business().entityManagerContainer().get(Page.class).detach(o);
				CacheManager.put(cache, cacheKey, o);
				return o;
			}
			return null;
		}
	}

	public Page pick(Portal portal, String flag) throws Exception {
		CacheKey cacheKey = new CacheKey(portal.getId(), flag);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			return (Page) optional.get();
		} else {
			Page o = entityManagerContainer().restrictFlag(flag, Page.class, Page.portal_FIELDNAME, portal.getId());
			if (null != o) {
				this.business().entityManagerContainer().get(Page.class).detach(o);
				CacheManager.put(cache, cacheKey, o);
				return o;
			}
			return null;
		}
	}
}