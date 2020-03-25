package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Process;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class BeginFactory extends ElementFactory {

	public BeginFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Begin pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Begin pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Begin.class);
	}

	public Begin getWithProcess(Process process) throws Exception {
		Begin o = null;
		Ehcache cache = ApplicationCache.instance().getCache(Begin.class);
		String cacheKey = "getWithProcess#" + process.getId();
		Element element = cache.get(cacheKey);
		if (null != element) {
			Object obj = element.getObjectValue();
			if (null != obj) {
				o = (Begin) obj;
			}
		} else {
			EntityManager em = this.entityManagerContainer().get(Begin.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Begin> cq = cb.createQuery(Begin.class);
			Root<Begin> root = cq.from(Begin.class);
			Predicate p = cb.equal(root.get("process"), process.getId());
			cq.select(root).where(p);
			List<Begin> list = em.createQuery(cq).setMaxResults(1).getResultList();
			if (!list.isEmpty()) {
				o = list.get(0);
			}
			cache.put(new Element(cacheKey, o));
		}
		return o;
	}
}