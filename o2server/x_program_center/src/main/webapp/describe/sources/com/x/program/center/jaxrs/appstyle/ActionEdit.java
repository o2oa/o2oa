package com.x.program.center.jaxrs.appstyle;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.AppStyle;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (!effectivePerson.isManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, Config.appStyle());
			Config.appStyle().save();
			Config.flush();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends AppStyle {
		static WrapCopier<Wi, AppStyle> copier = WrapCopierFactory.wi(Wi.class, AppStyle.class, null,
				ListTools.toList("images"));
	}

	public static class Wo extends WrapBoolean {
	}

}