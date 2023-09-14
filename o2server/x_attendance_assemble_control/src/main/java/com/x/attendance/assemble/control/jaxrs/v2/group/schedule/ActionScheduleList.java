package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionScheduleList extends BaseAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionScheduleList.class);

  ActionResult<ScheduleValueWo> execute(String groupId, String month) throws Exception {
    if (StringUtils.isEmpty(groupId)) {
      throw new ExceptionEmptyParameter("groupId");
    }
    if (StringUtils.isEmpty(month)) {
      throw new ExceptionEmptyParameter("month");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("排班数据查询  groupId {} month {}", groupId, month);
    }
    ActionResult<ScheduleValueWo> result = new ActionResult<>();
    ActionScheduleListFilter.Wi filterWi = new ActionScheduleListFilter.Wi();
    filterWi.setGroupId(groupId);
    filterWi.setMonth(month);
    // 执行 ActionScheduleListFilter 查询排班数据
    ScheduleValueWo wo = ThisApplication.context().applications()
        .postQuery(x_attendance_assemble_control.class, "v2/groupschedule/list/filter", filterWi)
        .getData(ScheduleValueWo.class);
    result.setData(wo);
    return result;
  }
}
