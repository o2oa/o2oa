package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.google.gson.JsonElement;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import org.apache.commons.lang3.StringUtils;

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
            AttendanceV2Shift shift = emc.find(id, AttendanceV2Shift.class);
            if (shift == null) {
                throw new ExceptionNotExistObject(id+"班次");
            }
            emc.beginTransaction(AttendanceV2Shift.class);
            emc.delete(AttendanceV2Shift.class, shift.getId());
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
