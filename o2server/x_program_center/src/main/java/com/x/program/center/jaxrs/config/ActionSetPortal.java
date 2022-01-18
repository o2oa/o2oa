package com.x.program.center.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Portal;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import java.util.ArrayList;
import java.util.Map;

/**
 * config/portal.json配置修改
 * @author sword
 */
public class ActionSetPortal extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Map<String,Object> map = XGsonBuilder.instance().fromJson(jsonElement, Map.class);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (!Config.nodes().centerServers().first().getValue().getConfigApiEnable()) {
			throw new ExceptionModifyConfig();
		}
		Wi.copier = WrapCopierFactory.wi(Wi.class, Portal.class, new ArrayList<>(map.keySet()), null);
		Wi.copier.copy(wi, Config.portal());
		wi.copyTo(Config.portal(), true);
		Config.portal().save();
		this.configFlush(effectivePerson);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends Portal {

		static WrapCopier<Wi, Portal> copier = WrapCopierFactory.wi(Wi.class, Portal.class, null, null);

	}

	public static class Wo extends WrapBoolean {

	}
}
