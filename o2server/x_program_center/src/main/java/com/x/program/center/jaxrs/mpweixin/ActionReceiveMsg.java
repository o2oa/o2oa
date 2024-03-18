package com.x.program.center.jaxrs.mpweixin;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.MPWeixinMenu;

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

            if (WX_MSG_RECEIVE_TYPE_EVENT.equals(msgType)) { // 事件出发
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
                        // 特殊消息处理 默认是回复文字消息 如果是媒体消息 需要配置特殊的头 media:image: 后面跟微信素材的mediaId
                        if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_IMAGE)) {
                            String mediaId = content.substring(WX_MSG_BACK_MEDIA_KEY_IMAGE.length());
                            String xml = imageMessageBack(toUser, fromUser, mediaId);
                            logger.info("回复点击菜单消息： {}", xml);
                            wo.setText(xml);
                        } else if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_VOICE)) {
                            String mediaId = content.substring(WX_MSG_BACK_MEDIA_KEY_VOICE.length());
                            String xml = voiceMessageBack(toUser, fromUser, mediaId);
                            logger.info("回复点击菜单消息： {}", xml);
                            wo.setText(xml);
                        } else if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_VIDEO)) {
                            // 视频消息需要多两个字段 title和description 用｜分割  如： mediaId|title|description
                            String videoContent = content.substring(WX_MSG_BACK_MEDIA_KEY_VIDEO.length());
                            String xml = null;
                            try {
                                String[] arr = videoContent.split("\\|");
                                xml = videoMessageBack(toUser, fromUser, arr[0], arr[1], arr[2]);
                            } catch (Exception e) {
                                logger.error(e);
                                xml = txtMessageBack(toUser, fromUser, content);
                            }
                            logger.info("回复点击菜单消息： {}", xml);
                            wo.setText(xml);
                        } else if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_SCRIPT)) {
                            String scriptId = content.substring(WX_MSG_BACK_MEDIA_KEY_SCRIPT.length());
                            if (StringUtils.isNotBlank(scriptId)) {
                                ExecuteServiceScriptThread runner1 = new ExecuteServiceScriptThread(toUser, "", scriptId);
                                Thread thread1 = new Thread(runner1);
                                thread1.start();
                            }
                            //执行脚本了 不回复消息
                            wo.setText("success");
                            actionResult.setData(wo);
                            return actionResult;
                        }  else {
                            String xml = txtMessageBack(toUser, fromUser, content);
                            logger.info("回复点击菜单消息： {}", xml);
                            wo.setText(xml);
                        }
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
            } else if (WX_MSG_RECEIVE_TYPE_TEXT.equalsIgnoreCase(msgType)) { // 接收到文本消息
                String text = map.get("Content");
                String toUser = map.get("FromUserName");
                logger.info("接收到文本消息： text: {} ", text);
                //TODO 目前支持异步执行服务脚本，这里必须马上返回 否则会超时，脚本里面可以调用微信客服消息进行回复. 脚本id暂时先保存到配置文件
                String id = Config.mpweixin().getScriptId();
                if (StringUtils.isNotBlank(id)) {
                    ExecuteServiceScriptThread runner1 = new ExecuteServiceScriptThread(toUser, text, id);
                    Thread thread1 = new Thread(runner1);
                    thread1.start();
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


    // 回复文本消息
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

    // 回复图片消息
    private String imageMessageBack(String toUser, String from, String mediaId) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[image]]></MsgType>" +
                "<Image><MediaId><![CDATA["+mediaId+"]]></MediaId></Image>" +
                "</xml>";
        return xml;
    }

    // 回复音频消息
    private String voiceMessageBack(String toUser, String from, String mediaId) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[voice]]></MsgType>" +
                "<Voice><MediaId><![CDATA["+mediaId+"]]></MediaId></Voice>" +
                "</xml>";
        return xml;
    }

    //回复视频消息
    private String videoMessageBack(String toUser, String from, String mediaId, String title, String description) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[video]]></MsgType>" +
                "<Video>"+
                "<MediaId><![CDATA["+mediaId+"]]></MediaId>" +
                "<Title><![CDATA["+title+"]]></Title>" +
                "<Description><![CDATA["+description+"]]></Description>"  +
                "</Video>" +
                "</xml>";
        return xml;
    }

    private void asyncPostServiceScript(String text) {

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
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
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


    public static class ExecuteServiceScriptThread implements Runnable {
        String toUser;
        String text;
        String scriptId;
        ExecuteServiceScriptThread(String toUser, String text, String scriptId) {
            this.toUser = toUser;
            this.text = text;
            this.scriptId = scriptId;
        }
        @Override
        public void run() {
            evalRemote();
        }

        private void evalRemote() {
            try {
                if (StringUtils.isNotBlank(scriptId)) {
                    ScriptExecuteBody body = new ScriptExecuteBody();
                    body.setKeyword(text);
                    body.setOpenId(toUser);
                    ActionResponse result = CipherConnectionAction.post(false,
                            Config.url_x_program_center_jaxrs("invoke",scriptId, "execute"), body);
                    logger.info("执行脚本结果： " + result.toJson());
                } else {
                    logger.warn("没有配置服务脚本id");
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    /**
     * 脚本执行传入对象
     */
    public static class ScriptExecuteBody extends GsonPropertyObject {
        // 微信公众号 用户的 openId
        private String openId;
        private String keyword;

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }
}
