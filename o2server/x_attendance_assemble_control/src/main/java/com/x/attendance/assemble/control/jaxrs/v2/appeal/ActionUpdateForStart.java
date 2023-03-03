package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/3/3.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionUpdateForStart extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson person, String id) throws Exception {

        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
            if (info == null) {
                throw new ExceptionNotExistObject("数据不存在，" + id);
            }
            if (!person.getDistinguishedName().equals(info.getUserId())) {
                throw new ExceptionPersonNotEqual();
            }
            emc.beginTransaction(AttendanceV2AppealInfo.class);
            info.setStatus(1);
            info.setStartTime(DateTools.now());
            emc.check(info, CheckPersistType.all);
            emc.commit();
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -4351149479944550193L;
    }
}
