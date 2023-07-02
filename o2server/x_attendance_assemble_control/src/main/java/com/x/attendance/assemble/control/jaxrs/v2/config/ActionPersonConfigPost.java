package com.x.attendance.assemble.control.jaxrs.v2.config;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2PersonConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.List;

/**
 * Created by fancyLou on 2023/3/28.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPersonConfigPost extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPersonConfigPost.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            List<AttendanceV2PersonConfig> list = business.getAttendanceV2ManagerFactory().personConfigWithPerson(effectivePerson.getDistinguishedName());
            AttendanceV2PersonConfig config;
            if (list != null && !list.isEmpty()) {
                 config = list.get(0);
            } else {
                config = new AttendanceV2PersonConfig();
            }
            wi.setId(null);
            Wi.copier.copy(wi, config);
            config.setPerson(effectivePerson.getDistinguishedName());
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


        private static final long serialVersionUID = 8462523963830522516L;
    }

    public static class Wi extends AttendanceV2PersonConfig {

        private static final long serialVersionUID = -3249862767046264731L;
        static WrapCopier<Wi, AttendanceV2PersonConfig> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2PersonConfig.class, null,
                JpaObject.FieldsUnmodify);
    }
}
