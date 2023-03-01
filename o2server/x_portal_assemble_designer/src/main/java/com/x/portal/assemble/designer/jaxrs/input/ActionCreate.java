package com.x.portal.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.general.core.entity.wrap.WrapApplicationDict;
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

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (!business.editable(effectivePerson, null)) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getName(), wi.getName(), wi.getId());
			}
			Portal portal = this.create(business, wi);
			wo.setId(portal.getId());
			result.setData(wo);
			return result;
		}
	}

	private Portal create(Business business, Wi wi) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		Portal portal = business.entityManagerContainer().find(wi.getId(), Portal.class);
		if (null != portal) {
			throw new ExceptionPortalExistForCreate(wi.getId());
		}
		portal = WrapPortal.inCopier.copy(wi);
		portal.setName(this.idlePortalName(business, portal.getName(), portal.getId()));
		portal.setAlias(this.idlePortalAlias(business, portal.getAlias(), portal.getId()));
		persistObjects.add(portal);
		for (WrapWidget _o : wi.getWidgetList()) {
			Widget obj = business.entityManagerContainer().find(_o.getId(), Widget.class);
			if (null != obj) {
				throw new ExceptionMenuExistForCreate(_o.getId());
			}
			obj = WrapWidget.inCopier.copy(_o);
			obj.setPortal(portal.getId());
			persistObjects.add(obj);
		}
		for (WrapPage _o : wi.getPageList()) {
			Page obj = business.entityManagerContainer().find(_o.getId(), Page.class);
			if (null != obj) {
				throw new ExceptionPageExistForCreate(_o.getId());
			}
			obj = WrapPage.inCopier.copy(_o);
			obj.setPortal(portal.getId());
			persistObjects.add(obj);
		}
		for (WrapScript _o : wi.getScriptList()) {
			Script obj = business.entityManagerContainer().find(_o.getId(), Script.class);
			if (null != obj) {
				throw new ExceptionScriptExistForCreate(_o.getId());
			}
			obj = WrapScript.inCopier.copy(_o);
			obj.setPortal(portal.getId());
			persistObjects.add(obj);
		}
		for (WrapFile _o : wi.getFileList()) {
			File obj = business.entityManagerContainer().find(_o.getId(),File.class);
			if (null != obj) {
				throw new ExceptionFileExistForCreate(_o.getId());
			}
			obj = WrapFile.inCopier.copy(_o);
			obj.setPortal(portal.getId());
			persistObjects.add(obj);
		}
		for (WrapApplicationDict _o : wi.getApplicationDictList()) {
			ApplicationDict obj = business.entityManagerContainer().find(_o.getId(), ApplicationDict.class);
			if (null != obj) {
				throw new ExceptionEntityExistForCreate(_o.getId(), ApplicationDict.class);
			}
			obj = WrapApplicationDict.inCopier.copy(_o);
			obj.setApplication(portal.getId());
			obj.setProject(ApplicationDict.PROJECT_PORTAL);
			persistObjects.add(obj);
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(_o.getData());
			for (ApplicationDictItem o : list) {
				o.setBundle(obj.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(obj.getDistributeFactor());
				o.setApplication(obj.getApplication());
				persistObjects.add(o);
			}
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
		business.entityManagerContainer().commit();
		CacheManager.notify(ApplicationDictItem.class);
		CacheManager.notify(ApplicationDict.class);
		CacheManager.notify(Script.class);
		CacheManager.notify(Page.class);
		CacheManager.notify(Widget.class);
		CacheManager.notify(Portal.class);
		return portal;
	}

	public static class Wi extends WrapPortal {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WoId {

	}

}
