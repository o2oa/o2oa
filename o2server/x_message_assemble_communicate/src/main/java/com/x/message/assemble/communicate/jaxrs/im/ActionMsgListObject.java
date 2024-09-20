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
import com.x.message.core.entity.IMMsg;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ActionMsgListObject extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMsgListObject.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {

        LOGGER.debug("execute ActionMsgListObject :{}.", effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null || wi.getMsgIdList() == null || wi.getMsgIdList().isEmpty()) {
                throw new ExceptionEmptyId();
            }
            List<IMMsg> msgList = business.imConversationFactory().listMsgObject(wi.getMsgIdList());
            List<Wo> wos = Wo.copier.copy(msgList);
            for (Wo wo : wos) {
                if (StringUtils.isNotEmpty(wo.getQuoteMessageId())) {
                    IMMsg quoteMessage = emc.find(wo.getQuoteMessageId(), IMMsg.class);
                    if (quoteMessage != null) {
                        wo.setQuoteMessage(quoteMessage);
                    }
                }
            }
            result.setData(
                    wos.stream().sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                            .collect(Collectors.toList()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = 3649112474402515842L;
        @FieldDescribe("消息id列表")
        private List<String> msgIdList;

        public List<String> getMsgIdList() {
            return msgIdList;
        }

        public void setMsgIdList(List<String> msgIdList) {
            this.msgIdList = msgIdList;
        }
    }

    public static class Wo extends IMMsg {


        private static final long serialVersionUID = -4500552602639326831L;
        static WrapCopier<IMMsg, Wo> copier = WrapCopierFactory.wo(IMMsg.class, Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("引用消息.")
        private IMMsg quoteMessage;

        public IMMsg getQuoteMessage() {
            return quoteMessage;
        }

        public void setQuoteMessage(IMMsg quoteMessage) {
            this.quoteMessage = quoteMessage;
        }
    }
}
