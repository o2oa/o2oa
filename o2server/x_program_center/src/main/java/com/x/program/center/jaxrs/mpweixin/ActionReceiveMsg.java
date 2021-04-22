package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.MPWeixinMenu;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Map<String, String> map = xmlToMap(inputStream);
            String msgType = map.get("MsgType");

            if (WX_MSG_RECEIVE_TYPE_EVENT.equals(msgType)) { //
                String event = map.get("Event");
                String eventKey = map.get("EventKey");
                logger.info("接收到事件消息： event: {} , eventKey: {}", event, eventKey);
                //TODO 目前只处理菜单点击事件和订阅公众号的事件 其他消息类型暂不处理
                if (StringUtils.isNotEmpty(event) && event.equalsIgnoreCase(WX_MSG_RECEIVE_EVENT_CLICK)) { //点击菜单事件
                    MPWeixinMenu menu = findMenuWithEventKey(eventKey);
                    if (menu != null) {
                        String content = menu.getContent();
                        String toUser = map.get("FromUserName");
                        String fromUser = map.get("ToUserName");
                        String xml = txtMessageBack(toUser, fromUser, content);
                        logger.info("回复点击菜单消息： {}", xml);
                        wo.setText(xml);
                        actionResult.setData(wo);
                        return actionResult;
                    } else {
                        logger.info("没有查询到对应的 eventKey：{}", eventKey);
                    }
                }else if (WX_MSG_RECEIVE_EVENT_SUBSCRIBE.equalsIgnoreCase(event)) { // 订阅事件
                    MPWeixinMenu menu = findMenuWithEventKey(WX_MSG_RECEIVE_EVENT_SUBSCRIBE);
                    if (menu != null) {
                        String content = menu.getContent();
                        String toUser = map.get("FromUserName");
                        String fromUser = map.get("ToUserName");
                        String xml = txtMessageBack(toUser, fromUser, content);
                        logger.info("回复关注公众号的消息： {}", xml);
                        wo.setText(xml);
                        actionResult.setData(wo);
                        return actionResult;
                    } else {
                        logger.info("没有查询到对应的 eventKey：{}", eventKey);
                    }
                }
            } else {
                logger.info("未处理消息类型, MsgType: {}", msgType);
            }
        }else {
            logger.info("没有获取到消息内容 inputStream 为空！");
        }
        wo.setText("success");//不回复消息
        actionResult.setData(wo);
        return actionResult;
    }

    private MPWeixinMenu findMenuWithEventKey(String eventKey) throws Exception {
        if (StringUtils.isBlank(eventKey)) {
            return null;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<MPWeixinMenu> list = emc.listEqual(MPWeixinMenu.class, MPWeixinMenu.key_FIELDNAME, eventKey);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    private String txtMessageBack(String toUser, String from, String content) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[" + content + "]]></Content>" +
                "</xml>";
        return xml;
    }





    /**
     * 读取xml
     * @param ins
     * @return
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(InputStream ins) throws Exception{
        Map<String, String> map = new HashMap<>();
        if (ins == null) {
            return map;
        }
        SAXReader reader = new SAXReader();

        Document doc = reader.read(ins);

        Element root = doc.getRootElement();

        List<Element> list = root.elements();

        for(Element e : list){
            map.put(e.getName(), e.getText());
        }
        ins.close();
        return map;
    }

    public static class Wo extends WoText {
    }
}
