package com.x.attendance.assemble.control.jaxrs.v2.shift;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Shift;
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

/**
 * Created by fancyLou on 2023/1/31.
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
            Business business = new Business(emc);
            ActionResult<List<Wo>> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null) {
                wi = new Wi();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            List<AttendanceV2Shift> list = business.getAttendanceV2ManagerFactory().listShiftWithNameByPage(adjustPage,
                    adjustPageSize, wi.getName());
            List<Wo> wos = Wo.copier.copy(list);
            result.setData(wos);
            result.setCount(business.getAttendanceV2ManagerFactory().shiftCountWithName(wi.getName()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = -4138592167949224238L;
        @FieldDescribe("班次名称")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static class Wo extends AttendanceV2Shift {
 

        private static final long serialVersionUID = -4698953176142071598L;
        static WrapCopier<AttendanceV2Shift, Wo> copier = WrapCopierFactory.wo(AttendanceV2Shift.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
