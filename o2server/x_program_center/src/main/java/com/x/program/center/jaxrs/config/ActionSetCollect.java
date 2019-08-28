package com.x.program.center.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

public class ActionSetCollect extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (!Config.centerServer().getConfigApiEnable()) {
			throw new ExceptionModifyConfig();
		}
		Wi.copier.copy(wi, Config.collect());
		Config.collect().save();
		this.configFlush(effectivePerson);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

		static WrapCopier<Wi, Collect> copier = WrapCopierFactory.wi(Wi.class, Collect.class, null, null);

	}

	public static class Wo extends WrapBoolean {

	}
}