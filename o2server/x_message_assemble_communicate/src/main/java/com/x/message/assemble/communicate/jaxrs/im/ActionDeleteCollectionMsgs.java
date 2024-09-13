package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.IMMsgCollection;
import java.util.List;

public class ActionDeleteCollectionMsgs extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteCollectionMsgs.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        LOGGER.debug("execute ActionDeleteCollectionMsgs :{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null || wi.getMsgIdList() == null || wi.getMsgIdList().isEmpty()) {
                throw new ExceptionEmptyId();
            }
            for (String id : wi.getMsgIdList()) {
                IMMsgCollection msgCollection =  emc.find(id, IMMsgCollection.class);
                emc.beginTransaction(IMMsgCollection.class);
                emc.remove(msgCollection);
                emc.commit();
            }
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }


    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 112857560004007558L;

        @FieldDescribe("收藏消息id列表")
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
