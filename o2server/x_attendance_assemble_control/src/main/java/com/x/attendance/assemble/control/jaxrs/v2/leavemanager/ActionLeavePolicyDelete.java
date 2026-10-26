package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

public class ActionLeavePolicyDelete extends BaseAction {

    ActionResult<Wo> execute(String id) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2LeavePolicy policy = emc.find(id, AttendanceV2LeavePolicy.class);
            if (policy == null) {
                throw new ExceptionNotExistObject("无法找到指定ID的假期规则信息，ID：" + id);
            }
            emc.beginTransaction(AttendanceV2LeavePolicy.class);
            policy.setActive(false);
            emc.check(policy, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }
        return result;
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = 1245976381876774019L;

    }

}
