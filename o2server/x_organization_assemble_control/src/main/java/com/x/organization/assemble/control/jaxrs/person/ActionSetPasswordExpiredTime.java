package com.x.organization.assemble.control.jaxrs.person;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionSetPasswordExpiredTime extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionSetPasswordExpiredTime.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String date) throws Exception {

        LOGGER.debug("execute:{}, flag:{}, date:{}.", effectivePerson, flag, date);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            // 排除xadmin
            if (Config.token().isInitialManager(flag)) {
                throw new ExceptionDenyChangeInitialManagerPassword();
            } else {
                Person o = business.person().pick(flag);
                if (null == o) {
                    throw new ExceptionPersonNotExist(flag);
                }
                o = emc.find(o.getId(), Person.class);
                if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, o)) {
                    throw new ExceptionDenyEditPerson(effectivePerson, flag);
                }
                Date expiredTime = DateTools.parse(date);
                emc.beginTransaction(Person.class);
                o.setPasswordExpiredTime(DateUtils.ceiling(expiredTime, Calendar.DAY_OF_MONTH));
                emc.check(o, CheckPersistType.all);
                emc.commit();
                CacheManager.notify(Person.class);
                Wo wo = new Wo();
                wo.setValue(true);
                result.setData(wo);
            }

            return result;
        }
    }

    
    public static class Wi extends GsonPropertyObject {

    	@FieldDescribe("到期日期.")
    	@Schema(description = "到期日期.")
        private Date passwordExpiredTime;

        public Date getPasswordExpiredTime() {
            return passwordExpiredTime;
        }

        public void setPasswordExpiredTime(Date passwordExpiredTime) {
            this.passwordExpiredTime = passwordExpiredTime;
        }

    }

    public static class Wo extends WrapBoolean {

    }
}
