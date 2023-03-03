package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

/**
 * 申诉流程结束后回填数据
 * Created by fancyLou on 2023/3/3.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionUpdateForEnd extends BaseAction {

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
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
            if (info == null) {
                throw new ExceptionNotExistObject("数据不存在，" + id);
            }
            emc.beginTransaction(AttendanceV2AppealInfo.class);
            info.setStatus(wi.getResult() == 1 ? 2 : 3);
            info.setEndTime(DateTools.now());
            info.setReason(wi.getReason());
            info.setJobId(wi.getJobId());
            emc.check(info, CheckPersistType.all);
            emc.commit();
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
