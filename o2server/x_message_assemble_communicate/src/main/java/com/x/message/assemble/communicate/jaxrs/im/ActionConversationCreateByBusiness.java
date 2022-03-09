package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Application;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMConversation;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2022/3/7.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionConversationCreateByBusiness extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionConversationCreateByBusiness.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String businessId, String businessType)  throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        if (StringUtils.isEmpty(businessId)) {
            throw new ExceptionEmptyId();
        }
        if (StringUtils.isEmpty(businessType) || !(businessType.equals(IMConversation.CONVERSATION_BUSINESS_TYPE_PROCESS) || businessType.equals(IMConversation.CONVERSATION_BUSINESS_TYPE_CMS))) {
            throw new ExceptionEmptyBusinessType();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<IMConversation> list = business.imConversationFactory().listConversationWithPersonAndBusinessId(effectivePerson.getDistinguishedName(), businessId);
            if (list != null && !list.isEmpty()) {
                // 存在 返回结果
                List<Wo> wos = Wo.copier.copy(list);
                result.setData(wos);
            } else {
                // 不存在 创建
                if (businessType.equals(IMConversation.CONVERSATION_BUSINESS_TYPE_PROCESS)) { //流程

                }
            }
        }

        return result;
    }



    public static class Wo extends IMConversation {

        private static final long serialVersionUID = 5379640808709337246L;
        static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }


}
