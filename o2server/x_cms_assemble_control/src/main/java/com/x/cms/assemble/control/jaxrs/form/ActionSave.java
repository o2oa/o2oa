package com.x.cms.assemble.control.jaxrs.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
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

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

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
					emc.check(form, CheckPersistType.all);
					emc.commit();

					logService.log(emc, effectivePerson.getDistinguishedName(), form.getName(), form.getAppId(), "", "",
							form.getId(), "FORM", "更新");

					Wo wo = new Wo();
					wo.setId(form.getId());
					result.setData(wo);
				}
				ApplicationCache.notify(Form.class);
				ApplicationCache.notify(View.class);
				ApplicationCache.notify(ViewFieldConfig.class);
				ApplicationCache.notify(ViewCategory.class);
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
	}
}