package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMMsg;

import java.util.List;


public class ActionMyConversationList extends BaseAction {

    private final Logger logger = LoggerFactory.getLogger(ActionMyConversationList.class);


    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            List<String> ids = business.imConversationFactory().listConversationWithPerson(effectivePerson.getDistinguishedName());
            List<Wo> wos = Wo.copier.copy(emc.list(IMConversation.class, ids));
            for (Wo wo : wos) {
                IMConversationExt ext = business.imConversationFactory()
                        .getConversationExt(effectivePerson.getDistinguishedName(), wo.getId());
                if (ext != null) {
                    wo.setIsTop(ext.getIsTop());
                    wo.setUnreadNumber(business.imConversationFactory().unreadNumber(ext));
                }
                wo.setLastMessage(WoMsg.copier.copy(business.imConversationFactory().lastMessage(wo.getId())));
            }
            result.setData(wos);
            return result;
        }
    }

    public static class Wo extends IMConversation {

        @FieldDescribe( "是否置顶." )
        private Boolean isTop = false;

        @FieldDescribe( "未读数量." )
        private Long unreadNumber;

        @FieldDescribe( "最后一条消息." )
        private WoMsg lastMessage;


        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class, Wo.class, null,
                JpaObject.FieldsInvisible);

        public Boolean getIsTop() {
            return isTop;
        }

        public void setIsTop(Boolean isTop) {
            this.isTop = isTop;
        }

        public Long getUnreadNumber() {
            return unreadNumber;
        }

        public void setUnreadNumber(Long unreadNumber) {
            this.unreadNumber = unreadNumber;
        }

        public WoMsg getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(WoMsg lastMessage) {
            this.lastMessage = lastMessage;
        }
    }

    public static class WoMsg extends IMMsg {
        private static final long serialVersionUID = 5910475322522970446L;
        static WrapCopier<IMMsg, WoMsg> copier = WrapCopierFactory.wo(IMMsg.class, WoMsg.class, null,
                JpaObject.FieldsInvisible);
    }
}
