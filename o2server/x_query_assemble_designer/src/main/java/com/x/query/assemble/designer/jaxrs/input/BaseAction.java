package com.x.query.assemble.designer.jaxrs.input;

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
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Query_;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache inputCache = ApplicationCache.instance().getCache(BaseAction.class.getName(), 100,
			ApplicationCache.MINUTES_20, ApplicationCache.MINUTES_20);

	public enum Method {
		cover, create, ignore;
	}

	protected Query getQuery(Business business, String id, String name, String alias) throws Exception {
		Query o = null;
		if (StringUtils.isNotEmpty(id)) {
			o = business.entityManagerContainer().flag(id, Query.class );
		}
		if (null == o && StringUtils.isNotEmpty(name)) {
			o = business.entityManagerContainer().flag(name, Query.class );
		}
		if (null == o && StringUtils.isNotEmpty(alias)) {
			o = business.entityManagerContainer().flag(alias, Query.class );
		}
		return o;
	}

	protected <T extends JpaObject> String idleQueryName(Business business, String name, String excludeId)
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
		EntityManager em = business.entityManagerContainer().get(Query.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Query> root = cq.from(Query.class);
		Predicate p = root.get(Query_.name).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(Query_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	protected <T extends JpaObject> String idleQueryAlias(Business business, String alias, String excludeId)
			throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(alias);
		for (int i = 1; i < 99; i++) {
			list.add(alias + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(Query.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Query> root = cq.from(Query.class);
		Predicate p = root.get(Query_.alias).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(Query_.alias)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

}