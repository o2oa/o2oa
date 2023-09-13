package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2GroupSchedule;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionScheduleList extends BaseAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionScheduleList.class);

  ActionResult<Wo> execute(String groupId, String month) throws Exception {
    if (StringUtils.isEmpty(groupId)) {
      throw new ExceptionEmptyParameter("groupId");
    }
    if (StringUtils.isEmpty(month)) {
      throw new ExceptionEmptyParameter("month");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("排班数据查询  groupId {} month {}", groupId, month);
    }
    ActionResult<Wo> result = new ActionResult<>();
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      Business business = new Business(emc);
      List<AttendanceV2GroupSchedule> list = business.getAttendanceV2ManagerFactory().listGroupSchedule(groupId, month,
          null, null);
      Map<String, List<ScheduleWo>> scheduleValue = toMap(list, business);
      Wo wo = new Wo();
      wo.setScheduleValue(scheduleValue);
      result.setData(wo);
    }
    return result;
  }

  private Map<String, List<ScheduleWo>> toMap(List<AttendanceV2GroupSchedule> list, Business business) throws Exception {
        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }
        return list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        AttendanceV2GroupSchedule::getUserId,
                        Collectors.mapping(element -> {
                            ScheduleWo scheduleWo = ScheduleWo.copier.copy(element);
                            if (StringUtils.isNotEmpty(element.getShiftId())) {
                                try {
                                  scheduleWo.setShift(business.getAttendanceV2ManagerFactory().pick(element.getShiftId(), AttendanceV2Shift.class));
                                } catch (Exception e) {
                                  LOGGER.error(e);
                                }
                            }
                            return scheduleWo;
                        }, Collectors.toList())
                ));
    }

  public static class Wo extends GsonPropertyObject {

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
