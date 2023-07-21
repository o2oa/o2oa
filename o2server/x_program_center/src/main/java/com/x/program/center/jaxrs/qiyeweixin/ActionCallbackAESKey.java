package com.x.program.center.jaxrs.qiyeweixin;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionCallbackAESKey extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCallbackAESKey.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
        if (!effectivePerson.isManager()) {
			 throw new ExceptionAccessDenied(effectivePerson);
		}
        ActionResult<Wo> result = new ActionResult<Wo>();
        String encodingAesKey = Base64.encodeBase64String(UUID.randomUUID().toString().replaceAll("-","").getBytes());
        logger.info("生成企业微信回调用的 AESKey "+ encodingAesKey);
        if (encodingAesKey.contains("=")) {
            encodingAesKey = encodingAesKey.substring(0, encodingAesKey.lastIndexOf("="));
        }
        Wo wo = new Wo();
        wo.setKey(encodingAesKey);
        result.setData(wo);
        return result;
    }


    public static class Wo extends GsonPropertyObject {


        private static final long serialVersionUID = 6701949833103126778L;


        @FieldDescribe("EncodingAESKey")
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
