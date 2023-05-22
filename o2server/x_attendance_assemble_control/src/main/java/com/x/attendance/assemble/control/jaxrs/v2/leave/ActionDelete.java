package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;

/**
 * Created by fancyLou on 2023/4/25.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionDelete extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            AttendanceV2LeaveData leaveData = emc.find(id, AttendanceV2LeaveData.class);
            if (leaveData == null) {
                throw new ExceptionNotExistObject("请假数据"+ id);
            }
            ActionResult<Wo> result = new ActionResult<>();
            emc.beginTransaction(AttendanceV2LeaveData.class);
            emc.delete(AttendanceV2LeaveData.class, leaveData.getId());
            emc.commit();
            Wo wo = new  Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }


    public static class Wo extends WrapOutBoolean {
        private static final long serialVersionUID = -317066765555169582L;
    }
}
