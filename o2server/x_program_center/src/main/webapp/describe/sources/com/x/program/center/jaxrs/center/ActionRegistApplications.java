package com.x.program.center.jaxrs.center;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.CenterQueueRegistApplicationsBody;
import com.x.program.center.ThisApplication;

class ActionRegistApplications extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRegistApplications.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		CenterQueueRegistApplicationsBody body = gson.fromJson(wi.getValue(), CenterQueueRegistApplicationsBody.class);

		ThisApplication.centerQueue.send(body);

		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

	public static class Wi extends WrapString {

	}

}