package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Qiyeweixin;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionOauthQiyeweixinConfig extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionOauthQiyeweixinConfig.class);

    ActionResult<Qiyeweixin> execute(EffectivePerson effectivePerson) throws Exception {
        ActionResult<Qiyeweixin> result = new ActionResult<>();
        if (Config.qiyeweixin().getScanLoginEnable()) {
            result.setData(Config.qiyeweixin());
            return result;
        }
        return result;
    }


}
