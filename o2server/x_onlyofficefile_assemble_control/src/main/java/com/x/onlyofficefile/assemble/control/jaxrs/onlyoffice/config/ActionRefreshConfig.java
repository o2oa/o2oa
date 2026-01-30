package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.config;


import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ConfigManager;

public class ActionRefreshConfig extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionRefreshConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		ConfigManager.init("0");
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}


	public static class Wo  extends WrapBoolean {
	}
}
