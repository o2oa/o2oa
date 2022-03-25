package com.x.program.center.jaxrs.mpweixin;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.core.entity.MPWeixinMenu;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionUpdateMenu extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionUpdateMenu.class);

    ActionResult<Wo> execute(String id, JsonElement jsonElement)  throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        logger.debug("update menu : {}.", jsonElement);
        if (StringUtils.isBlank(id)) {
            throw new ExceptionNotEmpty("id");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            MPWeixinMenu menu = emc.find(id, MPWeixinMenu.class);

            Wi wi =  this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isBlank(wi.getName())) {
                throw new ExceptionNotEmpty("name");
            }
            String type = wi.getType();
            if (StringUtils.isNotBlank(type)) { //type 为空，是空菜单，一般作为父菜单用的
                if ("click".equals(type)) {
                    if (StringUtils.isBlank(wi.getKey())) {
                        throw new ExceptionNotEmpty("key");
                    }
                    if (StringUtils.isBlank(wi.getContent())) {
                        throw new ExceptionNotEmpty("content");
                    }
                } else if ("view".equals(type)) {
                  if (StringUtils.isBlank(wi.getUrl())) {
                      throw new ExceptionNotEmpty("url");
                  }
                } else if ("miniprogram".equals(type)) {
                    if (StringUtils.isBlank(wi.getUrl())) {
                        throw new ExceptionNotEmpty("url");
                    }
                    if (StringUtils.isBlank(wi.getAppid())) {
                        throw new ExceptionNotEmpty("appid");
                    }
                    if (StringUtils.isBlank(wi.getPagepath())) {
                        throw new ExceptionNotEmpty("pagepath");
                    }
                } else if(WX_MSG_RECEIVE_EVENT_SUBSCRIBE.equals(type)) { //关注事件 添加了之后会发送文本消息
                    wi.setKey(WX_MSG_RECEIVE_EVENT_SUBSCRIBE);
                    if (StringUtils.isBlank(wi.getContent())) {
                        throw new ExceptionNotEmpty("content");
                    }
                } else {
                    throw new ExceptionTypeNotSupport();
                }
            }

            Wi.copier.copy(wi, menu);
            emc.beginTransaction(MPWeixinMenu.class);
            emc.check(menu, CheckPersistType.all);
            emc.commit();

            Wo wo = Wo.copier.copy(menu);
            result.setData(wo);
            return result;
        }

    }


    public static class Wo extends MPWeixinMenu {
        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<MPWeixinMenu, Wo> copier = WrapCopierFactory.wo(MPWeixinMenu.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }

    public static class Wi extends MPWeixinMenu {

        private static final long serialVersionUID = 4704212776164514486L;

        static WrapCopier<Wi, MPWeixinMenu> copier = WrapCopierFactory.wi(Wi.class, MPWeixinMenu.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify));
    }
}
