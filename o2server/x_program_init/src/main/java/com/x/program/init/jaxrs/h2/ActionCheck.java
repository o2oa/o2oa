package com.x.program.init.jaxrs.h2;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Optional;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.H2Tools;

class ActionCheck extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheck.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		BaseAction.CheckResult checkResult = new BaseAction.CheckResult();
		checkResult
				.setConfigured(Files.exists(Config.pathLocalRepositoryData(true).resolve(H2Tools.FILENAME_DATABASE)));
		Optional<String> opt = H2Tools.jarVersion();
		if (opt.isPresent()) {
			checkResult.setVersion(opt.get());
		}
		checkResult.copyTo(wo);
		result.setData(wo);
		return result;
	}

	public static class Wo extends BaseAction.CheckResult {

		private static final long serialVersionUID = 6128234476964722036L;

	}

}