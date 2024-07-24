package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

class ActionCheckMobile extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCheckMobile.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String mobile) throws Exception {

        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (com.x.base.core.project.config.Person.REGISTER_TYPE_DISABLE.equals(
                    Config.person().getRegister())) {
                throw new ExceptionDisableRegist();
            }
            if (!Config.person().isMobile(mobile)) {
                throw new ExceptionInvalidMobile(mobile);
            }
            if (this.mobileExisted(emc, mobile)) {
                throw new ExceptionMobileExist(mobile);
            }
            if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
                throw new ExceptionDisableCollect();
            }
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WrapBoolean {

    }

}
