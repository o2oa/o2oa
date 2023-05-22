package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionListByPage extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPage.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
            throws Exception {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson.getDistinguishedName(), page, size);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null) {
                wi = new Wi();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            List<AttendanceV2CheckInRecord> list = business.getAttendanceV2ManagerFactory().listRecordByPage(adjustPage,
                    adjustPageSize, wi.getUserId(), wi.getRecordDateString());
            List<Wo> wos = Wo.copier.copy(list);
            for (Wo wo : wos) {
                try {
                    if (StringUtils.isNotEmpty(wo.getLeaveDataId())) {
                        AttendanceV2LeaveData leaveData = emc.find(wo.getLeaveDataId(), AttendanceV2LeaveData.class);
                        if (leaveData != null) {
                            wo.setLeaveData(leaveData);
                        }
                    }
                } catch (Exception ignore) {}
            }
            result.setData(wos);
            result.setCount(business.getAttendanceV2ManagerFactory().recordCount(wi.getUserId(), wi.getRecordDateString()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = 4227642755086093795L;
        @FieldDescribe("打卡的用户标识")
        private String userId;
        @FieldDescribe("打卡记录日期字符串: YYYY-MM-dd")
        private String recordDateString;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRecordDateString() {
            return recordDateString;
        }

        public void setRecordDateString(String recordDateString) {
            this.recordDateString = recordDateString;
        }
    }


    public static class Wo extends AttendanceV2CheckInRecord {

        @FieldDescribe("外出请假记录")
        private AttendanceV2LeaveData leaveData;

        public AttendanceV2LeaveData getLeaveData() {
            return leaveData;
        }

        public void setLeaveData(AttendanceV2LeaveData leaveData) {
            this.leaveData = leaveData;
        }

        static WrapCopier<AttendanceV2CheckInRecord, Wo> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
