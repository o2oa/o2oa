package com.x.portal.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.ApplicationCache;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.File;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;
import com.x.portal.core.entity.wrap.WrapFile;
import com.x.portal.core.entity.wrap.WrapPage;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.portal.core.entity.wrap.WrapScript;
import com.x.portal.core.entity.wrap.WrapWidget;

class ActionCover extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCover.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		// logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Portal portal = business.entityManagerContainer().find(wi.getId(), Portal.class);
			if (null == portal) {
				throw new ExceptionPortalNotExist(wi.getId());
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getName(), portal.getName(), portal.getId());
			}

			this.cover(business, wi, portal);
			wo.setId(portal.getId());
			result.setData(wo);
			return result;
		}
	}

	private void cover(Business business, Wi wi, Portal portal) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();

		for (WrapWidget _o : wi.getWidgetList()) {
			Widget obj = business.entityManagerContainer().find(_o.getId(), Widget.class);
			if (null != obj) {
				WrapWidget.inCopier.copy(_o, obj);
			} else {
				obj = WrapWidget.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(
						this.idleAliasWithPortal(business, portal.getId(), obj.getAlias(), Widget.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(
						this.idleNameWithPortal(business, portal.getId(), obj.getName(), Widget.class, obj.getId()));
			}
			obj.setPortal(portal.getId());
		}
		for (WrapPage _o : wi.getPageList()) {
			Page obj = business.entityManagerContainer().find(_o.getId(), Page.class);
			if (null != obj) {
				WrapPage.inCopier.copy(_o, obj);
			} else {
				obj = WrapPage.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(
						this.idleAliasWithPortal(business, portal.getId(), obj.getAlias(), Page.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithPortal(business, portal.getId(), obj.getName(), Page.class, obj.getId()));
			}
			obj.setPortal(portal.getId());
		}
		for (WrapScript _o : wi.getScriptList()) {
			Script obj = business.entityManagerContainer().find(_o.getId(), Script.class);
			if (null != obj) {
				WrapScript.inCopier.copy(_o, obj);
			} else {
				obj = WrapScript.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(
						this.idleAliasWithPortal(business, portal.getId(), obj.getAlias(), Script.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(
						this.idleNameWithPortal(business, portal.getId(), obj.getName(), Script.class, obj.getId()));
			}
			obj.setPortal(portal.getId());
		}
		for (WrapFile _o : wi.getFileList()) {
			File obj = business.entityManagerContainer().find(_o.getId(), File.class);
			if (null != obj) {
				WrapFile.inCopier.copy(_o, obj);
			} else {
				obj = WrapFile.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(
						this.idleAliasWithPortal(business, portal.getId(), obj.getAlias(), File.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithPortal(business, portal.getId(), obj.getName(), File.class, obj.getId()));
			}
			obj.setPortal(portal.getId());
		}
		business.entityManagerContainer().beginTransaction(Portal.class);
		business.entityManagerContainer().beginTransaction(Widget.class);
		business.entityManagerContainer().beginTransaction(Page.class);
		business.entityManagerContainer().beginTransaction(Script.class);
		business.entityManagerContainer().beginTransaction(File.class);
		for (JpaObject o : persistObjects) {
			business.entityManagerContainer().persist(o);
		}
		business.entityManagerContainer().commit();
		ApplicationCache.notify(Script.class);
		ApplicationCache.notify(Page.class);
		ApplicationCache.notify(Widget.class);
		ApplicationCache.notify(Portal.class);
	}

	private <T extends JpaObject> String idleNameWithPortal(Business business, String portalId, String name,
			Class<T> cls, String excludeId) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(name);
		for (int i = 1; i < 99; i++) {
			list.add(name + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("name").in(list);
		p = cb.and(p, cb.equal(root.get("portal"), portalId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("name")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	private <T extends JpaObject> String idleAliasWithPortal(Business business, String portalId, String alias,
			Class<T> cls, String excludeId) throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(alias);
		for (int i = 1; i < 99; i++) {
			list.add(alias + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("alias").in(list);
		p = cb.and(p, cb.equal(root.get("portal"), portalId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("alias")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	public static class Wi extends WrapPortal {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WoId {

	}

}