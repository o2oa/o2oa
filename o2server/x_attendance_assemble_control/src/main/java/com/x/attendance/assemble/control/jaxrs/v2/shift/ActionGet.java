package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/3/22.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionGet extends BaseAction {

    ActionResult<Wo> execute(String id) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            AttendanceV2Shift shift = business.getAttendanceV2ManagerFactory().pick(id, AttendanceV2Shift.class);
            if (shift == null) {
                throw new ExceptionNotExistObject("班次:" + id);
            }
            result.setData( Wo.copier.copy(shift));
        }
        return result;
    }


    public static class Wo extends AttendanceV2Shift {


        private static final long serialVersionUID = -2730467641768813251L;
        static WrapCopier<AttendanceV2Shift, Wo> copier = WrapCopierFactory.wo(AttendanceV2Shift.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
