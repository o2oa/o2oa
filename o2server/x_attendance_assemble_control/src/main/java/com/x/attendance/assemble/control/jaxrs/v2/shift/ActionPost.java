package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTimeProperties;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPost extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPost.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson,
                             JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isBlank(wi.getShiftName())) {
                throw new ExceptionEmptyParameter("班次名称");
            }
            AttendanceV2ShiftCheckTimeProperties properties = wi.getProperties();
            checkShiftProperties(properties);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("班次post {}", wi.toString());
            }
            // 新增
            AttendanceV2Shift shift = Wi.copier.copy(wi);
            shift.setOperator(effectivePerson.getDistinguishedName());
            emc.beginTransaction(AttendanceV2Shift.class);
            emc.persist(shift, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(shift.getId());
            result.setData(wo);
        }
        return result;
    }



    public static class Wi extends AttendanceV2Shift {


        private static final long serialVersionUID = -3754167120785080501L;
        static WrapCopier<Wi, AttendanceV2Shift> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2Shift.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -386687689632138413L;
    }
}
