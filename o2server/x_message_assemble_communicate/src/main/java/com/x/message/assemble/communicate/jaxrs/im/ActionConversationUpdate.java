package com.x.message.assemble.communicate.jaxrs.im;

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_SINGLE;

import java.util.Date;
import java.util.List;

import com.x.base.core.project.message.MessageConnector;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.IMConversation;

public class ActionConversationUpdate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionConversationUpdate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getId())) {
                throw new ExceptionEmptyId();
            }
            IMConversation conversation = emc.find(wi.getId(), IMConversation.class);
            if (conversation == null) {
                throw new ExceptionConversationNotExist();
            }
            // 有成员变化的情况 所以所有老成员都要收到消息
            List<String> oldMembers = conversation.getPersonList();
            if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) {
                throw new ExceptionSingleConvNotUpdate();
            }
            if (!effectivePerson.getDistinguishedName().equals(conversation.getAdminPerson())) {
                throw new ExceptionConvUpdateNoPermission();
            }
            emc.beginTransaction(IMConversation.class);
            if (StringUtils.isNotEmpty(wi.getTitle())) {
                conversation.setTitle(wi.getTitle());
            }
            if (StringUtils.isNotEmpty(wi.getNote())) {
                conversation.setNote(wi.getNote());
            }
            if (wi.getPersonList() != null && !wi.getPersonList().isEmpty()) {
                conversation.setPersonList(wi.getPersonList());
                if (!conversation.getPersonList()
                        .contains(effectivePerson.getDistinguishedName())) {
                    List<String> list = conversation.getPersonList();
                    list.add(effectivePerson.getDistinguishedName());
                    conversation.setPersonList(list);
                }
            }
            ConversationInvokeValue value = checkConversationInvoke(effectivePerson, "update",
                    conversation.getType(), conversation.getPersonList(), oldMembers,
                    conversation.getTitle(), conversation.getNote());
            if (BooleanUtils.isFalse(value.getResult())) {
                LOGGER.warn("没有通过脚本校验, {} ", value.toString());
                throw new ExceptionConversationCheckError(
                        value.getMsg() == null ? "脚本校验不通过" : value.getMsg());
            }

            conversation.setUpdateTime(new Date());
            emc.check(conversation, CheckPersistType.all);
            emc.commit();
            // 发送消息
            sendConversationMsg(oldMembers, conversation,
                    MessageConnector.TYPE_IM_CONVERSATION_UPDATE);
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = Wo.copier.copy(conversation);
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 2252942380132685149L;

        @FieldDescribe("id")
        private String id;
        @FieldDescribe("会话标题")
        private String title;
        @FieldDescribe("会话公告")
        private String note;
        @FieldDescribe("会话对象")
        private List<String> personList;
        @FieldDescribe("会话管理员")
        private String adminPerson;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public List<String> getPersonList() {
            return personList;
        }

        public void setPersonList(List<String> personList) {
            this.personList = personList;
        }

        public String getAdminPerson() {
            return adminPerson;
        }

        public void setAdminPerson(String adminPerson) {
            this.adminPerson = adminPerson;
        }
    }

    public static class Wo extends IMConversation {

        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);
    }

}
