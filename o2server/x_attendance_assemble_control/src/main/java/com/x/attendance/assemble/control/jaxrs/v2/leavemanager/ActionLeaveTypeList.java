package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.List;
import java.util.stream.Collectors;

import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionLeaveTypeList extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeaveTypeList.class);

    ActionResult<List<Wo>> execute() throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        List<AttendanceV2LeaveType> types = getLeaveTypeList(null);
        List<Wo> wos = types.stream().map(type -> {
            Wo wo = Wo.copier.copy(type);
            return wo;
        }).collect(Collectors.toList());
        result.setData(wos);
        return result;
    }

    public static class Wo extends AttendanceV2LeaveType {

        private static final long serialVersionUID = -5167911239345830660L;
        static WrapCopier<AttendanceV2LeaveType, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveType.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);

    }
}
