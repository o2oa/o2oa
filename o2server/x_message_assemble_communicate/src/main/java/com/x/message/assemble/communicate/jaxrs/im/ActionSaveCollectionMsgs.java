package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMMsgCollection;
import java.util.List;

public class ActionSaveCollectionMsgs extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionSaveCollectionMsgs.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        LOGGER.debug("execute ActionSaveCollectionMsgs :{}.",
                effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null || wi.getMsgIdList() == null || wi.getMsgIdList().isEmpty()) {
                throw new ExceptionEmptyId();
            }

            for (String id : wi.getMsgIdList()) {
                List<IMMsgCollection> msgList = business.imConversationFactory()
                        .listCollectionByPersonAndMsgId(effectivePerson.getDistinguishedName(), id);
                if (msgList != null && !msgList.isEmpty()) {
                    continue;
                }
                emc.beginTransaction(IMMsgCollection.class);
                IMMsgCollection msgCollection = new IMMsgCollection();
                msgCollection.setCreatePerson(effectivePerson.getDistinguishedName());
                msgCollection.setMessageId(id);
                emc.persist(msgCollection, CheckPersistType.all);
                emc.commit();
            }
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
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

    public static class Wo extends WrapOutBoolean {

    }
}
