package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;

public class ActionLeavePolicyGet extends BaseAction {

    ActionResult<Wo> execute(String id) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            AttendanceV2LeavePolicy policy = emc.find(id, AttendanceV2LeavePolicy.class);
            if (policy == null) {
                throw new ExceptionNotExistObject("无法找到指定ID的假期规则信息，ID：" + id);
            }
            Wo wo = Wo.copier.copy(policy);
            AttendanceV2LeaveType leaveType = emc.find(policy.getLeaveTypeId(), AttendanceV2LeaveType.class);
            if (leaveType != null) {
                wo.setLeaveType(leaveType);
            }
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends AttendanceV2LeavePolicy {

        private static final long serialVersionUID = -5159333621668443727L;

        static WrapCopier<AttendanceV2LeavePolicy, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeavePolicy.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("假期类型")
        private AttendanceV2LeaveType leaveType;

        public AttendanceV2LeaveType getLeaveType() {
            return leaveType;
        }

        public void setLeaveType(AttendanceV2LeaveType leaveType) {
            this.leaveType = leaveType;
        }

    }
}
