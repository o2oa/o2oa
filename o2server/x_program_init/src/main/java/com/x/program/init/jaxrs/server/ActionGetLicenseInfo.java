package com.x.program.init.jaxrs.server;

import com.x.base.core.entity.LcInfo;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

class ActionGetLicenseInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetLicenseInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception{

		ActionResult<Wo> result = new ActionResult<>();
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		Wo wo = new Wo();
		wo.setVersion(Config.version());
		try {
			Class<?> licenseToolsCls = Class.forName("com.x.base.core.lc.LcTools");
			String info = (String) MethodUtils.invokeStaticMethod(licenseToolsCls, "getInfo");
			if(StringUtils.isNotBlank(info)){
				wo = XGsonBuilder.instance().fromJson(info, Wo.class);
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends LcInfo {
	}

}
