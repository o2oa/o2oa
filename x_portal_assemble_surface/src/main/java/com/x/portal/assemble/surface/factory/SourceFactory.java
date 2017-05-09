package com.x.portal.assemble.surface.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.cache.ApplicationCache;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Source;
import com.x.portal.core.entity.Source_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class SourceFactory extends AbstractFactory {

	static Ehcache sourceCache = ApplicationCache.instance().getCache(Source.class);

	public SourceFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Source pick(String id) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(id);
		Element element = sourceCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (Source) element.getObjectValue();
		} else {
			Source o = this.business().entityManagerContainer().find(id, Source.class);
			if (null != o) {
				this.business().entityManagerContainer().get(Source.class).detach(o);
				sourceCache.put(new Element(id, o));
				return o;
			}
			return null;
		}
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Source.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Source> root = cq.from(Source.class);
		Predicate p = cb.equal(root.get(Source_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Source_.id)).where(p)).getResultList();
		return list;
	}
}