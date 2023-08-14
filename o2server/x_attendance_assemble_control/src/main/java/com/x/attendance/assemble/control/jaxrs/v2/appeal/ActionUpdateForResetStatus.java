package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

/**
 * 还原数据状态，清除流程关联.
 * Created by fancyLou on 2023/3/3.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionUpdateForResetStatus extends BaseAction {

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
            info.setJobId(""); // 设置 job  前端根据 job 显示打开流程的按钮
            info.setStatus(AttendanceV2AppealInfo.status_TYPE_INIT); // 还原状态 初始化
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
        private static final long serialVersionUID = -2371502815391079967L;
    }
}
