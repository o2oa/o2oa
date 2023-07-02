package com.x.attendance.assemble.control.jaxrs.v2.config;

import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionGet extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

    ActionResult<Wo> execute() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            if (list != null && !list.isEmpty()) {
                Wo wo = Wo.copier.copy(list.get(0));
                result.setData(wo);
            } else {
                result.setData(new Wo());
            }
            return result;
        }
    }

    public static class Wo extends AttendanceV2Config {

        private static final long serialVersionUID = 7957142970493421609L;
        static WrapCopier<AttendanceV2Config,  Wo> copier = WrapCopierFactory.wo(AttendanceV2Config.class,  Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
