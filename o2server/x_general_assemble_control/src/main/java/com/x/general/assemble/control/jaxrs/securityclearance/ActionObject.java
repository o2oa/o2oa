package com.x.general.assemble.control.jaxrs.securityclearance;

import java.util.LinkedHashMap;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionObject extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionObject.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		final Integer system = Config.ternaryManagement().getSystemSecurityClearance();
		if (null == system) {
			throw new ExceptionEmptySystemSecurityClearance();
		}
		Config.ternaryManagement().getObjectSecurityClearance().entrySet().stream()
				.filter(o -> (null != o.getValue()) && o.getValue() >= system)
				.sorted((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()))
				.forEach(o -> wo.put(o.getKey(), o.getValue()));
		result.setData(wo);
		return result;
	}

	public class Wo extends LinkedHashMap<String, Integer> {

		private static final long serialVersionUID = 1713071186691612472L;

	}

}
