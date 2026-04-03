package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveRequestEnums.LeaveRequestStatusEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;

public class ActionLeaveRequestApply extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeaveRequestApply.class);

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("人员标识");
            }
            if (StringUtils.isEmpty(wi.getLeaveTypeId())) {
                throw new ExceptionEmptyParameter("请假类型ID");
            }
            if (null == wi.getStartTime()) {
                throw new ExceptionEmptyParameter("开始时间");
            }
            if (null == wi.getEndTime()) {
                throw new ExceptionEmptyParameter("结束时间");
            }
            if (wi.getStartTime() != null && wi.getEndTime() != null && wi.getStartTime().after(wi.getEndTime())) {
                throw new ExceptionDateEndBeforeStartError();
            }
            // 人员查询 转化成DN
            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("人员 " + wi.getPerson());
            }
            wi.setPerson(person.getDistinguishedName()); // 转换为DN存储
            AttendanceV2LeaveType leaveType = emc.find(wi.getLeaveTypeId(), AttendanceV2LeaveType.class);
            if (leaveType == null) {
                throw new ExceptionNotExistObject("请假类型 " + wi.getLeaveTypeId());
            }

            if (wi.getDuration() <= 0.0) { // 有传入数据 就不计算 按照传入的值来。
                // 计算日期间隔
                long interval = wi.getEndTime().getTime() - wi.getStartTime().getTime();
                double days = interval / (1000.0 * 3600 * 24);
                // 保留1位小数
                BigDecimal b = new BigDecimal(days);
                days = b.setScale(1, RoundingMode.HALF_UP).doubleValue();
                wi.setDuration(days);
            }
            String id = saveLeaveRequest(emc, wi);
            Wo wo = new Wo();
            wo.setId(id);
            result.setData(wo);
            if (QuotaTypeEnum.QUOTA.getValue().equals(leaveType.getQuotaType())) {
                // 处理有限制配额的情况
                deductingLeaveBalance(leaveType, person.getDistinguishedName(), wi.getDuration(), id);
            }

            return result;
        }
    }

    /**
     * 保存请假申请数据
     * 
     * @param emc
     * @param wi
     * @return
     * @throws Exception
     */
    private String saveLeaveRequest(EntityManagerContainer emc, Wi wi) throws Exception {
        emc.beginTransaction(AttendanceV2LeaveRequest.class);
        AttendanceV2LeaveRequest leaveRequest = new AttendanceV2LeaveRequest();
        leaveRequest.setPerson(wi.getPerson());
        leaveRequest.setLeaveTypeId(wi.getLeaveTypeId());
        leaveRequest.setStartTime(wi.getStartTime());
        leaveRequest.setEndTime(wi.getEndTime());
        leaveRequest.setDescription(wi.getDescription());
        leaveRequest.setStatus(LeaveRequestStatusEnum.APPLYING.getValue());
        emc.persist(leaveRequest, CheckPersistType.all);
        emc.commit();
        return leaveRequest.getId();
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 6434642802459794425L;

        @FieldDescribe("用户")
        private String person;

        @FieldDescribe("假期类型ID")
        private String leaveTypeId;

        @FieldDescribe("开始时间: yyyy-MM-dd HH:mm:ss")
        private Date startTime;

        @FieldDescribe("结束时间: yyyy-MM-dd HH:mm:ss")
        private Date endTime;

        @FieldDescribe("请假天数")
        private Double duration;

        @FieldDescribe("请假说明")
        private String description;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(String leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public Double getDuration() {
            return duration;
        }

        public void setDuration(Double duration) {
            this.duration = duration;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -2826350900812782444L;

    }
}
