package com.x.cms.assemble.control.jaxrs.form;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	@AuditLog(operation = "保存表单")
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (id != null && !id.isEmpty()) {
				wi.setId(id);
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);

				// 看看用户是否有权限进行应用信息新增操作
				if (!business.formEditAvailable( effectivePerson)) {
					throw new Exception(
							"person{name:" + effectivePerson.getDistinguishedName() + "} 用户没有内容管理表单模板信息操作的权限！");
				}
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

					Wo wo = new Wo();
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

					Wo wo = new Wo();
					wo.setId(form.getId());
					result.setData(wo);
				}
				CacheManager.notify(Form.class);
				CacheManager.notify(View.class);
				CacheManager.notify(ViewFieldConfig.class);
				CacheManager.notify(ViewCategory.class);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public static WrapCopier<Wi, Form> copier = WrapCopierFactory.wi(Wi.class, Form.class, null,
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
