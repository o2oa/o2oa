package com.x.attendance.assemble.control.jaxrs.dingding;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActionListDDAttendanceDetail extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListDDAttendanceDetail.class);

    public ActionResult<List<Wo>> execute(JsonElement jsonElement) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement , Wi.class);
            Date start = DateTools.parseDateTime(wi.getStartTime());
            Date end = DateTools.parseDateTime(wi.getEndTime());
            String dingdingUser = null;
            //转化dingding的id
            if (wi.getPerson() != null) {
                Person person = business.organization().person().getObject(wi.getPerson());
                dingdingUser = person.getDingdingId();
            }
            List<AttendanceDingtalkDetail> list = business.dingdingAttendanceFactory().findAllDingdingAttendanceDetail(start, end, dingdingUser);
            if (list != null && !list.isEmpty()) {
                List<Wo> wos = list.stream().map(detail -> {
                    Wo wo = new Wo();
                    try {
                        wo = Wo.copier.copy(detail, wo);
                        wo.formatDateTime();
                    }catch (Exception e) {
                        logger.error(e);
                    }
                    return wo;
                }).collect(Collectors.toList());
                result.setData(wos);
            }
        }
        return result;
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("开始时间：yyyy-MM-dd HH:mm:ss")
        private String startTime;
        @FieldDescribe("结束时间：yyyy-MM-dd HH:mm:ss")
        private String endTime;
        @FieldDescribe("人员")
        private String person;


        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
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
