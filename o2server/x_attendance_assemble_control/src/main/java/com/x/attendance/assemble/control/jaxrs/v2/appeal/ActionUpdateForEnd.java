package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 申诉流程结束后回填数据
 * Created by fancyLou on 2023/3/3.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionUpdateForEnd extends BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateForEnd.class);


    ActionResult<Wo> execute(String id, JsonElement jsonElement) throws Exception {
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        if (wi.getResult() == null) {
            throw new ExceptionEmptyParameter("result");
        }
        if (StringUtils.isEmpty(wi.getReason())) {
            throw new ExceptionEmptyParameter("reason");
        }
        if (StringUtils.isEmpty(wi.getJobId())) {
            throw new ExceptionEmptyParameter("jobId");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("审批结束数据回填：{}", wi.toString());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
            if (info == null) {
                throw new ExceptionNotExistObject("数据不存在，" + id);
            }
            emc.beginTransaction(AttendanceV2AppealInfo.class);
            info.setStatus(wi.getResult() == 1 ? AttendanceV2AppealInfo.status_TYPE_PROCESS_AGREE : AttendanceV2AppealInfo.status_TYPE_PROCESS_DISAGREE); // 审批结果
            info.setEndTime(DateTools.now());
            info.setReason(wi.getReason());
            info.setJobId(wi.getJobId());
            emc.check(info, CheckPersistType.all);
            emc.commit();
            if (wi.getResult() == 1) { // 审批 同意 更新打卡记录 否则不动
                AttendanceV2CheckInRecord record = emc.find(info.getRecordId(), AttendanceV2CheckInRecord.class);
                if (record != null) {
                    emc.beginTransaction(AttendanceV2CheckInRecord.class);
                    // 申诉成功后，更新打卡状态
                    record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL);
                    record.setAppealId(info.getId()); // 申诉完成回填审核数据id
                    record.setDescription("申诉完成！");
                    try {
                        if (BooleanUtils.isTrue(wi.getNeedResetTime())) {
                            Date onDutyTime = DateTools.parse(record.getRecordDateString() + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                            record.setRecordDate(onDutyTime);
                        }
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                    emc.check(record, CheckPersistType.all);
                    emc.commit();
                    // 申诉成功后，重新生成对应的数据
                    if (wi.getResult() == 1) {
                        LOGGER.info("申诉成功，重新发起考勤数据生成，Date：{} person: {}", record.getRecordDateString(), record.getUserId());
                        ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(record.getUserId(), record.getRecordDateString()));
                    }
                } else {
                    LOGGER.info("没有找到对应的打卡记录数据，"+info.getRecordId());
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("审批结果：1-审批通过，-1-审批不通过")
        private Integer result;
        @FieldDescribe("申诉详细，申诉流程结束后写入.")
        private String reason;
        @FieldDescribe("流程的jobId，申诉流程结束后写入")
        private String jobId;
        @FieldDescribe("是否需要重置打卡时间")
        private Boolean needResetTime;

        public Boolean getNeedResetTime() {
            return needResetTime;
        }

        public void setNeedResetTime(Boolean needResetTime) {
            this.needResetTime = needResetTime;
        }

        public Integer getResult() {
            return result;
        }

        public void setResult(Integer result) {
            this.result = result;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -4351149479944550193L;
    }
}
