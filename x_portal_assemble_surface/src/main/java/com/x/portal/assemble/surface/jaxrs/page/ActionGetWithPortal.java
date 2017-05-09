package com.x.portal.assemble.surface.jaxrs.page;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutPage;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Page_;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Element;

class ActionGetWithPortal extends ActionBase {

	ActionResult<WrapOutPage> execute(EffectivePerson effectivePerson, String flag, String portalFlag)
			throws Exception {
		ActionResult<WrapOutPage> result = new ActionResult<>();
		WrapOutPage wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(flag, portalFlag);
			Element element = pageCache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wrap = (WrapOutPage) element.getObjectValue();
				Portal portal = business.portal().pick(wrap.getPortal());
				if (!business.portal().visible(effectivePerson, portal)) {
					throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
				}
			} else {
				Portal portal = emc.flag(portalFlag, Portal.class, ExceptionWhen.none, false, Portal.FLAGS);
				if (null == portal) {
					throw new PortalNotExistedException(portalFlag);
				}
				if (!business.portal().visible(effectivePerson, portal)) {
					throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
				}
				String id = this.getPage(business, flag, portal);
				if (StringUtils.isEmpty(id)) {
					throw new PageNotExistedException(id);
				}
				Page page = emc.find(id, Page.class);
				wrap = outCopier.copy(page);
				pageCache.put(new Element(cacheKey, wrap));
			}
			result.setData(wrap);
			return result;
		}
	}

	private String getPage(Business business, String flag, Portal portal) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.equal(root.get(Page_.portal), portal.getId());
		p = cb.and(p, cb.or(cb.equal(root.get(Page_.name), flag), cb.equal(root.get(Page_.alias), portal.getId()),
				cb.equal(root.get(Page_.id), portal.getId())));
		List<String> list = em.createQuery(cq.select(root.get(Page_.id)).where(p).distinct(true)).setMaxResults(1)
				.getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
}