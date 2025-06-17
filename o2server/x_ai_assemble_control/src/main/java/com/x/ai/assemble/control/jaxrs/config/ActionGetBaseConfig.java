package com.x.ai.assemble.control.jaxrs.config;


import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.List;

/**
 * @author sword
 */
public class ActionGetBaseConfig extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionGetBaseConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) {
		logger.debug(effectivePerson.getDistinguishedName());

		ActionResult<Wo> result = new ActionResult<>();
		AiConfig config = Business.getConfig();
		Wo wo = Wo.copier.copy(config);
		result.setData(wo);

		return result;
	}

	public static class Wo extends AiConfig {
		static WrapCopier<AiConfig, Wo> copier = WrapCopierFactory.wo(AiConfig.class, Wo.class,
				List.of(AiConfig.appName_FIELD, AiConfig.appIconUrl_FIELD,
						AiConfig.title_FIELD, AiConfig.desc_FIELD, AiConfig.o2AiEnable_FIELD),
				null);
	}

}
