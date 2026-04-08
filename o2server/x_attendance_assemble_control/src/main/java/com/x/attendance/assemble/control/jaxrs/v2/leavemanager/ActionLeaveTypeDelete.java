package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

public class ActionLeaveTypeDelete extends BaseAction {

    ActionResult<Wo> execute(String id) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2LeaveType type = emc.find(id, AttendanceV2LeaveType.class);
            if (type == null) {
                throw new ExceptionNotExistObject("无法找到指定ID的假期类型信息，ID：" + id);
            }
            emc.beginTransaction(AttendanceV2LeaveType.class);
            type.setActive(true);
            emc.check(type, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }
        return result;
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -3528827734002400673L;

    }
}
