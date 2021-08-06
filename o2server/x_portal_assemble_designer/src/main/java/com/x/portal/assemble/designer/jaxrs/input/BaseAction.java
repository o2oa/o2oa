package com.x.portal.assemble.designer.jaxrs.input;

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
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.StringTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

abstract class BaseAction extends StandardJaxrsAction {

	public enum Method {
		cover, create, ignore;
	}

	protected Portal getPortal(Business business, String id, String name, String alias) throws Exception {
		Portal o = business.entityManagerContainer().find(id, Portal.class);
		if (null == o) {
			o = this.getPortalWithName(business, name);
		}
		if (null == o) {
			o = this.getPortalWithAlias(business, alias);
		}
		return o;
	}

	private Portal getPortalWithAlias(Business business, String alias) throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return null;
		}
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Portal> cq = cb.createQuery(Portal.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.equal(root.get(Portal_.alias), alias);
		List<Portal> os = em.createQuery(cq.select(root).where(p)).getResultList();
		if (os.size() == 1) {
			return os.get(0);
		} else {
			return null;
		}
	}

	private Portal getPortalWithName(Business business, String name) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Portal> cq = cb.createQuery(Portal.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.equal(root.get(Portal_.name), name);
		List<Portal> os = em.createQuery(cq.select(root).where(p)).getResultList();
		if (os.size() == 1) {
			return os.get(0);
		} else {
			return null;
		}
	}

	public static abstract class CompareWoPortal extends GsonPropertyObject {

		@FieldDescribe("标记")
		private String flag;

		@FieldDescribe("导入名称")
		private String name;

		@FieldDescribe("导入id")
		private String id;

		@FieldDescribe("导入别名")
		private String alias;

		@FieldDescribe("是否已经存在")
		private Boolean exist;

		@FieldDescribe("已经存在名称")
		private String existName;

		@FieldDescribe("已经存在id")
		private String existId;

		@FieldDescribe("已经存在别名")
		private String existAlias;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getExistName() {
			return existName;
		}

		public void setExistName(String existName) {
			this.existName = existName;
		}

		public String getExistId() {
			return existId;
		}

		public void setExistId(String existId) {
			this.existId = existId;
		}

		public String getExistAlias() {
			return existAlias;
		}

		public void setExistAlias(String existAlias) {
			this.existAlias = existAlias;
		}

		public Boolean getExist() {
			return exist;
		}

		public void setExist(Boolean exist) {
			this.exist = exist;
		}
	}

	protected <T extends JpaObject> String idlePortalName(Business business, String name, String excludeId)
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
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = root.get(Portal_.name).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(Portal_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	protected <T extends JpaObject> String idlePortalAlias(Business business, String alias, String excludeId)
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
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = root.get(Portal_.alias).in(list);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(Portal_.alias)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

}