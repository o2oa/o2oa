package com.x.attendance.assemble.control.jaxrs.v2.detail.model;

import java.util.List;

import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;

public class DetailWo extends AttendanceV2Detail {
  public static WrapCopier<AttendanceV2Detail, DetailWo> copier = WrapCopierFactory.wo(AttendanceV2Detail.class,
      DetailWo.class, null,
      JpaObject.FieldsInvisible);

  @FieldDescribe("打卡记录")
  private List<RecordWo> recordList;

  public List<RecordWo> getRecordList() {
    return recordList;
  }

  public void setRecordList(List<RecordWo> recordList) {
    this.recordList = recordList;
  }

}
