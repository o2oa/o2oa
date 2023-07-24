package com.x.attendance.assemble.control.jaxrs.qywx;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.dingding.BaseAction;
import com.x.attendance.entity.AttendanceQywxDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.BetweenTerms;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionListQywxAttendanceDetail extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListQywxAttendanceDetail.class);

    public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag, Integer count, JsonElement jsonElement) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (business.isManager(effectivePerson)) {
                Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
                if (StringUtils.isEmpty(wi.getYear())) {
                    throw new ExceptionTimeEmpty();
                }
                if (StringUtils.isEmpty(wi.getPerson()) && StringUtils.isEmpty(wi.getUnit()) && StringUtils.isEmpty(wi.getTopUnit())) {
                    throw new ExceptionSearchArgEmpty();
                }
                Date startDay;
                Date endDay;
                if (StringUtils.isEmpty(wi.getMonth())) {
                    startDay = getDay(wi.getYear(), "1", "1");
                    endDay = getDay(wi.getYear(), "12", "31");
                } else {
                    if (StringUtils.isEmpty(wi.getDay())) {
                        startDay = getDay(wi.getYear(), wi.getMonth(), "1");
                        endDay = getMonthLastDay(wi.getYear(), wi.getMonth());
                    } else {
                        startDay = getDay(wi.getYear(), wi.getMonth(), wi.getDay());
                        endDay = getEndDay(wi.getYear(), wi.getMonth(), wi.getDay());
                    }
                }
                BetweenTerms betweenTerms = new BetweenTerms();
                betweenTerms.put("checkin_time_date", ListTools.toList(startDay, endDay));
                String id = EMPTY_SYMBOL;
                /** 如果不是空位标志位 */
                if (!StringUtils.equals(EMPTY_SYMBOL, flag)) {
                    id = flag;
                }
                if (StringUtils.isNotEmpty(wi.getPerson())) {
                    EqualsTerms equals = new EqualsTerms();
                    equals.put("o2User", wi.getPerson());
                    if (isCheckTypeEnable(wi.getCheckType())) {
                        equals.put("checkin_type", wi.getCheckType());
                    }
                    if (isExceptionTypeEnable(wi.getExceptionType())) {
                        equals.put("exception_type", wi.getExceptionType());
                    }
                    result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null,
                            null, null, null, null, null, betweenTerms, true, DESC);
                }
                if (StringUtils.isNotEmpty(wi.getUnit())) {
                    EqualsTerms equals = new EqualsTerms();
                    equals.put("o2Unit", wi.getUnit());
                    if (isCheckTypeEnable(wi.getCheckType())) {
                        equals.put("checkin_type", wi.getCheckType());
                    }
                    if (isExceptionTypeEnable(wi.getExceptionType())) {
                        equals.put("exception_type", wi.getExceptionType());
                    }
                    result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null,
                            null, null, null, null, null, betweenTerms, true, DESC);
                }
                if (StringUtils.isNotEmpty(wi.getTopUnit())) {
                    EqualsTerms equals = new EqualsTerms();
                    if (isCheckTypeEnable(wi.getCheckType())) {
                        equals.put("checkin_type", wi.getCheckType());
                    }
                    if (isExceptionTypeEnable(wi.getExceptionType())) {
                        equals.put("exception_type", wi.getExceptionType());
                    }
                    InTerms ins = new InTerms();
                    List<String> subUnits = business.organization().unit().listWithUnitSubNested(wi.getTopUnit());
                    if (subUnits == null || subUnits.isEmpty()) {
                        subUnits = new ArrayList<>();
                    }
                    subUnits.add(wi.getTopUnit());
                    ins.put("o2Unit", subUnits);
                    result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null,
                            null, ins, null, null, null, betweenTerms, true, DESC);
                }
            } else {
                throw new ExceptionNotManager();
            }

        }
        return result;
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("年份")
        private String year;
        @FieldDescribe("月份")
        private String month;
        @FieldDescribe("日期")
        private String day;
        @FieldDescribe("人员")
        private String person;
        @FieldDescribe("部门")
        private String unit;
        @FieldDescribe("顶级部门，会及联查询下级部门")
        private String topUnit;
        @FieldDescribe("打卡类型:上班打卡，下班打卡，外出打卡")
        private String checkType;
        @FieldDescribe("打卡结果:时间异常，地点异常，未打卡，wifi异常，非常用设备")
        private String exceptionType;


        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getTopUnit() {
            return topUnit;
        }

        public void setTopUnit(String topUnit) {
            this.topUnit = topUnit;
        }

        public String getCheckType() {
            return checkType;
        }

        public void setCheckType(String checkType) {
            this.checkType = checkType;
        }

        public String getExceptionType() {
            return exceptionType;
        }

        public void setExceptionType(String exceptionType) {
            this.exceptionType = exceptionType;
        }
    }

    public static class Wo extends AttendanceQywxDetail {
        static final WrapCopier<AttendanceQywxDetail, Wo> copier =
                WrapCopierFactory.wo(AttendanceQywxDetail.class, Wo.class, null, JpaObject.FieldsInvisible);

        @FieldDescribe("实际打卡时间")
        private Date checkTimeFormat;
        @FieldDescribe("排序号")
        private Long rank;

        public Long getRank() {
            return rank;
        }

        public void setRank(Long rank) {
            this.rank = rank;
        }

        public void formatDateTime() {
            if (checkTimeFormat == null) {
                Date date = new Date();
                date.setTime(getCheckin_time());
                setCheckTimeFormat(date);
            }
        }

        public Date getCheckTimeFormat() {
            return checkTimeFormat;
        }

        public void setCheckTimeFormat(Date checkTimeFormat) {
            this.checkTimeFormat = checkTimeFormat;
        }
    }


    private boolean isCheckTypeEnable(String type) {
        if (StringUtils.isEmpty(type) || (!AttendanceQywxDetail.CHECKIN_TYPE_OFF.equals(type) && !AttendanceQywxDetail.CHECKIN_TYPE_ON.equals(type) && !AttendanceQywxDetail.CHECKIN_TYPE_OUTSIDE.equals(type))) {
            return false;
        }
        return true;
    }

    private boolean isExceptionTypeEnable(String result) {
        if (StringUtils.isEmpty(result) ||
                (!AttendanceQywxDetail.EXCEPTION_TYPE_NORMAL.equals(result)
                        && !AttendanceQywxDetail.EXCEPTION_TYPE_ADDRESS.equals(result)
                        && !AttendanceQywxDetail.EXCEPTION_TYPE_NOSIGN.equals(result)
                        && !AttendanceQywxDetail.EXCEPTION_TYPE_TIME.equals(result)
                        && !AttendanceQywxDetail.EXCEPTION_TYPE_UNKOWN_DEVICE.equals(result)
                        && !AttendanceQywxDetail.EXCEPTION_TYPE_WIFI.equals(result))) {
            return false;
        }
        return true;
    }

    private static Date getMonthLastDay(String year, String month) throws Exception {
        Calendar cal = Calendar.getInstance();
        int yearInt = Integer.parseInt(year);
        cal.set(Calendar.YEAR, yearInt);
        int monthInt = Integer.parseInt(month);
        cal.set(Calendar.MONTH, monthInt);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    private static Date getDay(String year, String month, String day) throws Exception {
        Calendar cal = Calendar.getInstance();
        int yearInt = Integer.parseInt(year);
        cal.set(Calendar.YEAR, yearInt);
        int monthInt = Integer.parseInt(month);
        cal.set(Calendar.MONTH, monthInt-1);
        int dayInt = Integer.parseInt(day);
        cal.set(Calendar.DATE, dayInt);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    private static Date getEndDay(String year, String month, String day) throws Exception {
        Calendar cal = Calendar.getInstance();
        int yearInt = Integer.parseInt(year);
        cal.set(Calendar.YEAR, yearInt);
        int monthInt = Integer.parseInt(month);
        cal.set(Calendar.MONTH, monthInt-1);
        int dayInt = Integer.parseInt(day);
        cal.set(Calendar.DATE, dayInt);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
