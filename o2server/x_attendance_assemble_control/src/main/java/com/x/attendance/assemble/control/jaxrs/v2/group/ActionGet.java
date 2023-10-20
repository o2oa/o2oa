package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2GroupWorkDayProperties;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionGet extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson,String id) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2Group group = emc.find(id, AttendanceV2Group.class);
            if (group == null) {
                throw new ExceptionNotExistObject(id+"考勤组");
            }
            Wo wo = Wo.copier.copy(group);
            // 班次对象返回
            if (group.getWorkDateProperties() != null && AttendanceV2Group.CHECKTYPE_Fixed.equals( group.getCheckType())) {
                AttendanceV2GroupWorkDayProperties properties = group.getWorkDateProperties();
                setPropertiesShiftData(new Business(emc), properties);
            }
            result.setData(wo);
        }
        return result;
    }



    public static class Wo extends AttendanceV2Group {

        private static final long serialVersionUID = 9085711603307897631L;

        static WrapCopier<AttendanceV2Group, Wo> copier = WrapCopierFactory.wo(AttendanceV2Group.class, Wo.class, null,
                JpaObject.FieldsInvisible);

    }
}
