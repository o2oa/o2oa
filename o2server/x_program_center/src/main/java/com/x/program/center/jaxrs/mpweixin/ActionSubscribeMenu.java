package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.MPWeixinMenu;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionSubscribeMenu extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSubscribeMenu.class);

    ActionResult<WoMenu> execute()  throws Exception {
        ActionResult<WoMenu> result = new ActionResult<>();
        MPWeixinMenu menu = findMenuWithEventKey(WX_MSG_RECEIVE_EVENT_SUBSCRIBE);
        if (menu != null) {
            WoMenu wo = WoMenu.copier.copy(menu);
            result.setData(wo);

        }else {
            WoMenu wo = new WoMenu();
            result.setData(wo);
        }
        return result;
    }

}
