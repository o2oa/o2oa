package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Qiyeweixin;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionOauthQiyeweixinConfig extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOauthQiyeweixinConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isTrue(Config.qiyeweixin().getScanLoginEnable())) {
			Wo qiyeweixin = Wo.copier.copy(Config.qiyeweixin());
			result.setData(qiyeweixin);
			return result;
		}
		return result;
	}

	public static class Wo extends Qiyeweixin {
 
		static WrapCopier<Qiyeweixin, Wo> copier = WrapCopierFactory.wo(Qiyeweixin.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible, "syncSecret", "corpSecret", "encodingAesKey", "attendanceSyncSecret"));
	}
}
