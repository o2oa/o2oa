package com.x.portal.assemble.designer.jaxrs.output;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
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

import net.sf.ehcache.Element;

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
			this.cache.put(new Element(flag, cacheObject));
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
		return wo;
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