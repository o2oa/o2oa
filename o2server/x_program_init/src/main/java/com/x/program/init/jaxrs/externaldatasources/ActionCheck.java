package com.x.program.init.jaxrs.externaldatasources;

import java.lang.reflect.InvocationTargetException;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.MissionExternalDataSources;

class ActionCheck extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheck.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		MissionExternalDataSources.CheckResult checkResult = MissionExternalDataSources.check();
		checkResult.copyTo(wo);
		result.setData(wo);
		return result;
	}

	public static class Wo extends MissionExternalDataSources.CheckResult {

		private static final long serialVersionUID = 6128234476964722036L;

	}

}