package com.x.portal.assemble.designer.jaxrs.output;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.wrap.WrapPortal;

abstract class BaseAction extends StandardJaxrsAction {

	protected CacheCategory cache = new CacheCategory(CacheObject.class);

	protected <T extends JpaObject> List<String> listWithPortal(Business business, Portal portal, Class<T> cls)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get("portal"), portal.getId());
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class CacheObject extends GsonPropertyObject {

		private String name;
		private WrapPortal portal;

		public WrapPortal getPortal() {
			return portal;
		}

		public void setPortal(WrapPortal portal) {
			this.portal = portal;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
