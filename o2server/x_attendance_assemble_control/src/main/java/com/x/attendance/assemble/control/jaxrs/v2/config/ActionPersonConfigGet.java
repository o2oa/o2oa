package com.x.attendance.assemble.control.jaxrs.v2.config;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Group_;
import com.x.attendance.entity.v2.AttendanceV2PersonConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPersonConfigGet extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPersonConfigGet.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            List<AttendanceV2PersonConfig> list = business.getAttendanceV2ManagerFactory().personConfigWithPerson(effectivePerson.getDistinguishedName());
            if (list != null && !list.isEmpty()) {
                Wo wo = Wo.copier.copy(list.get(0));
                result.setData(wo);
            } else {
                result.setData(new Wo());
            }
            return result;
        }
    }

    public static class Wo extends AttendanceV2PersonConfig {


        private static final long serialVersionUID = 2595194988386700618L;
        static WrapCopier<AttendanceV2PersonConfig,  Wo> copier = WrapCopierFactory.wo(AttendanceV2PersonConfig.class,  Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
