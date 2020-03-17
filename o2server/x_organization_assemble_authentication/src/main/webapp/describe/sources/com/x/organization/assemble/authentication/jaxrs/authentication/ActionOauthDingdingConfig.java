package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.config.Qiyeweixin;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionOauthDingdingConfig extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionOauthDingdingConfig.class);

    ActionResult<Dingding> execute(EffectivePerson effectivePerson) throws Exception {
        ActionResult<Dingding> result = new ActionResult<>();
        if (Config.dingding().getScanLoginEnable()) {
            result.setData(Config.dingding());
            return result;
        }
        return result;
    }


}
