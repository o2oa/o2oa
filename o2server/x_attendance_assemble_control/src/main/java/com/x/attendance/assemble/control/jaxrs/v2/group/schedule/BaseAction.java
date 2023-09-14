package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.attendance.entity.v2.AttendanceV2GroupSchedule;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.DateTools;

abstract class BaseAction extends StandardJaxrsAction {


  /**
   * 字符串是不是 月份格式yyyy-MM
   * @param dateString
   * @return
   */
  protected boolean isValidMonthString(String dateString) {
    // 正则表达式用于检查格式是否匹配
    Pattern pattern = Pattern.compile("\\d{4}-\\d{2}");
    Matcher matcher = pattern.matcher(dateString);
    if (!matcher.matches()) {
      return false; // 格式不匹配
    }
    SimpleDateFormat sdf = new SimpleDateFormat(DateTools.format_yyyyMM);
    sdf.setLenient(false);
    try {
      // 使用SimpleDateFormat尝试解析日期
      sdf.parse(dateString);
      return true; // 解析成功，日期格式正确
    } catch (ParseException e) {
      return false; // 解析失败，日期格式不正确
    }
  }

  /**
   * 字符串是不是 日期格式  yyyy-MM-dd
   * @param dateString
   * @return
   */
  protected boolean isValidDateString(String dateString) {
    // 正则表达式用于检查格式是否匹配
    Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    Matcher matcher = pattern.matcher(dateString);
    if (!matcher.matches()) {
      return false; // 格式不匹配
    }
    SimpleDateFormat sdf = new SimpleDateFormat(DateTools.format_yyyyMMdd);
    sdf.setLenient(false);
    try {
      // 使用SimpleDateFormat尝试解析日期
      sdf.parse(dateString);
      return true; // 解析成功，日期格式正确
    } catch (ParseException e) {
      return false; // 解析失败，日期格式不正确
    }
  }



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
