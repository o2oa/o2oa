package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.core.entity.PushDevice;

import javax.servlet.http.HttpServletRequest;

/**
 * android 端 app使用 当前服务器启用了哪种推送渠道 极光还是华为
 *
 * Created by fancyLou on 9/14/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionConfigPushType extends BaseAction {

    private Logger logger = LoggerFactory.getLogger(ActionBind.class);

    protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setPushType(PushDevice.PUSH_TYPE_JPUSH);
        result.setData(wo);
        return result;
    }


    /**
     *
     * 向外输出的结果对象包装类
     *
     */
    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("推送通道类型：jpush")
        private String pushType;


        public String getPushType() {
            return pushType;
        }

        public void setPushType(String pushType) {
            this.pushType = pushType;
        }
    }
    }
