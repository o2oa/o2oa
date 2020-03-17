package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;

import java.util.List;


public class ActionMsgListWithConversationByPage extends BaseAction {

    private final Logger logger = LoggerFactory.getLogger(ActionMsgListWithConversationByPage.class);


    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size,
                                   JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null) {
                wi = new Wi();
            }
            if (wi.getConversationId() == null || wi.getConversationId().isEmpty()) {
                throw new ExceptionMsgEmptyConversationId();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            List<IMMsg> msgList = business.imConversationFactory().listMsgWithConversationByPage(adjustPage, adjustPageSize, wi.getConversationId());
            List<Wo> wos = Wo.copier.copy(msgList);
            result.setData(wos);
            result.setCount(business.imConversationFactory().count(wi.getConversationId()));
            return result;
        }
    }

    public class Wi extends GsonPropertyObject {

        @FieldDescribe("会话id")
        private String conversationId;


        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
    }

    public static class Wo extends IMMsg {

        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMMsg, Wo> copier = WrapCopierFactory.wo(IMMsg.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
