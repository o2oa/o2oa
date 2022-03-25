package com.x.attendance.assemble.control.jaxrs.dingding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.BetweenTerms;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionListDDAttendanceDetail extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListDDAttendanceDetail.class);

    public ActionResult<List<Wo>> execute(String flag, Integer count, JsonElement jsonElement) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement , Wi.class);
            if (StringUtils.isEmpty(wi.getYear())) {
                throw new ExceptionTimeEmpty();
            }
            if (StringUtils.isEmpty(wi.getPerson()) && StringUtils.isEmpty(wi.getUnit()) && StringUtils.isEmpty(wi.getTopUnit())) {
                throw new ExceptionSearchArgEmpty();
            }
            Date startDay  ;
            Date endDay;
            if (StringUtils.isEmpty(wi.getMonth())) {
                startDay = getDay(wi.getYear(), "1", "1");
                endDay = getDay(wi.getYear(), "12", "31");
            }else {
                if (StringUtils.isEmpty(wi.getDay())) {
                    startDay = getDay(wi.getYear(), wi.getMonth(), "1");
                    endDay = getMonthLastDay(wi.getYear(), wi.getMonth());
                }else {
                    startDay = getDay(wi.getYear(), wi.getMonth(), wi.getDay());
                    endDay = getEndDay(wi.getYear(), wi.getMonth(), wi.getDay());
                }
            }
            BetweenTerms betweenTerms = new BetweenTerms();
            betweenTerms.put("userCheckTime", ListTools.toList(startDay.getTime(), endDay.getTime()));
            String id = EMPTY_SYMBOL;
            /** 如果不是空位标志位 */
            if (!StringUtils.equals(EMPTY_SYMBOL, flag)) {
                id = flag;
            }
            if (StringUtils.isNotEmpty(wi.getPerson())) {
                EqualsTerms equals = new EqualsTerms();
                equals.put("o2User", wi.getPerson());
                if (isCheckTypeEnable(wi.getCheckType())){
                    equals.put("checkType", wi.getCheckType());
                }
                if (isTimeResultEnable(wi.getTimeResult())) {
                    equals.put("timeResult", wi.getTimeResult());
                }
                result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null,
                        null, null, null, null, null, betweenTerms, true, DESC);
            }
            if (StringUtils.isNotEmpty(wi.getUnit())) {
                EqualsTerms equals = new EqualsTerms();
                equals.put("o2Unit", wi.getUnit());
                if (isCheckTypeEnable(wi.getCheckType())){
                    equals.put("checkType", wi.getCheckType());
                }
                if (isTimeResultEnable(wi.getTimeResult())) {
                    equals.put("timeResult", wi.getTimeResult());
                }
                logger.info("equals :"+equals.toString());
                result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null,
                        null, null, null, null, null, betweenTerms, true, DESC);
            }
            if (StringUtils.isNotEmpty(wi.getTopUnit())) {
                EqualsTerms equals = new EqualsTerms();
                if (isCheckTypeEnable(wi.getCheckType())){
                    equals.put("checkType", wi.getCheckType());
                }
                if (isTimeResultEnable(wi.getTimeResult())) {
                    equals.put("timeResult", wi.getTimeResult());
                }
                InTerms ins = new InTerms();
                List<String> subUnits = business.organization().unit().listWithUnitSubNested( wi.getTopUnit() );
                if (subUnits == null || subUnits.isEmpty()) {
                    subUnits = new ArrayList<>();
                }
                subUnits.add(wi.getTopUnit());
                ins.put("o2Unit", subUnits);
                logger.info("ins :"+ins.toString());
                result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null,
                        null, ins, null, null, null, betweenTerms, true, DESC);
            }

        }
        return result;
    }

    private boolean isCheckTypeEnable(String type) {
        if (StringUtils.isEmpty(type) || (!AttendanceDingtalkDetail.OffDuty.equals(type) && !AttendanceDingtalkDetail.OnDuty.equals(type))) {
            return false;
        }
        return true;
    }

    private boolean isTimeResultEnable(String result) {
        if (StringUtils.isEmpty(result) ||
                (!AttendanceDingtalkDetail.TIMERESULT_Absenteeism.equals(result)
                        && !AttendanceDingtalkDetail.TIMERESULT_Early.equals(result)
                        && !AttendanceDingtalkDetail.TIMERESULT_Late.equals(result)
                        && !AttendanceDingtalkDetail.TIMERESULT_NORMAL.equals(result)
                        && !AttendanceDingtalkDetail.TIMERESULT_NotSigned.equals(result)
                        && !AttendanceDingtalkDetail.TIMERESULT_SeriousLate.equals(result))) {
            return false;
        }
        return true;
    }

    public static Date getMonthLastDay(String year, String month) throws Exception {
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

    public static Date getDay(String year, String month, String day) throws Exception {
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
    public static Date getEndDay(String year, String month, String day) throws Exception {
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
        @FieldDescribe("打卡类型:OffDuty|OnDuty")
        private String checkType;
        @FieldDescribe("打卡结果:Normal|Early|Late|SeriousLate|Absenteeism|NotSigned")
        private String timeResult;


        public String getCheckType() {
            return checkType;
        }

        public void setCheckType(String checkType) {
            this.checkType = checkType;
        }

        public String getTimeResult() {
            return timeResult;
        }

        public void setTimeResult(String timeResult) {
            this.timeResult = timeResult;
        }

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

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getTopUnit() {
            return topUnit;
        }

        public void setTopUnit(String topUnit) {
            this.topUnit = topUnit;
        }
    }

    public static class Wo extends AttendanceDingtalkDetail {
        static final WrapCopier<AttendanceDingtalkDetail, Wo> copier =
                WrapCopierFactory.wo(AttendanceDingtalkDetail.class, Wo.class, null, JpaObject.FieldsInvisible);

        @FieldDescribe("实际打卡时间")
        private Date userCheckTimeFormat;
        @FieldDescribe("工作日")
        private Date workDateFormat;
        @FieldDescribe("基准时间，用于计算迟到和早退")
        private Date baseCheckTimeFormat;
        @FieldDescribe("排序号")
        private Long rank;


        public void formatDateTime() {
            if (userCheckTimeFormat == null) {
                Date date = new Date();
                date.setTime(getUserCheckTime());
                setUserCheckTimeFormat(date);
            }
            if (workDateFormat == null) {
                Date date = new Date();
                date.setTime(getWorkDate());
                setWorkDateFormat(date);
            }
            if (baseCheckTimeFormat == null) {
                Date date = new Date();
                date.setTime(getBaseCheckTime());
                setBaseCheckTimeFormat(date);
            }
        }

        public Long getRank() {
            return rank;
        }

        public void setRank(Long rank) {
            this.rank = rank;
        }

        public Date getUserCheckTimeFormat() {
            return userCheckTimeFormat;
        }

        public void setUserCheckTimeFormat(Date userCheckTimeFormat) {
            this.userCheckTimeFormat = userCheckTimeFormat;
        }

        public Date getWorkDateFormat() {
            return workDateFormat;
        }

        public void setWorkDateFormat(Date workDateFormat) {
            this.workDateFormat = workDateFormat;
        }

        public Date getBaseCheckTimeFormat() {
            return baseCheckTimeFormat;
        }

        public void setBaseCheckTimeFormat(Date baseCheckTimeFormat) {
            this.baseCheckTimeFormat = baseCheckTimeFormat;
        }
    }
}
