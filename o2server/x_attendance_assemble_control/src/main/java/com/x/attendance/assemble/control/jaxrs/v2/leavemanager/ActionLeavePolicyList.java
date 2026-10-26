package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.List;

import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionLeavePolicyList extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeavePolicyList.class);

    ActionResult<List<Wo>> execute(String typeId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            List<AttendanceV2LeaveType> typeList = emc.listAll(AttendanceV2LeaveType.class);
            List<Wo> wos = emc.listEqual(AttendanceV2LeavePolicy.class, AttendanceV2LeavePolicy.leaveTypeId_FIELDNAME, typeId)
                    .stream().map(policy -> {
                        Wo wo = Wo.copier.copy(policy);
                        typeList.stream()
                                .filter(type -> type.getId().equals(policy.getLeaveTypeId()))
                                .findFirst().ifPresent(wo::setLeaveType);
                        return wo;
                    }).collect(java.util.stream.Collectors.toList());
            result.setData(wos);
            return result;
        }
    }

    public static class Wo extends AttendanceV2LeavePolicy {
 

        private static final long serialVersionUID = -2972497235792721018L;

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
