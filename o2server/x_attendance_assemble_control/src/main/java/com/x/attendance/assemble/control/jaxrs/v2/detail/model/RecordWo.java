package com.x.attendance.assemble.control.jaxrs.v2.detail.model;


import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;

public class RecordWo extends AttendanceV2CheckInRecord {
  @FieldDescribe("外出请假记录")
  private AttendanceV2LeaveData leaveData;

  @FieldDescribe("申诉记录")
  private AttendanceV2AppealInfo appealData;

  public static WrapCopier<AttendanceV2CheckInRecord, RecordWo> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class,
      RecordWo.class, null,
      JpaObject.FieldsInvisible);

  public AttendanceV2LeaveData getLeaveData() {
    return leaveData;
  }

  public void setLeaveData(AttendanceV2LeaveData leaveData) {
    this.leaveData = leaveData;
  }

  public AttendanceV2AppealInfo getAppealData() {
    return appealData;
  }

  public void setAppealData(AttendanceV2AppealInfo appealData) {
    this.appealData = appealData;
  }

  

}
