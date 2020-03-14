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
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;

import java.util.List;

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_GROUP;
import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_SINGLE;


public class ActionConversationCreate extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionConversationCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)  throws Exception {

        logger.debug("receive{}.", jsonElement);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            IMConversation conversation = this.convertToWrapIn(jsonElement, IMConversation.class);
            if (conversation.getType() == null || conversation.getType().isEmpty() ||
                    !(conversation.getType().equals(CONVERSATION_TYPE_SINGLE) || conversation.getType().equals(CONVERSATION_TYPE_GROUP))) {
                throw new ExceptionConversationTypeError();
            }
            if (conversation.getPersonList() == null || conversation.getPersonList().isEmpty()) {
                throw new ExceptionEmptyMember();
            }
            if (conversation.getType().equals(CONVERSATION_TYPE_GROUP) && conversation.getPersonList().size() < 2) {
                throw new ExceptionGroupConversationEmptyMember();
            }
            if (!conversation.getPersonList().contains(effectivePerson.getDistinguishedName())) {
                List<String> list = conversation.getPersonList();
                list.add(effectivePerson.getDistinguishedName());
                conversation.setPersonList(list);
            }
            //单聊 判断会话是否存在
            if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) {
                Business business = new Business(emc);
                List<String> ids = business.imConversationFactory().listConversationWithPerson(effectivePerson.getDistinguishedName());
                List<IMConversation> list = emc.list(IMConversation.class, ids);
                if (list != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        IMConversation c = list.get(i);
                        if (ListTools.isSameList(c.getPersonList(), conversation.getPersonList())) {
                            ActionResult<Wo> result = new ActionResult<>();
                            Wo wo = Wo.copier.copy(c);
                            result.setData(wo);
                            return result;
                        }
                    }
                }
            }

            if (conversation.getTitle() == null || conversation.getTitle().isEmpty()) {
                String title = "";
                if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) {
                    for (int i = 0; i < conversation.getPersonList().size(); i++) {
                        String person = conversation.getPersonList().get(i);
                        if (!effectivePerson.getDistinguishedName().equals(person)) {
                            title = person.substring(0, person.indexOf("@"));
                            break;
                        }
                    }
                }else {
                    for (int i = 0; i < conversation.getPersonList().size(); i++) {
                        String person = conversation.getPersonList().get(i);
                        if (i > 3) {
                            title += person.substring(0, person.indexOf("@"));
                            title += "...";
                        }else {
                            title += person.substring(0, person.indexOf("@")) + "、";
                        }
                    }
                }
                conversation.setTitle(title);
            }
            emc.beginTransaction(IMConversation.class);
            emc.persist(conversation, CheckPersistType.all);
            emc.commit();

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = Wo.copier.copy(conversation);
            result.setData(wo);
            return result;
        }
    }



    public static class Wo extends IMConversation {

        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }


}
