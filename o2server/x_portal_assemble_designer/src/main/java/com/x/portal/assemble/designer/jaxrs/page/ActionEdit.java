package com.x.portal.assemble.designer.jaxrs.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.x.portal.assemble.designer.ThisApplication;
import com.x.portal.core.entity.PageVersion;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionEdit extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Page page = emc.find(id, Page.class);
			if (null == page) {
				throw new PageNotExistedException(id);
			}
			Portal portal = emc.find(page.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(page.getPortal());
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Page.class);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, page);
			this.checkName(business, page);
			this.checkAlias(business, page);
			page.getProperties().setRelatedWidgetList(wi.getRelatedWidgetList());
			page.getProperties().setMobileRelatedWidgetList(wi.getMobileRelatedWidgetList());
			page.getProperties().setRelatedScriptMap(wi.getRelatedScriptMap());
			page.getProperties().setMobileRelatedScriptMap(wi.getMobileRelatedScriptMap());
			emc.check(page, CheckPersistType.all);
			emc.beginTransaction(Portal.class);
			/** 更新首页 */
			if (this.isBecomeFirstPage(business, portal, page)) {
				portal.setFirstPage(page.getId());
			} else if (StringUtils.isEmpty(portal.getFirstPage())
					|| (null == emc.find(portal.getFirstPage(), Page.class))) {
				/* 如果是第一个页面,设置这个页面为当前页面 */
				portal.setFirstPage(page.getId());
			}
			if(StringUtils.isNotBlank(wi.getCornerMarkScript()) && StringUtils.isNotBlank(wi.getCornerMarkScriptText())) {
				portal.setCornerMarkScript(wi.getCornerMarkScript());
				portal.setCornerMarkScriptText(wi.getCornerMarkScriptText());
			}
			emc.commit();
			CacheManager.notify(Page.class);
			CacheManager.notify(Portal.class);
			// 保存历史版本
			ThisApplication.pageVersionQueue.send(new PageVersion(page.getId(), jsonElement, effectivePerson.getDistinguishedName()));
			Wo wo = new Wo();
			wo.setId(page.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Page {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Page> copier = WrapCopierFactory.wi(Wi.class, Page.class, null, JpaObject.FieldsUnmodify);

		@FieldDescribe("关联Widget.")
		private List<String> relatedWidgetList = new ArrayList<>();

		@FieldDescribe("移动端关联Widget.")
		private List<String> mobileRelatedWidgetList = new ArrayList<>();

		@FieldDescribe("关联脚本.")
		private Map<String, String> relatedScriptMap = new LinkedHashMap<>();

		@FieldDescribe("移动端关联脚本.")
		private Map<String, String> mobileRelatedScriptMap = new LinkedHashMap<>();

		@FieldDescribe("角标关联脚本.")
		private String cornerMarkScript;

		@FieldDescribe("角标脚本文本.")
		private String cornerMarkScriptText;

		public List<String> getRelatedWidgetList() {
			return this.relatedWidgetList == null ? new ArrayList<>() : this.relatedWidgetList;
		}

		public List<String> getMobileRelatedWidgetList() {
			return this.mobileRelatedWidgetList == null ? new ArrayList<>() : this.mobileRelatedWidgetList;
		}

		public Map<String, String> getRelatedScriptMap() {
			return this.relatedScriptMap == null ? new LinkedHashMap<>() : this.relatedScriptMap;
		}

		public Map<String, String> getMobileRelatedScriptMap() {
			return this.mobileRelatedScriptMap == null ? new LinkedHashMap<>() : this.mobileRelatedScriptMap;
		}

		public void setRelatedWidgetList(List<String> relatedWidgetList) {
			this.relatedWidgetList = relatedWidgetList;
		}

		public void setMobileRelatedWidgetList(List<String> mobileRelatedWidgetList) {
			this.mobileRelatedWidgetList = mobileRelatedWidgetList;
		}

		public void setRelatedScriptMap(Map<String, String> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

		public void setMobileRelatedScriptMap(Map<String, String> mobileRelatedScriptMap) {
			this.mobileRelatedScriptMap = mobileRelatedScriptMap;
		}

		public String getCornerMarkScript() {
			return cornerMarkScript;
		}

		public void setCornerMarkScript(String cornerMarkScript) {
			this.cornerMarkScript = cornerMarkScript;
		}

		public String getCornerMarkScriptText() {
			return cornerMarkScriptText;
		}

		public void setCornerMarkScriptText(String cornerMarkScriptText) {
			this.cornerMarkScriptText = cornerMarkScriptText;
		}
	}

	public static class Wo extends WoId {

	}

}
