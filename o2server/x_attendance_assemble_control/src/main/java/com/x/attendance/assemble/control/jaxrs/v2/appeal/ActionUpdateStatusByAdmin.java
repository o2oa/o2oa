package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

/**
 * 管理员修改异常数据为正常
 * 
 */
public class ActionUpdateStatusByAdmin extends BaseAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateStatusByAdmin.class);

  ActionResult<Wo> execute(EffectivePerson person, String id) throws Exception {
    if (StringUtils.isEmpty(id)) {
      throw new ExceptionEmptyParameter("id");
    }
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
      if (info == null) {
        throw new ExceptionNotExistObject("数据不存在，" + id);
      }
      emc.beginTransaction(AttendanceV2AppealInfo.class);
      info.setStatus(AttendanceV2AppealInfo.status_TYPE_END_BY_ADMIN); // 管理员处理
      info.setEndTime(DateTools.now());
      info.setUpdateStatusAdminPerson(person.getDistinguishedName()); // 处理人
      emc.check(info, CheckPersistType.all);
      emc.commit();
      AttendanceV2CheckInRecord record = emc.find(info.getRecordId(), AttendanceV2CheckInRecord.class);
      if (record != null) {
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL); // 修改为正常
        record.setAppealId(info.getId()); // 回填 id
        record.setDescription("管理员处理！"); // 描述
        emc.check(record, CheckPersistType.all);
        emc.commit();
        // 申诉成功后，重新生成对应的数据
        LOGGER.info("管理员处理成功，重新发起考勤数据生成，Date：{} person: {}", record.getRecordDateString(), record.getUserId());
        ThisApplication.queueV2Detail
            .send(new QueueAttendanceV2DetailModel(record.getUserId(), record.getRecordDateString()));
      } else {
        LOGGER.info("没有找到对应的打卡记录数据，" + info.getRecordId());
      }
    }
    ActionResult<Wo> result = new ActionResult<>();
    Wo wo = new Wo();
    wo.setValue(true);
    result.setData(wo);
    return result;
  }

  public static class Wo extends WrapBoolean {

  }
}
