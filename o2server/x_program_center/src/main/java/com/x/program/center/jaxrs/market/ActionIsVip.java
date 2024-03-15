package com.x.program.center.jaxrs.market;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;

class ActionIsVip extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionIsVip.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(false);
		String token = Business.loginCollect();
		if (StringUtils.isNotEmpty(token)) {
			try {
				ActionResponse response = ConnectionAction.get(
						Config.collect().url(COLLECT_UNIT_IS_VIP),
						ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
				wo = response.getData(Wo.class);
			} catch (Exception e) {
				logger.warn("check unit is vip form o2cloud error: {}.", e.getMessage());
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}
