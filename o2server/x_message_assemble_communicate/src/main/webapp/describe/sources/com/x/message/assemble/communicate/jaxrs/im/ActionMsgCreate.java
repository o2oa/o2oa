package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;

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
                logger.info("发送im消息， person: " + persons.get(i));
                MessageConnector.send(MessageConnector.TYPE_IM_CREATE,  "来自 "+ name + "的IM消息", persons.get(i), msg);
            }

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = Wo.copier.copy(msg);
            result.setData(wo);
            return result;
        }
    }



    public static class Wo extends IMMsg {

        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMMsg, Wo> copier = WrapCopierFactory.wo(IMMsg.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }


}
