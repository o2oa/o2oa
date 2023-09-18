package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionCannotOperateGroup;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionRebuildDetailWithGroupDate  extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRebuildDetailWithGroupDate.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, String groupId, String date) throws Exception {
        if (StringUtils.isEmpty(groupId)) {
            throw new ExceptionEmptyParameter("groupId");
        }
        if (StringUtils.isEmpty(date)) {
            throw new ExceptionEmptyParameter("date");
        }
        if (!AttendanceV2Helper.isValidDateString(date)) {
            throw new ExceptionWithMessage("日期格式不正确，需要格式：yyyy-MM-dd！");
        }
        if (!AttendanceV2Helper.beforeToday(date)) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(false);
            result.setData(wo);
            return result;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if(!business.isManager(effectivePerson)){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            AttendanceV2Group group = emc.find(groupId, AttendanceV2Group.class);
            if (group == null) {
                throw new ExceptionNotExistObject("考勤组"+groupId);
            }
            if (group.getStatus() != null && group.getStatus() == AttendanceV2Group.status_auto) {
                throw new ExceptionCannotOperateGroup(groupId);
            }
            List<String> trueList =  AttendanceV2Helper.calTruePersonFromMixList(emc, business, group.getId(), group.getParticipateList(), group.getUnParticipateList());
            if (trueList == null || trueList.isEmpty()) {
                throw new ExceptionEmptyParameter("考勤打卡人员、组织");
            }
            group.setTrueParticipantList(trueList);
            emc.beginTransaction(AttendanceV2Group.class);
            emc.persist(group, CheckPersistType.all);
            emc.commit();
            // 发送到队列处理数据
            for (String person : trueList) {
                LOGGER.info("发起考勤数据生成，Date：{} person: {}", date, person);
                ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(person, date));
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = 5693725665271046294L;
    }

}
