package com.x.attendance.assemble.control.jaxrs.v2.my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public class ActionDateIsRestDay extends BaseAction {

  private static final Logger logger = LoggerFactory.getLogger(ActionDateIsRestDay.class);

  ActionResult<Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
    ActionResult<Wo> result = new ActionResult<>();
    List<String> restDateList = new ArrayList<>();
    Wo wo = new Wo();
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      Business business = new Business(emc);
      Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
      if (wi.getDateList() == null || wi.getDateList().isEmpty()) {
        logger.info("DateList 是空的！");
        wo.setRestDateList(restDateList);
        result.setData(wo);
        return result;
      }
      // 人员查询所属考勤组
      List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory()
          .listGroupWithPerson(person.getDistinguishedName());
      if (groups == null || groups.isEmpty()) {
        logger.info("没有考勤组！");
        wo.setRestDateList(emptyListGetSatAndSun(wi.getDateList()));
        result.setData(wo);
        return result;
      }

      for (String date : wi.getDateList()) {
        WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(person.getDistinguishedName(), date);
        AttendanceV2Group group = woGroupShift.getGroup();
        if (group == null) {
          continue;
        }
        AttendanceV2Shift shift = woGroupShift.getShift();
        // 日期
        Date d = null;
        try {
          d = DateTools.parse(date, DateTools.format_yyyyMMdd);
        } catch (Exception e) {
          continue;
        }
        if (logger.isDebugEnabled()) {
          logger.debug("date: " + date);
        }
        // 周几 0-6 代表 星期天 - 星期六
        int day = DateTools.dayForWeekAttendanceV2(d);
        if (logger.isDebugEnabled()) {
          logger.debug("day: " + day);
        }
        // 是否工作日
        boolean isWorkDay = false;
        if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Free)) { // 自由工时
          if (!isWorkDay) { // 如果已经是工作日 下面不需要判断了
            // 判断休息日还是工作日
            if (StringUtils.isEmpty(group.getWorkDateList())) {
              continue;
            }
            String[] workDayList = group.getWorkDateList().split(",");
            List<Integer> dayList = Arrays.stream(workDayList).map(Integer::parseInt).collect(Collectors.toList());
            // 是否工作日
            isWorkDay = dayList.contains(day);
          }
          // 特殊节假日
          if (isWorkDay && AttendanceV2Helper.isSpecialRestDay(date, group)) {
            isWorkDay = false;
          }
        } else {
          if (shift != null) {
            isWorkDay = true;
          }
        }

        if (!isWorkDay) {
          restDateList.add(date);
        }
      }
      wo.setRestDateList(restDateList);
      result.setData(wo);
      if (logger.isDebugEnabled()) {
        logger.debug("restDateList: " + ListTools.toStringJoin(restDateList));
      }
      return result;
    }
  }

  private List<String> emptyListGetSatAndSun(List<String> dateList) throws Exception {
    List<String> restDateList = new ArrayList<>();
    for (String date : dateList) {
      Date d = null;
      try {
        d = DateTools.parse(date, DateTools.format_yyyyMMdd);
      } catch (Exception e) {
        continue;
      }
      if (logger.isDebugEnabled()) {
        logger.debug("date: " + date);
      }
      // 周几 0-6 代表 星期天 - 星期六
      int day = DateTools.dayForWeekAttendanceV2(d);
      if (logger.isDebugEnabled()) {
        logger.debug("day: " + day);
      }
      if (day == 0 || day == 6) {
        restDateList.add(date);
      }
    }
    return restDateList;
  }

  public static class Wi extends GsonPropertyObject {

    private static final long serialVersionUID = 7874429260551956123L;
    
    @FieldDescribe("查询是否休息日的日期列表，yyyy-MM-dd")
    private List<String> dateList;

    public List<String> getDateList() {
      return dateList;
    }

    public void setDateList(List<String> dateList) {
      this.dateList = dateList;
    }

  }

  public static class Wo extends GsonPropertyObject {
    private static final long serialVersionUID = -6596581682844590658L;
    
    @FieldDescribe("休息日的日期列表，yyyy-MM-dd")
    private List<String> restDateList;

    public List<String> getRestDateList() {
      return restDateList;
    }

    public void setRestDateList(List<String> restDateList) {
      this.restDateList = restDateList;
    }

  }

}
