package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.Message;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    public static final String IM_CONFIG_KEY_NAME = "imConfig"; // 这个配置会已对象写入到 web.json ，已imConfig作为key名称

    // 发送会话消息
    public void sendConversationMsg(List<String> persons, IMConversation conversation, String messageType) {
        for (String person : persons) {
            String title = "会话的消息";
            MessageConnector.send(messageType, title, person, conversation);
        }
    }
    // 发送聊天消息
    public void sendWsMessage(IMConversation conversation, IMMsg msg, String messageType, EffectivePerson effectivePerson) {
        // 发送消息
        List<String> persons = conversation.getPersonList();
        persons.removeIf(s -> (effectivePerson.getDistinguishedName().equals(s)));
        for (int i = 0; i < persons.size(); i++) {
            String name = "";
            try {
                name = effectivePerson.getDistinguishedName().substring(0,
                        effectivePerson.getDistinguishedName().indexOf("@"));
            } catch (Exception e) {
                LOGGER.error(e);
            }
            String person = persons.get(i);
            LOGGER.info("发送im消息， person: " + person + " messageType: "+messageType);
            String title = "来自 " + name + " 的消息";
            MessageConnector.send(messageType, title, person, msg);
            // 如果消息接收者没有在线 连接ws 就发送一个推送消息
            if (MessageConnector.TYPE_IM_CREATE.equals(messageType)) { // 发送聊天消息时候 如果没有在线 发送app推送消息
                try {
                    if (!ThisApplication.wsClients().containsValue(person)) {
                        LOGGER.info("向app 推送im消息， person: " + person);
                        Message message = new Message();
                        String body = imMessageBody(msg);
                        message.setTitle(title + ": " + body);
                        message.setPerson(person);
                        message.setType(MessageConnector.TYPE_IM_CREATE);
                        message.setId("");
                        if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
                            ThisApplication.pmsinnerConsumeQueue.send(message);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }


    private String imMessageBody(IMMsg msg) {
        String json = msg.getBody();
        ActionMsgCreate.IMMessageBody body = gson.fromJson(json, ActionMsgCreate.IMMessageBody.class);
        if ("text".equals(body.getType())) {
            return body.getBody();
        } else if ("emoji".equals(body.getType())) {
            return "[表情]";
        } else if ("image".equals(body.getType())) {
            return "[图片]";
        } else if ("audio".equals(body.getType())) {
            return "[声音]";
        } else if ("location".equals(body.getType())) {
            return "[位置]";
        } else if ("file".equals(body.getType())) {
            return "[文件]";
        } else if ("process".equals(body.getType())) {
            return "[工作]";
        } else if ("cms".equals(body.getType())) {
            return "[信息]";
        } else {
            return "[其它]";
        }
    }
}