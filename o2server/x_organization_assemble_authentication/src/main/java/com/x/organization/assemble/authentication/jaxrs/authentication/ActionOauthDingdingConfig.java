package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionOauthDingdingConfig extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOauthDingdingConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isTrue(Config.dingding().getScanLoginEnable())) {
			Wo dingding = Wo.copier.copy(Config.dingding());
			result.setData(dingding);
			return result;
		}
		return result;
	}


	public static class Wo extends Dingding {
 
		static WrapCopier<Dingding, Wo> copier = WrapCopierFactory.wo(Dingding.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible, "appSecret", "encodingAesKey", "scanLoginAppSecret"));
	}

}
