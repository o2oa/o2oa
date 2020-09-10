package com.x.processplatform.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;

abstract class BaseAction extends StandardJaxrsAction {

//	protected Ehcache inputCache = ApplicationCache.instance().getCache(BaseAction.class.getName(), 100,
//			ApplicationCache.MINUTES_20, ApplicationCache.MINUTES_20);

	protected CacheCategory cacheCategory = new CacheCategory(BaseAction.class);

	public enum Method {
		cover, create, ignore;
	}

	protected Application getApplication(Business business, String id, String name, String alias) throws Exception {
		Application o = business.entityManagerContainer().find(id, Application.class);
		if (null == o) {
			o = this.getApplicationWithName(business, name);
		}
		if (null == o) {
			o = this.getApplicationWithAlias(business, alias);
		}
		return o;
	}

	private Application getApplicationWithAlias(Business business, String alias) throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return null;
		}
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.equal(root.get(Application_.alias), alias);
		List<Application> os = em.createQuery(cq.select(root).where(p)).getResultList();
		if (os.size() == 1) {
			return os.get(0);
		} else {
			return null;
		}
	}

	private Application getApplicationWithName(Business business, String name) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.equal(root.get(Application_.name), name);
		List<Application> os = em.createQuery(cq.select(root).where(p)).getResultList();
		if (os.size() == 1) {
			return os.get(0);
		} else {
			return null;
		}
	}

	protected <T extends JpaObject> String idleApplicationName(Business business, String name, String excludeId)
			throws Exception {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(name);
		for (int i = 1; i < 99; i++) {
			list.add(name + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = root.get(Application_.name).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(Application_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	protected <T extends JpaObject> String idleApplicationAlias(Business business, String name, String excludeId)
			throws Exception {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(name);
		for (int i = 1; i < 99; i++) {
			list.add(name + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = root.get(Application_.alias).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(Application_.alias)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

}