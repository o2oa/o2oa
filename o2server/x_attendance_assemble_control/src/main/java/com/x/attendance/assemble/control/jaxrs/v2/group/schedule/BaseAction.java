package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import java.util.List;
import java.util.Map;

import com.x.attendance.entity.v2.AttendanceV2GroupSchedule;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {





  public static class ScheduleValueWo extends GsonPropertyObject {

    private static final long serialVersionUID = 4498053097789932291L;
    @FieldDescribe("排班数据，key 是人员 DN， value 是AttendanceV2GroupSchedule 对象列表.")
    private Map<String, List<ScheduleWo>> scheduleValue;

    public Map<String, List<ScheduleWo>> getScheduleValue() {
      return scheduleValue;
    }

    public void setScheduleValue(Map<String, List<ScheduleWo>> scheduleValue) {
      this.scheduleValue = scheduleValue;
    }

  }

  public static class ScheduleWo extends AttendanceV2GroupSchedule {

    private static final long serialVersionUID = 7669884231289610482L;

    static WrapCopier<AttendanceV2GroupSchedule, ScheduleWo> copier = WrapCopierFactory.wo(
        AttendanceV2GroupSchedule.class, ScheduleWo.class, null,
        JpaObject.FieldsInvisible);

    @FieldDescribe("考勤组的班次对象")
    private AttendanceV2Shift shift;

    public AttendanceV2Shift getShift() {
      return shift;
    }

    public void setShift(AttendanceV2Shift shift) {
      this.shift = shift;
    }

    
  }
}
