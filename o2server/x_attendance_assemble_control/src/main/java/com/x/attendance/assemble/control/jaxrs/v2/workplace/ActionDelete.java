package com.x.attendance.assemble.control.jaxrs.v2.workplace;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWorkPlaceUsed;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionDelete extends BaseAction {


    ActionResult<Wo> execute(EffectivePerson effectivePerson,
                                        String id) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if(!business.isManager(effectivePerson)){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            AttendanceV2WorkPlace workPlace = emc.find(id, AttendanceV2WorkPlace.class);
            if (workPlace == null) {
                throw new ExceptionNotExistObject(id+"工作地点");
            }
            List<AttendanceV2Group> groupList = business.getAttendanceV2ManagerFactory().listGroupWithWorkPlaceId(id);
            if (groupList != null && !groupList.isEmpty()) {
                throw new ExceptionWorkPlaceUsed(groupList);
            }
            emc.beginTransaction(AttendanceV2WorkPlace.class);
            emc.delete(AttendanceV2WorkPlace.class, workPlace.getId());
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }

        return result;
    }

    public static class Wo extends WrapOutBoolean {

        private static final long serialVersionUID = -719724843655567310L;
    }
}
