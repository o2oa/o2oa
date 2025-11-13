package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionConversationMemberQuitSelf extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionConversationMemberQuitSelf.class);

    ActionResult<Wo> execute(
            EffectivePerson effectivePerson, String conversationId) throws Exception {
        String me = effectivePerson.getDistinguishedName();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}.", me);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (StringUtils.isEmpty(conversationId)) {
                throw new ExceptionEmptyId();
            }
            IMConversation conversation = emc.find(conversationId, IMConversation.class);
            if (conversation == null) {
                throw new ExceptionConversationNotExist();
            }
            ActionResult<ActionImConfig.Wo> config = new ActionImConfig().execute(effectivePerson);
            // 没有配置 或者 没有开启撤回
            if (config == null || !BooleanUtils.isTrue(config.getData().getEnableGroupMemberQuitSelf())) {
                throw new ExceptionConversationQuitError("配置未开启。");
            }
            // 不能退出自己创建的会话
            if (me.equals(conversation.getAdminPerson())) {
                throw new ExceptionConversationQuitError("不能退出自己创建的会话。");
            }
            // 原成员列表
            List<String> oldMembers = conversation.getPersonList();
            if (!oldMembers.contains(me)) {
                throw new ExceptionConversationQuitError("您不在会话成员列表中，不能退出会话。");
            }
            emc.beginTransaction(IMConversation.class);
            // 退出会话
            oldMembers.remove(me);
            conversation.setPersonList(oldMembers);
            conversation.setUpdateTime(new Date());
            emc.check(conversation, CheckPersistType.all);
            emc.commit();
            // 删除会话扩展信息
            Business business = new Business(emc);
            IMConversationExt ext = business.imConversationFactory().getConversationExt(me, conversation.getId());
            if (ext != null) {
                emc.beginTransaction(IMConversationExt.class);
                emc.delete(IMConversationExt.class, ext.getId());
                emc.commit();
            }
            // 重新生成会话图标
            generateConversationIcon(conversation.getId());
            // 发送消息
            sendConversationMsg(oldMembers, conversation,
                    MessageConnector.TYPE_IM_CONVERSATION_UPDATE);
            Wo wo = new Wo();
            wo.setValue(true);
            ActionResult<Wo> result = new ActionResult<>();
            result.setData(wo);
            return result;
        }

    }

    public static class Wo extends WrapOutBoolean {

        private static final long serialVersionUID = -7676752582998670730L;
    }


}
