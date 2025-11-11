package com.x.message.assemble.communicate.jaxrs.im;

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_GROUP;
import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_SINGLE;

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
import com.x.message.core.entity.IMConversationExt;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionConversationCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionConversationCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            IMConversation conversation = this.convertToWrapIn(jsonElement, IMConversation.class);
            if (conversation.getType() == null || conversation.getType().isEmpty()
                || !(conversation.getType().equals(CONVERSATION_TYPE_SINGLE)
                     || conversation.getType().equals(CONVERSATION_TYPE_GROUP))) {
                throw new ExceptionConversationTypeError();
            }
            if (conversation.getPersonList() == null) {
                conversation.setPersonList(new ArrayList<>());
            }
            if (!conversation.getPersonList().contains(effectivePerson.getDistinguishedName())) {
                List<String> list = conversation.getPersonList();
                list.add(effectivePerson.getDistinguishedName());
                conversation.setPersonList(list);
            }
            // 成员数量判断
            if ((conversation.getType().equals(CONVERSATION_TYPE_SINGLE)
                 && conversation.getPersonList().size() != 2)) {
                throw new ExceptionGroupConversationEmptyMember();
            }
            // 如果有业务 id 就不限制成员人数
            if (conversation.getType().equals(CONVERSATION_TYPE_GROUP) && StringUtils.isEmpty(conversation.getBusinessId()) && conversation.getPersonList().size() < 3) {
                throw new ExceptionGroupConversationEmptyMember();
            }

            // 单聊 判断会话是否存在
            if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) {
                Business business = new Business(emc);
                List<IMConversation> list = business.imConversationFactory()
                        .listConversationWithPerson2(effectivePerson.getDistinguishedName());
                if (list != null && !list.isEmpty()) {
                    for (IMConversation c : list) {
                        if (ListTools.isSameList(c.getPersonList(), conversation.getPersonList())) {
                            ActionResult<Wo> result = new ActionResult<>();
                            Wo wo = Wo.copier.copy(c);
                            result.setData(wo);
                            return result;
                        }
                    }
                }
            }
            // 群聊添加管理员
            if (conversation.getType().equals(CONVERSATION_TYPE_GROUP)) {
                conversation.setAdminPerson(effectivePerson.getDistinguishedName());
            }
            // 处理标题
            if (StringUtils.isEmpty(conversation.getTitle())) {
                String title = "";
                if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) {
                    for (int i = 0; i < conversation.getPersonList().size(); i++) {
                        String person = conversation.getPersonList().get(i);
                        if (!effectivePerson.getDistinguishedName().equals(person)) {
                            title = person.substring(0, person.indexOf("@"));
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < conversation.getPersonList().size(); i++) {
                        String person = conversation.getPersonList().get(i);
                        if (i == 3) {
                            title += person.substring(0, person.indexOf("@")) + "...";
                            break;
                        } else {
                            title += person.substring(0, person.indexOf("@")) + "、";
                        }
                    }
                    if (title.endsWith("、")) {
                        title = title.substring(0, title.length() - 1);
                    }
                }
                conversation.setTitle(title);
            }

            // 如果配置了脚本了 需要进行校验 成功才能创建会话
            ConversationInvokeValue value = checkConversationInvoke(effectivePerson, "create",
                    conversation.getType(), conversation.getPersonList(), null, null, null);
            if (BooleanUtils.isFalse(value.getResult())) {
                LOGGER.warn("没有通过脚本校验, {} ", value.toString());
                throw new ExceptionConversationCheckError(
                        value.getMsg() == null ? "脚本校验不通过" : value.getMsg());
            }

            emc.beginTransaction(IMConversation.class);
            emc.persist(conversation, CheckPersistType.all);
            emc.commit();
            // 生成 icon
            generateConversationIcon(conversation.getId());
            // 必须同时创建 IMConversationExt
            for (int i = 0; i < conversation.getPersonList().size(); i++) {
                String person = conversation.getPersonList().get(i);
                IMConversationExt conversationExt = new IMConversationExt();
                conversationExt.setConversationId(conversation.getId());
                conversationExt.setPerson(person);
                conversationExt.setLastDeleteTime(new Date());
                emc.beginTransaction(IMConversationExt.class);
                emc.persist(conversationExt, CheckPersistType.all);
                emc.commit();
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = Wo.copier.copy(conversation);
            result.setData(wo);
            return result;
        }
    }


    public static class Wo extends IMConversation {

        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);
    }

}
