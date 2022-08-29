package com.x.cms.assemble.control.jaxrs.view;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
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
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存列表配置
 * @author sword
 */
public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ViewAction.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isBlank(wi.getAppId())){
			throw new ExceptionViewInfoAppIdEmpty();
		}
		if ( StringUtils.isEmpty(wi.getFormId())) {
			throw new ExceptionViewInfoFormIdEmpty();
		}

		AppInfo appInfo = appInfoServiceAdv.get(wi.getAppId());
		if(appInfo == null){
			throw new ExceptionAppInfoNotExists(wi.getAppId());
		}

		Business business = new Business(null);
		if (!business.isAppInfoManager(effectivePerson, appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		View view = viewServiceAdv.save(wi, effectivePerson, wi.getFields());
		new LogService().log(null, effectivePerson.getDistinguishedName(), view.getName(), view.getAppId(), "",
				"", view.getId(), "VIEW", "保存");

		CacheManager.notify(View.class);
		CacheManager.notify(ViewFieldConfig.class);
		CacheManager.notify(ViewCategory.class);

		Wo wo = new Wo();
		wo.setId(view.getId());
		result.setData(wo);
		return result;
	}

	public static class Wi extends View {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

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
