package com.x.program.center.jaxrs.tokenthreshold;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.log.TokenThreshold;

class ActionUpdate extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionUpdate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Config.resource_node_tokenThresholds().put(wi.getPerson(), wi.getThreshold());
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends TokenThreshold {

		private static final long serialVersionUID = -6020110344581428321L;
		static WrapCopier<Wi, TokenThreshold> copier = WrapCopierFactory.wi(Wi.class, TokenThreshold.class, null,
				ListTools.toList(TokenThreshold.person_FIELDNAME, TokenThreshold.threshold_FIELDNAME));
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -338678364674176846L;

	}

}
