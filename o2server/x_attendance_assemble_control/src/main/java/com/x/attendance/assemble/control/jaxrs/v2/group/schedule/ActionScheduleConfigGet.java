package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2GroupScheduleConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionScheduleConfigGet extends BaseAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionScheduleConfigGet.class);

  ActionResult<Wo> execute(String groupId) throws Exception {
    if (StringUtils.isEmpty(groupId)) {
      throw new ExceptionEmptyParameter("groupId");
    }
    ActionResult<Wo> result = new ActionResult<Wo>();
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      Business business = new Business(emc);
      List<AttendanceV2GroupScheduleConfig> configs = business.getAttendanceV2ManagerFactory()
          .listGroupScheduleConfig(groupId);
      AttendanceV2GroupScheduleConfig config;
      if (configs != null && !configs.isEmpty()) {
        config = configs.get(0);
      } else {
        config = new AttendanceV2GroupScheduleConfig();
      }
      Wo wo = Wo.copier.copy(config);
      result.setData(wo);
    }
    return result;
  }

  public static class Wo extends AttendanceV2GroupScheduleConfig {

    private static final long serialVersionUID = 1L;

    static WrapCopier<AttendanceV2GroupScheduleConfig, Wo> copier = WrapCopierFactory.wo(
        AttendanceV2GroupScheduleConfig.class, Wo.class, null,
        JpaObject.FieldsInvisible);
  }
}
