package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionCannotRepetitive;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

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
            if(!business.isManager(effectivePerson)){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getGroupName())) {
                throw new ExceptionEmptyParameter("考勤组名称");
            }
            // 名称不能相同
            List<AttendanceV2Group> checkNameGroup = emc.listEqual(AttendanceV2Group.class, AttendanceV2Group.groupName_FIELDNAME, wi.getGroupName());
            if (checkNameGroup != null && !checkNameGroup.isEmpty()) {
                for (AttendanceV2Group attendanceV2Group : checkNameGroup) {
                    if (attendanceV2Group.getGroupName().equals(wi.getGroupName()) && !attendanceV2Group.getId().equals(wi.getId())) {
                        throw new ExceptionCannotRepetitive("考勤组名称");
                    }
                }
            }
            if (wi.getParticipateList() == null || wi.getParticipateList().isEmpty()) {
                throw new ExceptionEmptyParameter("考勤打卡人员、组织");
            }
            // 固定班制
            if (AttendanceV2Group.CHECKTYPE_Fixed.equals(wi.getCheckType())) {
                if (wi.getWorkDateProperties() == null || !wi.getWorkDateProperties().validateNotEmpty()) {
                    throw new ExceptionEmptyParameter("考勤工作日设置");
                }
            } else if (AttendanceV2Group.CHECKTYPE_Free.equals(wi.getCheckType())) { // 自由班制
                if (StringUtils.isEmpty(wi.getWorkDateList())) {
                    throw new ExceptionEmptyParameter("考勤工作日设置");
                }
            } else {
                throw new   ExceptionEmptyParameter("打卡类型");
            }

            if (wi.getWorkPlaceIdList() == null || wi.getWorkPlaceIdList().isEmpty()) {
                throw new ExceptionEmptyParameter("工作场所列表");
            }
            List<String> trueList = AttendanceV2Helper.calTruePersonFromMixList(emc, business, wi.getId(), wi.getParticipateList(), wi.getUnParticipateList());
            wi.setTrueParticipantList(trueList);
            // 新增或更新
            AttendanceV2Group group = emc.find(wi.getId(), AttendanceV2Group.class);
            if (group == null) { // 新增
                group = Wi.copier.copy(wi);
            } else { // 修改
                Wi.copier.copy(wi, group);
            }
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
