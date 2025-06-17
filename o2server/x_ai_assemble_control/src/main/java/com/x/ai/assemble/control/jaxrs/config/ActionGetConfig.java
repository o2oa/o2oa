package com.x.ai.assemble.control.jaxrs.config;


import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.List;

/**
 * @author sword
 */
public class ActionGetConfig extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionGetConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception{
		logger.debug(effectivePerson.getDistinguishedName());
		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		ActionResult<Wo> result = new ActionResult<>();
		AiConfig config = Business.getConfig();
		Wo wo = Wo.copier.copy(config);
		result.setData(wo);

		return result;
	}

	public static class Wo extends AiConfig {
		static WrapCopier<AiConfig, Wo> copier = WrapCopierFactory.wo(AiConfig.class, Wo.class,
				null, null);
	}

}
