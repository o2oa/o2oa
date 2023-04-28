package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/3/9.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionGet extends BaseAction {


    ActionResult<Wo> execute(String id) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        ActionResult<Wo> result = new  ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
            if (info == null) {
                throw new ExceptionNotExistObject(id);
            }
            Wo wo = Wo.copier.copy(info);
            result.setData(wo);
            return result;
        }
    }


    public static class Wo extends AttendanceV2AppealInfo {
        static WrapCopier<AttendanceV2AppealInfo, Wo> copier = WrapCopierFactory.wo(AttendanceV2AppealInfo.class,   Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
