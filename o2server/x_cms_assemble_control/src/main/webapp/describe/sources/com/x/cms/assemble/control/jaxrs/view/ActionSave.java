package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ViewAction.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		View view = null;
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
			if ( StringUtils.isEmpty(wi.getFormId())) {
				check = false;
				Exception exception = new ExceptionViewInfoFormIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if ( StringUtils.isEmpty(wi.getAppId())) {
				check = false;
				Exception exception = new ExceptionViewInfoAppIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				view = viewServiceAdv.save(wi, effectivePerson, wi.getFields());
				new LogService().log(null, effectivePerson.getDistinguishedName(), view.getName(), view.getAppId(), "",
						"", view.getId(), "VIEW", "保存");

				ApplicationCache.notify(View.class);
				ApplicationCache.notify(ViewFieldConfig.class);
				ApplicationCache.notify(ViewCategory.class);

				Wo wo = new Wo();
				wo.setId(view.getId());
				result.setData(wo);

			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionViewInfoProcess(e, "系统保存视图信息对象时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends View {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public static WrapCopier<Wi, View> copier = WrapCopierFactory.wi(Wi.class, View.class, null,
				JpaObject.FieldsUnmodify);

		private List<ViewFieldConfig> fields = null;

		public List<ViewFieldConfig> getFields() {
			return fields;
		}

		public void setFields(List<ViewFieldConfig> fields) {
			this.fields = fields;
		}

	}

	public static class Wo extends WoId {

	}
}