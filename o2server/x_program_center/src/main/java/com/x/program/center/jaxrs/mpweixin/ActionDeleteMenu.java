package com.x.program.center.jaxrs.mpweixin;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.MPWeixinMenu;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionDeleteMenu extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionDeleteMenu.class);

    ActionResult<Wo> execute(String id)  throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        logger.debug("delete menu id : {}.", id);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            MPWeixinMenu menu = emc.find(id, MPWeixinMenu.class);
            if (menu == null) {
                throw new ExceptionNotExist();
            }
            //同时删除下级菜单
            List<MPWeixinMenu> list = emc.listEqual(MPWeixinMenu.class,  MPWeixinMenu.parentId_FIELDNAME, menu.getId());
            emc.beginTransaction(MPWeixinMenu.class);
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    MPWeixinMenu child = list.get(i);
                    emc.remove(child, CheckRemoveType.all);
                }
            }
            emc.remove(menu, CheckRemoveType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(id);
            result.setData(wo);
            return result;
        }

    }


    public static class Wo extends WoId {

    }
}
