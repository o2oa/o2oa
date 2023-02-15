package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionParticipateConflict;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionCreateUpdate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateUpdate.class);
    ActionResult<Wo> execute(EffectivePerson effectivePerson,
                             JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getGroupName())) {
                throw new ExceptionEmptyParameter("考勤组名称");
            }
            if (wi.getParticipateList() == null || wi.getParticipateList().isEmpty()) {
                throw new ExceptionEmptyParameter("考勤打卡人员、组织");
            }
            if (StringUtils.isEmpty(wi.getShiftId())) {
                throw new ExceptionEmptyParameter("关联班次");
            }
            AttendanceV2Shift shift = emc.find(wi.getShiftId(), AttendanceV2Shift.class);
            if (shift == null) {
                throw new ExceptionNotExistObject("关联班次"+wi.getShiftId());
            }
            if (StringUtils.isEmpty(wi.getWorkDateList())) {
                throw new ExceptionEmptyParameter("考勤工作日设置");
            }
            if (wi.getWorkPlaceIdList() == null || wi.getWorkPlaceIdList().isEmpty()) {
                throw new ExceptionEmptyParameter("工作场所列表");
            }
            // 处理考勤组
            List<String> peopleList = new ArrayList<>();
            for (String p : wi.getParticipateList()) {
                if (p.endsWith("@P")) {
                    peopleList.add(p);
                } else if (p.endsWith("@I")) {
                    String person = business.organization().person().getWithIdentity(p);
                    peopleList.add(person);
                }else if (p.endsWith("@U")) { // 递归查询人员
                    List<String> pList = business.organization().person().listWithUnitSubNested( p );
                    peopleList.addAll(pList);
                } else {
                    LOGGER.info("错误的标识？ " + p);
                }
            }
            // 删除排除的人员
            if (wi.getUnParticipateList() != null && !wi.getUnParticipateList().isEmpty()) {
                for (String p: wi.getUnParticipateList()) {
                    peopleList.remove(p);
                }
            }
            // 去重复
            HashSet<String> peopleSet = new HashSet<>(peopleList);
            // 判断是否和其它考勤组内的成员冲突
            List<String> conflictPersonInOtherGroup = new ArrayList<>();
            List<AttendanceV2Group> groups = emc.listAll(AttendanceV2Group.class);
            if (groups != null && !groups.isEmpty()) {
                for (String person : peopleSet) {
                    for (AttendanceV2Group oldG : groups) {
                        if (oldG.getTrueParticipantList().contains(person)) {
                            conflictPersonInOtherGroup.add(person);
                            break;
                        }
                    }
                }
            }
            if (!conflictPersonInOtherGroup.isEmpty()) {
                throw new ExceptionParticipateConflict(conflictPersonInOtherGroup);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("最终考勤组人员数：" + peopleSet.size());
            }

            wi.setTrueParticipantList(new ArrayList<>(peopleSet));
            // 新增或更新
            AttendanceV2Group group = Wi.copier.copy(wi);
            group.setOperator(effectivePerson.getDistinguishedName());
            emc.beginTransaction(AttendanceV2Group.class);
            emc.persist(group, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(group.getId());
            result.setData(wo);
        }
        return result;
    }


    public static class Wi extends AttendanceV2Group {

        private static final long serialVersionUID = -3776223218556349868L;
        static WrapCopier<Wi, AttendanceV2Group> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2Group.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = 2307399114926768280L;
    }
}
