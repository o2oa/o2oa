package com.x.cms.assemble.control.jaxrs.form;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.*;

/**
 * 保存表单
 * @author sword
 */
public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			JsonElement jsonElement) throws Exception {
		logger.debug(request.getMethod());
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		if (id != null && !id.isEmpty()) {
			wi.setId(id);
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			AppInfo appInfo = emc.find(wi.getAppId(), AppInfo.class);
			if(appInfo == null){
				throw new ExceptionAppInfoNotExist(wi.getAppId());
			}
			if (!business.isAppInfoManager( effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = new Wo();
			Form form = emc.find(wi.getId(), Form.class);
			if (null == form) {
				form = Wi.copier.copy(wi);
				if (wi.getId() != null && !wi.getId().isEmpty()) {
					form.setId(wi.getId());
				}
				emc.beginTransaction(Form.class);
				form.getProperties().setRelatedFormList(wi.getRelatedFormList());
				form.getProperties().setMobileRelatedFormList(wi.getMobileRelatedFormList());
				form.getProperties().setRelatedScriptMap(wi.getRelatedScriptMap());
				form.getProperties().setMobileRelatedScriptMap(wi.getMobileRelatedScriptMap());
				emc.persist(form, CheckPersistType.all);
				emc.commit();
				logService.log(emc, effectivePerson.getDistinguishedName(), form.getName(), form.getAppId(), "", "",
						form.getId(), "FORM", "新增");
				wo.setId(form.getId());
				result.setData(wo);
			} else {
				Wi.copier.copy(wi, form);
				emc.beginTransaction(Form.class);
				form.getProperties().setRelatedFormList(wi.getRelatedFormList());
				form.getProperties().setMobileRelatedFormList(wi.getMobileRelatedFormList());
				form.getProperties().setRelatedScriptMap(wi.getRelatedScriptMap());
				form.getProperties().setMobileRelatedScriptMap(wi.getMobileRelatedScriptMap());
				emc.check(form, CheckPersistType.all);
				emc.commit();

				logService.log(emc, effectivePerson.getDistinguishedName(), form.getName(), form.getAppId(), "", "",
						form.getId(), "FORM", "更新");

				wo.setId(form.getId());
				result.setData(wo);
			}
			CacheManager.notify(Form.class);
			CacheManager.notify(View.class);
			CacheManager.notify(ViewFieldConfig.class);
			CacheManager.notify(ViewCategory.class);
			// 保存历史版本
			ThisApplication.formVersionQueue.send(new FormVersion(wo.getId(), jsonElement, effectivePerson.getDistinguishedName()));
		}

		return result;
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public static final WrapCopier<Wi, Form> copier = WrapCopierFactory.wi(Wi.class, Form.class, null,
				JpaObject.FieldsUnmodify);

		@FieldDescribe("关联表单")
		private List<String> relatedFormList = new ArrayList<>();

		@FieldDescribe("移动端关联表单")
		private List<String> mobileRelatedFormList = new ArrayList<>();

		@FieldDescribe("关联脚本.")
		private Map<String, String> relatedScriptMap = new LinkedHashMap<>();

		@FieldDescribe("移动端关联脚本.")
		private Map<String, String> mobileRelatedScriptMap = new LinkedHashMap<>();

		public List<String> getRelatedFormList() {
			return relatedFormList;
		}

		public void setRelatedFormList(List<String> relatedFormList) {
			this.relatedFormList = relatedFormList;
		}

		public List<String> getMobileRelatedFormList() {
			return mobileRelatedFormList;
		}

		public void setMobileRelatedFormList(List<String> mobileRelatedFormList) {
			this.mobileRelatedFormList = mobileRelatedFormList;
		}

		public Map<String, String> getRelatedScriptMap() {
			return relatedScriptMap;
		}

		public void setRelatedScriptMap(Map<String, String> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

		public Map<String, String> getMobileRelatedScriptMap() {
			return mobileRelatedScriptMap;
		}

		public void setMobileRelatedScriptMap(Map<String, String> mobileRelatedScriptMap) {
			this.mobileRelatedScriptMap = mobileRelatedScriptMap;
		}
	}
}
