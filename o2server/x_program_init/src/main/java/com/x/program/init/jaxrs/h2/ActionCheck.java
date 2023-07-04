package com.x.program.init.jaxrs.h2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.MissionH2Upgrade;

class ActionCheck extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheck.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson) throws IOException, URISyntaxException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		MissionH2Upgrade.check().copyTo(wo);
		result.setData(wo);
		return result;
	}

	public static class Wo extends MissionH2Upgrade.CheckResult {

		private static final long serialVersionUID = 1126451219463764428L;

	}

}