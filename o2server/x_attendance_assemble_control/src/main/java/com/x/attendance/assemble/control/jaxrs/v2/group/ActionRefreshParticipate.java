package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionRefreshParticipate extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson,String id) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if(!business.isManager(effectivePerson)){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            AttendanceV2Group group = emc.find(id, AttendanceV2Group.class);
            if (group == null) {
                throw new ExceptionNotExistObject(id+"考勤组");
            }
            List<String> trueList = AttendanceV2Helper.calTruePersonFromMixList(emc, business, group.getId(), group.getParticipateList(), group.getUnParticipateList());
            if (trueList == null || trueList.isEmpty()) {
                throw new ExceptionEmptyParameter("考勤打卡人员、组织");
            }
            group.setTrueParticipantList(trueList);
            emc.beginTransaction(AttendanceV2Group.class);
            emc.persist(group, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            wo.setTrueParticipantList(trueList);
            result.setData(wo);
        }

        return result;
    }

    public static class Wo extends WrapOutBoolean {


        private static final long serialVersionUID = -3249622798550098407L;

         @FieldDescribe("真实的考勤打卡的人员列表.")
        private List<String> trueParticipantList; // 前端排班使用

        public List<String> getTrueParticipantList() {
            return trueParticipantList;
        }

        public void setTrueParticipantList(List<String> trueParticipantList) {
            this.trueParticipantList = trueParticipantList;
        }
    }
}
