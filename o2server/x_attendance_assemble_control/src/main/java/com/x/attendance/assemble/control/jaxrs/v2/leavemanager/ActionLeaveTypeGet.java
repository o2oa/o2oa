package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;

public class ActionLeaveTypeGet extends BaseAction {

    ActionResult<Wo> execute(String id) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            AttendanceV2LeaveType type = emc.find(id, AttendanceV2LeaveType.class);
            if (type == null) {
                throw new ExceptionNotExistObject("无法找到指定ID的假期类型信息，ID：" + id);
            }
            Wo wo = Wo.copier.copy(type);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends AttendanceV2LeaveType {

        private static final long serialVersionUID = -5167911239345830660L;
        static WrapCopier<AttendanceV2LeaveType, Wo> copier = WrapCopierFactory.wo(
                AttendanceV2LeaveType.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);

    }

}
