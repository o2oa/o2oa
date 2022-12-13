package com.x.program.center.jaxrs.mpweixin;

import java.util.Arrays;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.jaxrs.qiyeweixin.SHA1;

/**
 * Created by fancyLou on 3/1/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionCheckMPWeixin extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCheckMPWeixin.class);

    ActionResult<Wo> execute(String signature, Long timestamp, String nonce, String echostr)  throws Exception {
        logger.info("微信公众号接收到验证消息,signature:{}, timestamp:{}, nonce:{}, echostr:{}.", signature, timestamp, nonce, echostr);
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        String token = Config.mpweixin().getToken();
        wo.setText(checkSignature(signature, timestamp, nonce, echostr, token));
        result.setData(wo);

        return result;
    }

    /**
     * 公众号 开发者配置验证
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @param token
     * @return
     * @throws Exception
     */
    private String checkSignature(String signature, Long timestamp, String nonce, String echostr, String token) throws Exception  {

        String[] array = new String[] { token, timestamp+"", nonce };
        StringBuffer sb = new StringBuffer();
        // 字符串排序
        Arrays.sort(array);
        for (int i = 0; i < 3; i++) {
            sb.append(array[i]);
        }
        String str = sb.toString();
        String hexstr = SHA1.sha1(str);
        logger.info("我们的 {}, 传入的 {}", hexstr, signature);
        if (signature.equalsIgnoreCase(hexstr)){
            return echostr;
        }
        return null;
    }



    public static class Wo extends WoText {
    }

}
