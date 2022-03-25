package com.x.cms.assemble.control.jaxrs.categoryinfo;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

class ActionExecuteProjection extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CategoryInfo categoryInfo = emc.flag(flag, CategoryInfo.class);
			if (null == categoryInfo) {
				throw new ExceptionEntityNotExist(flag, CategoryInfo.class);
			}
			AppInfo appInfo = emc.flag(categoryInfo.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionEntityNotExist(categoryInfo.getAppId(), AppInfo.class);
			}
			if (!business.editable(effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Wo wo = new Wo();
			if (StringUtils.isNotBlank(categoryInfo.getProjection()) && XGsonBuilder.isJsonArray(categoryInfo.getProjection())) {
				if (!ThisApplication.projectionExecuteQueue.contains(categoryInfo.getId())) {
					ThisApplication.projectionExecuteQueue.send(categoryInfo.getId());
					wo.setValue(true);
				} else {
					throw new ExceptionAlreadyAddQueue();
				}
			} else {
				wo.setValue(false);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 8751222971648353980L;

		@FieldDescribe("无效字段")
		private String dummy;

		public String getDummy() {
			return dummy;
		}

		public void setDummy(String dummy) {
			this.dummy = dummy;
		}

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 5417378686087580564L;

	}

}
