package com.x.portal.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.general.core.entity.PersistenceProperties;
import com.x.general.core.entity.wrap.WrapApplicationDict;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.CacheManager;
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

			Portal portal = this.cover(business, wi, effectivePerson);
			wo.setId(portal.getId());
			result.setData(wo);
			return result;
		}
	}

	private Portal cover(Business business, Wi wi, EffectivePerson effectivePerson) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		List<JpaObject> removeObjects = new ArrayList<>();
		Portal portal = business.entityManagerContainer().find(wi.getId(), Portal.class);
		if (null == portal) {
			portal = WrapPortal.inCopier.copy(wi);
			portal.setName(this.idlePortalName(business, portal.getName(), portal.getId()));
			portal.setAlias(this.idlePortalAlias(business, portal.getAlias(), portal.getId()));
			persistObjects.add(portal);
		}else{
			WrapPortal.inCopier.copy(wi, portal);
			portal.setName(this.idlePortalName(business, portal.getName(), portal.getId()));
			portal.setAlias(this.idlePortalAlias(business, portal.getAlias(), portal.getId()));
		}

		if (!business.editable(effectivePerson, portal)) {
			throw new ExceptionPortalAccessDenied(effectivePerson.getName(), portal.getName(), portal.getId());
		}

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
		for (WrapApplicationDict _o : wi.getApplicationDictList()) {
			ApplicationDict obj = business.entityManagerContainer().find(_o.getId(), ApplicationDict.class);
			if (null != obj) {
				for (ApplicationDictItem o : business.applicationDictItem()
						.listWithApplicationDictObject(obj.getId())) {
					removeObjects.add(o);
				}
				WrapApplicationDict.inCopier.copy(_o, obj);
			} else {
				obj = WrapApplicationDict.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(_o.getData());
			for (ApplicationDictItem o : list) {
				o.setBundle(obj.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(obj.getDistributeFactor());
				o.setApplication(obj.getApplication());
				persistObjects.add(o);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithPortal(business, portal.getId(), obj.getAlias(), ApplicationDict.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithPortal(business, portal.getId(), obj.getName(),
						ApplicationDict.class, obj.getId()));
			}
			obj.setApplication(portal.getId());
			obj.setProject(ApplicationDict.PROJECT_PORTAL);
		}
		business.entityManagerContainer().beginTransaction(Portal.class);
		business.entityManagerContainer().beginTransaction(Widget.class);
		business.entityManagerContainer().beginTransaction(Page.class);
		business.entityManagerContainer().beginTransaction(Script.class);
		business.entityManagerContainer().beginTransaction(File.class);
		business.entityManagerContainer().beginTransaction(ApplicationDict.class);
		business.entityManagerContainer().beginTransaction(ApplicationDictItem.class);
		for (JpaObject o : persistObjects) {
			business.entityManagerContainer().persist(o);
		}
		for (JpaObject o : removeObjects) {
			business.entityManagerContainer().remove(o);
		}
		business.entityManagerContainer().commit();
		CacheManager.notify(ApplicationDictItem.class);
		CacheManager.notify(ApplicationDict.class);
		CacheManager.notify(Script.class);
		CacheManager.notify(Page.class);
		CacheManager.notify(Widget.class);
		CacheManager.notify(Portal.class);

		return portal;
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
		if(cls.getSimpleName().equals(ApplicationDict.class.getSimpleName())){
			p = cb.and(p, cb.equal(root.get("application"), portalId));
		}else {
			p = cb.and(p, cb.equal(root.get("portal"), portalId));
		}
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
		if(cls.getSimpleName().equals(ApplicationDict.class.getSimpleName())){
			p = cb.and(p, cb.equal(root.get("application"), portalId));
		}else {
			p = cb.and(p, cb.equal(root.get("portal"), portalId));
		}
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
