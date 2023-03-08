package com.x.portal.assemble.designer.jaxrs.output;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.general.core.entity.ApplicationDictItem_;
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

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

class ActionSelect extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String portalFlag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Portal portal = emc.flag(portalFlag, Portal.class);
			if (null == portal) {
				throw new ExceptionPortalNotExist(portalFlag);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
						portal.getId());
			}
			WrapPortal wrapPortal = this.get(business, portal, wi);
			CacheObject cacheObject = new CacheObject();
			cacheObject.setName(portal.getName());
			cacheObject.setPortal(wrapPortal);
			String flag = StringTools.uniqueToken();
			CacheKey cacheKey = new CacheKey(this.getClass(), flag);
			CacheManager.put(this.cache, cacheKey, cacheObject);
			Wo wo = XGsonBuilder.convert(wrapPortal, Wo.class);
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private WrapPortal get(Business business, Portal portal, Wi wi) throws Exception {
		WrapPortal wo = WrapPortal.outCopier.copy(portal);
		wo.setWidgetList(
				WrapWidget.outCopier.copy(business.entityManagerContainer().list(Widget.class, wi.listWidgetId())));
		wo.setPageList(WrapPage.outCopier.copy(business.entityManagerContainer().list(Page.class, wi.listPageId())));
		wo.setScriptList(
				WrapScript.outCopier.copy(business.entityManagerContainer().list(Script.class, wi.listScriptId())));
		wo.setFileList(WrapFile.outCopier.copy(business.entityManagerContainer().list(File.class, wi.listFileId())));
		wo.setApplicationDictList(this.listApplicationDict(business, wi));
		return wo;
	}

	private List<WrapApplicationDict> listApplicationDict(Business business, Wi wi)
			throws Exception {
		List<WrapApplicationDict> wos = new ArrayList<>();
		for (String id : ListTools.trim(wi.listApplicationDictId(), true, true)) {
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			ApplicationDict applicationDict = business.entityManagerContainer().find(id, ApplicationDict.class);
			if(applicationDict != null) {
				WrapApplicationDict wo = WrapApplicationDict.outCopier.copy(applicationDict);
				List<ApplicationDictItem> items = this.listApplicationDictItem(business, applicationDict);
				JsonElement json = converter.assemble(items);
				wo.setData(json);
				wos.add(wo);
			}
		}
		return wos;
	}

	private List<ApplicationDictItem> listApplicationDictItem(Business business, ApplicationDict applicationDict)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wi extends WrapPortal {

		private static final long serialVersionUID = 4926992449874268707L;

	}

	public static class Wo extends WrapPortal {

		private static final long serialVersionUID = -1130848016754973977L;
		@FieldDescribe("返回标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

}
