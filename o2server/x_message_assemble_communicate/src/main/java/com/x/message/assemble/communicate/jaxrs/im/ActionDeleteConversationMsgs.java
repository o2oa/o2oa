package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class ActionDeleteConversationMsgs extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionDeleteConversationMsgs.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(conversationId)) {
            throw new ExceptionEmptyId();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wo wo = new Wo();
            Business business = new Business(emc);
            // 判断权限 群聊只有管理员能清空
            IMConversation conversation = emc.find(conversationId, IMConversation.class);
            if (conversation == null) {
                throw new ExceptionConversationNotExist();
            }
            if (conversation.getType().equals(IMConversation.CONVERSATION_TYPE_GROUP) &&
                    !effectivePerson.getDistinguishedName().equals(conversation.getAdminPerson())) {
                    throw new ExceptionConvClearMsgsNoPermission();
            }

            List<String> msgIds = business.imConversationFactory().listAllMsgIdsWithConversationId(conversationId);
            if (msgIds == null || msgIds.isEmpty()) {
                logger.info("没有聊天记录，无需清空！ conversationId:"+conversationId);
            } else {
                // 这里是1条条删除的 优化一下？
                emc.beginTransaction(IMMsg.class);
                emc.delete(IMMsg.class, msgIds);
                emc.commit();
                logger.info("成功清空聊天记录！conversationId:" + conversationId + " msg size：" + msgIds.size() + " person：" + effectivePerson.getDistinguishedName());
            }
            wo.setValue(true);
            result.setData(wo);
        }
        return result;
    }


    static class Wo extends WrapOutBoolean {

    }
}
