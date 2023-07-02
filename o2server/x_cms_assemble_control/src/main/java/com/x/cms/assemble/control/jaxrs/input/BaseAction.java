package com.x.cms.assemble.control.jaxrs.input;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfo_;

abstract class BaseAction extends StandardJaxrsAction {

	public enum Method {
		cover, create, ignore;
	}

	protected AppInfo getAppInfo(Business business, String id, String name, String alias) throws Exception {
		AppInfo o = business.entityManagerContainer().find(id, AppInfo.class);
		if (null == o) {
			o = this.getAppInfoWithName(business, name);
		}
		if (null == o) {
			o = this.getAppInfoWithAlias(business, alias);
		}
		return o;
	}

	private AppInfo getAppInfoWithAlias(Business business, String alias) throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return null;
		}
		EntityManager em = business.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppInfo> cq = cb.createQuery(AppInfo.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.equal( root.get(AppInfo_.appAlias ), alias);
		List<AppInfo> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
		if (os.size() == 1) {
			return os.get(0);
		} else {
			return null;
		}
	}

	private AppInfo getAppInfoWithName(Business business, String name) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		EntityManager em = business.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppInfo> cq = cb.createQuery(AppInfo.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.equal(root.get(AppInfo_.appName), name);
		List<AppInfo> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
		if (os.size() == 1) {
			return os.get(0);
		} else {
			return null;
		}
	}

	protected <T extends JpaObject> String idleAppInfoName(Business business, String name, String excludeId)
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
		EntityManager em = business.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = root.get(AppInfo_.appName).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(AppInfo_.appName)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	protected <T extends JpaObject> String idleAppInfoAlias(Business business, String name, String excludeId)
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
		EntityManager em = business.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = root.get(AppInfo_.appAlias).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(AppInfo_.appAlias)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

}