package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;

class ActionGetIcon extends BaseAction {

	ActionResult<Wo> execute(String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(flag);
			/** 如果application 不存在,返回空值 */
			Wo wo = new Wo();
			if (null != application) {
				wo.setIcon(application.getIcon());
				wo.setIconHue(application.getIconHue());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("应用图标base64编码值")
		private String icon;

		@FieldDescribe("应用图标色调")
		private String iconHue;

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getIconHue() {
			return iconHue;
		}

		public void setIconHue(String iconHue) {
			this.iconHue = iconHue;
		}

	}

}
