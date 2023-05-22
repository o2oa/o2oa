package com.x.attendance.assemble.control.jaxrs.v2.config;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPost extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPost.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if(!business.isManager(effectivePerson)){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            AttendanceV2Config config;
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            if (list != null && !list.isEmpty()) {
                config = list.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            wi.setId(null);
            Wi.copier.copy(wi, config);
            emc.beginTransaction(AttendanceV2Config.class);
            emc.persist(config, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }


    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -6656550141287122328L;
    }

    public static class Wi extends AttendanceV2Config {

        private static final long serialVersionUID = 9207961741738023826L;
        static WrapCopier<Wi, AttendanceV2Config> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2Config.class, null,
                JpaObject.FieldsUnmodify);
    }
}
