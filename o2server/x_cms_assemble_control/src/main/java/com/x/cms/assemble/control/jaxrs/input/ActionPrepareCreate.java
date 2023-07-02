package com.x.cms.assemble.control.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapPair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.wrap.WrapAppDict;
import com.x.cms.core.entity.element.wrap.WrapCategoryInfo;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.cms.core.entity.element.wrap.WrapFile;
import com.x.cms.core.entity.element.wrap.WrapForm;
import com.x.cms.core.entity.element.wrap.WrapScript;

class ActionPrepareCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPrepareCreate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = this.adjustForCreate(business, wi);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> adjustForCreate(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		AppInfo exist_application = business.entityManagerContainer().find(wi.getId(), AppInfo.class);
		if (null != exist_application) {
			wos.add(new Wo(wi.getId(), JpaObject.createId()));
		}
		for (WrapForm wrap : wi.getFormList()) {
			Form exist_form = business.entityManagerContainer().find(wrap.getId(), Form.class);
			if (null != exist_form) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapScript wrap : wi.getScriptList()) {
			Script exist_script = business.entityManagerContainer().find(wrap.getId(), Script.class);
			if (null != exist_script) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		
		for (WrapFile wrap : wi.getFileList()) {
			File exist_file = business.entityManagerContainer().find(wrap.getId(), File.class);
			if (null != exist_file) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		
		for (WrapAppDict wrap : wi.getAppDictList()) {
			AppDict exist_applicationDict = business.entityManagerContainer().find(wrap.getId(),
					AppDict.class);
			if (null != exist_applicationDict) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapCategoryInfo wrapCategoryInfo : wi.getCategoryInfoList()) {
			CategoryInfo exist_categoryInfo = business.entityManagerContainer().find(wrapCategoryInfo.getId(), CategoryInfo.class);
			if (null != exist_categoryInfo) {
				wos.add(new Wo(wrapCategoryInfo.getId(), JpaObject.createId()));
			}
		}
		return wos;
	}

	public static class Wi extends WrapCms {
		private static final long serialVersionUID = -4612391443319365035L;
	}

	public static class Wo extends WrapPair {
		public Wo(String value, String replaceValue) {
			this.setFirst(value);
			this.setSecond(replaceValue);
		}
	}

}