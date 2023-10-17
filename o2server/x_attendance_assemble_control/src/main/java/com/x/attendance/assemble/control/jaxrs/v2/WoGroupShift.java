package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WoGroupShift extends GsonPropertyObject {

  private static final long serialVersionUID = -3655619888886551679L;

  @FieldDescribe("考勤组对象")
  private WoGroup group;

  @FieldDescribe("班次对象")
  private WoShift shift;

  public WoGroup getGroup() {
    return group;
  }

  public void setGroup(WoGroup group) {
    this.group = group;
  }

  public WoShift getShift() {
    return shift;
  }

  public void setShift(WoShift shift) {
    this.shift = shift;
  }

  public static class WoGroup extends AttendanceV2Group {

    private static final long serialVersionUID = 54051506565729886L;
    public static WrapCopier<AttendanceV2Group, WoGroup> copier = WrapCopierFactory.wo(AttendanceV2Group.class,
        WoGroup.class,
        null,
        JpaObject.FieldsInvisible);

  }

  public static class WoShift extends AttendanceV2Shift {

    private static final long serialVersionUID = 8234593798173782305L;

    public static WrapCopier<AttendanceV2Shift, WoShift> copier = WrapCopierFactory.wo(AttendanceV2Shift.class,
        WoShift.class,
        null,
        JpaObject.FieldsInvisible);
  }
}
