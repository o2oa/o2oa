package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecordProperties;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import java.util.Date;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionFieldWorkJobFinished extends BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionFieldWorkJobFinished.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("外勤打卡审批回调 person:{},  ", effectivePerson.getDistinguishedName());
        }
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            String id = wi.getRecordId();
            if (StringUtils.isEmpty(id)) {
                throw new ExceptionEmptyParameter("id");
            }
            AttendanceV2CheckInRecord record = emc.find(id, AttendanceV2CheckInRecord.class);
            if (record == null) {
                throw new ExceptionNotExistObject("打卡记录（" + id + "）");
            }
            String type = wi.getOptType();
            String type_field_work_job_finished = "finished";
            String getType_field_work_job_cancel = "canceled";
            if (StringUtils.isEmpty(type) || (!type_field_work_job_finished.equals(type) && !getType_field_work_job_cancel.equals(type))) {
                throw new ExceptionEmptyParameter("optType");
            }

            AttendanceV2CheckInRecordProperties properties = record.getProperties();
            if (properties != null) {
                emc.beginTransaction(AttendanceV2CheckInRecord.class);
                if (type_field_work_job_finished.equals(type)) {
                    properties.finishFieldWorkJob();
                } else {
                    properties.cancelFieldWorkJob();
                }
                if (StringUtils.isNotEmpty(wi.getJobId())) {
                    properties.setFieldWorkJobId(wi.getJobId());
                }
                try {
                    if (BooleanUtils.isTrue(wi.getNeedResetTime())) {
                        Date onDutyTime = DateTools.parse(record.getRecordDateString() + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                        record.setRecordDate(onDutyTime);
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                record.setProperties(properties);
                emc.persist(record, CheckPersistType.all);
                emc.commit();
            }
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }
    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("打卡记录id")
        private String recordId;

        @FieldDescribe("操作类型 finished/canceled")
        private String optType;

        @FieldDescribe("是否需要重置打卡时间")
        private Boolean needResetTime;

        @FieldDescribe("流程的jobId")
        private String jobId;

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public String getOptType() {
            return optType;
        }

        public void setOptType(String optType) {
            this.optType = optType;
        }

        public Boolean getNeedResetTime() {
            return needResetTime;
        }

        public void setNeedResetTime(Boolean needResetTime) {
            this.needResetTime = needResetTime;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }
    }
    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -2371502815391079967L;
    }
}
