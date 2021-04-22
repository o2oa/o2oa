package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.assemble.communicate.ws.collaboration.ActionCollaboration;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.Message;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_GROUP;
import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_SINGLE;


public class ActionMsgCreate extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionMsgCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)  throws Exception {

        logger.debug("receive{}.", jsonElement);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            IMMsg msg = this.convertToWrapIn(jsonElement, IMMsg.class);

            if (msg.getConversationId() == null || msg.getConversationId().isEmpty()) {
                throw new ExceptionMsgEmptyConversationId();
            }
            if (msg.getBody() == null || msg.getBody().isEmpty()) {
                throw new ExceptionMsgEmptyBody();
            }
            msg.setCreatePerson(effectivePerson.getDistinguishedName());
            escapeHTML(msg);
            logger.info("escape html json:" + msg.getBody());

            emc.beginTransaction(IMMsg.class);
            emc.persist(msg, CheckPersistType.all);
            emc.commit();

            emc.beginTransaction(IMConversation.class);
            IMConversation conversation = emc.find(msg.getConversationId(), IMConversation.class);
            conversation.setLastMessageTime(new Date());
            emc.check(conversation, CheckPersistType.all);
            emc.commit();

            //发送消息
            List<String> persons = conversation.getPersonList();
            persons.removeIf(s -> (effectivePerson.getDistinguishedName().equals(s)));
            for (int i = 0; i < persons.size(); i++) {
                String name = "";
                try {
                    name = effectivePerson.getDistinguishedName().substring(0, effectivePerson.getDistinguishedName().indexOf("@"));
                } catch (Exception e) {
                    logger.error(e);
                }
                String person = persons.get(i);
                logger.info("发送im消息， person: " + person);
                String title = "来自 "+ name + " 的消息";
                MessageConnector.send(MessageConnector.TYPE_IM_CREATE,  title, person, msg);
                //如果消息接收者没有在线 连接ws 就发送一个推送消息
                try {
                    if (!ActionCollaboration.clients.values().contains(person)) {
                        logger.info("向app 推送im消息， person: " + person);
                        Message message = new Message();
                        String body = imMessageBody(msg);
                        message.setTitle(title + ": " + body);
                        message.setPerson(person);
                        message.setType(MessageConnector.TYPE_IM_CREATE);
                        message.setId("");
                        if (Config.communicate().pmsEnable()) {
                            ThisApplication.pmsConsumeQueue.send(message);
                        }
                        if (Config.pushConfig().getEnable()) {
                            ThisApplication.pmsInnerConsumeQueue.send(message);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = Wo.copier.copy(msg);
            result.setData(wo);
            return result;
        }
    }

    private void escapeHTML(IMMsg msg) {
        String json = msg.getBody();
        IMMessageBody body = gson.fromJson(json, IMMessageBody.class);
        if ("text".equals(body.getType())) {
            String msgBody = body.getBody();
            String msgBodyEscape = StringEscapeUtils.escapeHtml4(msgBody);
            logger.info(msgBodyEscape);
            body.setBody(msgBodyEscape);
            msg.setBody(gson.toJson(body));
        }
    }

    private String imMessageBody(IMMsg msg) {
        String json  = msg.getBody();
        IMMessageBody body = gson.fromJson(json, IMMessageBody.class);
        if ("text".equals(body.getType())) {
            return body.getBody();
        }else if ("emoji".equals(body.getType())) {
            return "[表情]";
        }else if ("image".equals(body.getType())) {
            return "[图片]";
        }else if ("audio".equals(body.getType())) {
            return "[声音]";
        }else if ("location".equals(body.getType())) {
            return "[位置]";
        }else if ("file".equals(body.getType())) {
            return "[文件]";
        }else {
            return "[其它]";
        }
    }



    public static class Wo extends IMMsg {

        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMMsg, Wo> copier = WrapCopierFactory.wo(IMMsg.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }

    public static class IMMessageBody {
        /**
         *     text
         *     emoji
         *     image
         *     audio
         *     location
         *     file
         */
        private String type;
        private String body;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }


}
