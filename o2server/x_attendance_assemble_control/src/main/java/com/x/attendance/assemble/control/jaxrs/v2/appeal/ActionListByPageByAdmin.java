package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
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

import java.util.List;

public class ActionListByPageByAdmin extends BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPageByAdmin.class);

    ActionResult<List<Wo>> execute(EffectivePerson person, Integer page, Integer size, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("person {}, page: {}, size: {}", person.getDistinguishedName(), adjustPage, adjustPageSize);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            List<AttendanceV2AppealInfo> list = business.getAttendanceV2ManagerFactory().listAppealInfoByPage(adjustPage, adjustPageSize, wi.getUsers(), wi.getStartDate(), wi.getEndDate());
            List< Wo> wos =   Wo.copier.copy(list);
            if (wos != null && !wos.isEmpty()) {
                for ( Wo detail : wos) {
                    AttendanceV2CheckInRecord record = emc.find(detail.getRecordId(), AttendanceV2CheckInRecord.class);
                    if (record != null) {
                        detail.setRecord(record);
                    }
                }
            }
            result.setData(wos);
            result.setCount(business.getAttendanceV2ManagerFactory().appealCount(wi.getUsers(), wi.getStartDate(), wi.getEndDate()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("用户标识")
        private String userId;
        @FieldDescribe("用户标识")
        private List<String> users;
        @FieldDescribe("开始日期")
        private String startDate;
        @FieldDescribe("结束日期")
        private String endDate;


        
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }
    }


    public static class Wo extends AttendanceV2AppealInfo {
        private static final long serialVersionUID = 6142658857959870785L;
        
        static WrapCopier<AttendanceV2AppealInfo, Wo> copier = WrapCopierFactory.wo(AttendanceV2AppealInfo.class, Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("打卡记录")
        private AttendanceV2CheckInRecord record;

        public AttendanceV2CheckInRecord getRecord() {
            return record;
        }

        public void setRecord(AttendanceV2CheckInRecord record) {
            this.record = record;
        }
    }

}