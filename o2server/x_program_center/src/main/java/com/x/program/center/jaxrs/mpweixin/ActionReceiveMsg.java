package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;

/**
 * 微信公众号接收到消息的处理
 * 暂时不做处理
 * Created by fancyLou on 3/8/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionReceiveMsg  extends BaseAction {


    private static Logger logger = LoggerFactory.getLogger(ActionReceiveMsg.class);


    ActionResult<Wo> execute(String signature, Long timestamp, String nonce, String echostr, InputStream inputStream)  throws Exception {
        logger.info("微信公众号接收消息,signature:{}, timestamp:{}, nonce:{}, echostr:{}.", signature, timestamp, nonce, echostr);
        Wo wo = new Wo();
        ActionResult<Wo> actionResult = new ActionResult<>();

        if (inputStream != null) { // 解析消息
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "utf-8");
            String theString = writer.toString();
            logger.info("接收到的消息："+theString);
        }

        wo.setText("success");//不回复消息
        actionResult.setData(wo);
        return actionResult;
    }

    public static class Wo extends WoText {
    }
}
