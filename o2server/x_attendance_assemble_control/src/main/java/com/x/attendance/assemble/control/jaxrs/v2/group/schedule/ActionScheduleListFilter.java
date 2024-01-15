package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2GroupSchedule;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionScheduleListFilter extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionScheduleListFilter.class);

    ActionResult<List<ScheduleWo>> execute(JsonElement jsonElement) throws Exception {
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getGroupId())) {
            throw new ExceptionEmptyParameter("groupId");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("排班数据查询  groupId {} month {} date {} person {}", wi.getGroupId(), wi.getGroupId(), wi.getDate(), wi.getPerson());
        }
        if (StringUtils.isNotEmpty(wi.getMonth())) {
            if (!AttendanceV2Helper.isValidMonthString(wi.getMonth())) {
                throw new ExceptionWithMessage("月份格式不正确！");
            }
        }
        if (StringUtils.isNotEmpty(wi.getDate())) {
            if (!AttendanceV2Helper.isValidDateString(wi.getDate())) {
                throw new ExceptionWithMessage("日期格式不正确！");
            }
        }
        ActionResult<List<ScheduleWo>> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<AttendanceV2GroupSchedule> list = business.getAttendanceV2ManagerFactory().listGroupSchedule(wi.getGroupId(), wi.getMonth(),
                    wi.getDate(), wi.getPerson());
            result.setData(tWos(list, business));
        }
        return result;
    }

    private List<ScheduleWo> tWos(List<AttendanceV2GroupSchedule> list, Business business) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().filter(Objects::nonNull)
                .map(element -> {
                    ScheduleWo scheduleWo = ScheduleWo.copier.copy(element);
                    if (StringUtils.isNotEmpty(element.getShiftId())) {
                        try {
                            scheduleWo.setShift(business.getAttendanceV2ManagerFactory().pick(element.getShiftId(), AttendanceV2Shift.class));
                        } catch (Exception e) {
                            LOGGER.error(e);
                        }
                    }
                    return scheduleWo;
                }).collect(Collectors.toList());
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

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 4244599472824062438L;

        @FieldDescribe("考勤组 id")
        private String groupId;

        @FieldDescribe("月份: yyyy-MM")
        private String month;

        @FieldDescribe("日期: yyyy-MM-dd")
        private String date;
        @FieldDescribe("人员的全称")
        private String person;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }


    }
}
