package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
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

import java.util.List;

public class ActionListByPage extends  BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPage.class);

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
            List<AttendanceV2LeaveData> list = business.getAttendanceV2ManagerFactory().listLeaveDataByPage(adjustPage, adjustPageSize, wi.getPerson());
            result.setData(Wo.copier.copy(list));
            result.setCount(business.getAttendanceV2ManagerFactory().listLeaveDataCount(wi.getPerson()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("人员DN")
        private String person;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }
    }


    public static class Wo extends AttendanceV2LeaveData {

        static WrapCopier<AttendanceV2LeaveData, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveData.class, Wo.class, null,
                JpaObject.FieldsInvisible);

    }

}