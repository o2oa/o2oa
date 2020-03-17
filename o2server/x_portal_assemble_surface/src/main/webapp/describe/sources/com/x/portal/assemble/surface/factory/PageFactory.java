package com.x.portal.assemble.surface.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Page_;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class PageFactory extends AbstractFactory {

	static Ehcache pageCache = ApplicationCache.instance().getCache(Page.class);

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
		String cacheKey = ApplicationCache.concreteCacheKey(id);
		Element element = pageCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (Page) element.getObjectValue();
		} else {
			Page o = this.business().entityManagerContainer().find(id, Page.class);
			if (null != o) {
				this.business().entityManagerContainer().get(Page.class).detach(o);
				pageCache.put(new Element(id, o));
				return o;
			}
			return null;
		}
	}

	public Page pick(Portal portal, String flag) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(portal.getId(), flag);
		Element element = pageCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (Page) element.getObjectValue();
		} else {
			Page o = entityManagerContainer().restrictFlag(flag, Page.class, Page.portal_FIELDNAME, portal.getId());
			if (null != o) {
				this.business().entityManagerContainer().get(Page.class).detach(o);
				pageCache.put(new Element(cacheKey, o));
				return o;
			}
			return null;
		}
	}
}