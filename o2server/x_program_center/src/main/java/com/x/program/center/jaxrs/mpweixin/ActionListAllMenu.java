package com.x.program.center.jaxrs.mpweixin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.MPWeixinMenu;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionListAllMenu extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionListAllMenu.class);

    ActionResult<Wo> execute()  throws Exception {
        ActionResult<Wo> result = new ActionResult<>();

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<MPWeixinMenu> menus = emc.listAll(MPWeixinMenu.class);
            if (menus == null) {
                menus = new ArrayList<>();
            }
            List<WoMenu> firstLevelList = new ArrayList<>();
            for (int i = 0; i < menus.size(); i++) {
                MPWeixinMenu menu = menus.get(i);
                if (StringUtils.isEmpty(menu.getParentId())) {
                    firstLevelList.add(WoMenu.copier.copy(menu));
                }
            }
            searchChildren(menus, firstLevelList);
            firstLevelList.sort((o1, o2) -> {
                if (StringUtils.isEmpty(o1.getOrder()) || StringUtils.isEmpty(o2.getOrder())) {
                    return 0;
                }
                return o1.getOrder().compareToIgnoreCase(o2.getOrder());
            });
            Wo wo = new Wo();
            wo.setButton(firstLevelList);
            result.setData(wo);
            return result;
        }

    }

    public static class Wo extends GsonPropertyObject {
        private List<WoMenu> button ;

        public List<WoMenu> getButton() {
            return button;
        }

        public void setButton(List<WoMenu> button) {
            this.button = button;
        }
    }


}
