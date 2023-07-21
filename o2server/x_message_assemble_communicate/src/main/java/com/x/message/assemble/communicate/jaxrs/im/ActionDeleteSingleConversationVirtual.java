package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 单聊 虚拟删除
 * 在 IMConversationExt 会话扩展中添加一个删除标识，一个最新删除日期
 * Created by fancyLou on 2022/12/1.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionDeleteSingleConversationVirtual  extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteSingleConversationVirtual.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("单聊 个人虚拟删除会话 person:{}, conversationId:{}.", effectivePerson.getDistinguishedName(), conversationId);
        }
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(conversationId)) {
            throw new ExceptionEmptyId();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            IMConversation conversation = emc.find(conversationId, IMConversation.class);
            if (conversation == null) {
                throw new ExceptionConversationNotExist();
            }
            // 这个Action操作是针对单聊的，不是单聊不给操作！
            if (!conversation.getType().equals(IMConversation.CONVERSATION_TYPE_SINGLE)) {
                throw new ExceptionConvDeleteNoPermission();
            }
            Business business = new Business(emc);
            IMConversationExt ext = business.imConversationFactory()
                    .getConversationExt(effectivePerson.getDistinguishedName(), conversationId);
            if (ext == null) {
                ext = new IMConversationExt();
                ext.setConversationId(conversationId);
                ext.setPerson(effectivePerson.getDistinguishedName());
            }
            ext.setIsDeleted(true);
            ext.setLastDeleteTime(new Date());
            emc.beginTransaction(IMConversationExt.class);
            emc.persist(ext, CheckPersistType.all);
            emc.commit();
            LOGGER.info("虚拟删除单聊成功 person {} ==============================================", effectivePerson.getDistinguishedName());
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }
        return result;
    }


    public static class Wo extends WrapOutBoolean {


        private static final long serialVersionUID = -7547394049405029386L;
    }
}
