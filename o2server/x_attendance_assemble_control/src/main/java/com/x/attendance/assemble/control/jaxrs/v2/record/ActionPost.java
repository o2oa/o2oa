package com.x.attendance.assemble.control.jaxrs.v2.record;


import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

/**
 * Created by fancyLou on 2023/4/17.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPost extends BaseAction {


  private static final Logger LOGGER = LoggerFactory.getLogger(ActionPost.class);


    ActionResult<Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(person)) {
                throw new ExceptionAccessDenied(person);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi.getRecordDate() == null) {
                throw new ExceptionEmptyParameter("打卡记录日期");
            }
            if (StringUtils.isEmpty(wi.getCheckInResult())) {
                throw new ExceptionEmptyParameter("打卡结果");
            }
            if (!wi.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                    && !wi.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Early)
                    && !wi.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Late)
                    && !wi.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_SeriousLate)
                    && !wi.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned)
            ) {
                throw new ExceptionWithMessage("打卡结果不正确！");
            }
            if (StringUtils.isEmpty(wi.getCheckInType())) {
                throw new ExceptionEmptyParameter("考勤类型");
            }
            if (!wi.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty) && !wi.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)) {
                throw new ExceptionWithMessage("考勤类型不正确！");
            }
            // 新增或更新
            AttendanceV2CheckInRecord record = emc.find(wi.getId(), AttendanceV2CheckInRecord.class);
            if (record == null) { // 新增
                if (StringUtils.isEmpty(wi.getUserId())) {
                    throw new ExceptionEmptyParameter("userId");
                }
                Person p = business.organization().person().getObject(wi.getUserId());
                if (p == null || StringUtils.isEmpty(p.getDistinguishedName())) {
                    throw new ExceptionNotExistObject("人员(" + wi.getUserId() + ")");
                }
                // 查询当前用户的考勤组
                List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(p.getDistinguishedName());
                if (groups == null || groups.isEmpty()) {
                   throw new ExceptionNotExistObject("没有对应的考勤组");
                }
                record = Wi.copier.copy(wi);
                AttendanceV2Group group = groups.get(0);
                record.setGroupId(group.getId());
                record.setGroupName(group.getGroupName());
                record.setGroupCheckType(group.getCheckType());
                // 固定班制
                if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed)) {
                    String recordDateString = DateTools.format( record.getRecordDate(), DateTools.format_yyyyMMdd);
                    record.setRecordDateString(recordDateString);
                    // 正常的班次id
                    String shiftId = group.getWorkDateProperties().shiftIdWithDate(record.getRecordDate());
                    // 是否特殊工作日
                    if (StringUtils.isEmpty(shiftId)) {
                        shiftId = AttendanceV2Helper.specialWorkDayShift(recordDateString, group);
                    }
                    // 是否特殊节假日 清空shiftid
                    if (StringUtils.isNotEmpty(shiftId) && AttendanceV2Helper.isSpecialRestDay(recordDateString, group)) {
                        shiftId = null;
                    }
                    if (StringUtils.isNotEmpty(shiftId)) {
                        AttendanceV2Shift shift = emc.find(shiftId, AttendanceV2Shift.class);
                        if (shift != null) {
                            if (shift != null) {
                                record.setShiftId(shift.getId());
                                record.setShiftName(shift.getShiftName());
                            }
                        }
                    }
                }
            } else { // 修改
                Wi.copier.copy(wi, record);
            }
            emc.beginTransaction(AttendanceV2CheckInRecord.class);
            emc.persist(record, CheckPersistType.all);
            emc.commit();
            LOGGER.info("打卡记录保存：{}, {}, {} ", person, record.getRecordDateString(), record.getCheckInResult());
            result.setData(new Wo(record.getId()));
            return result;
        }
    }


    public static class Wo extends WrapOutId {

        private static final long serialVersionUID = 7459948549248297461L;

        public Wo(String id) throws Exception {
            super(id);
        }
    }

    public static class Wi extends AttendanceV2CheckInRecord {


        private static final long serialVersionUID = -6701765861170475468L;
        static WrapCopier<ActionPost.Wi, AttendanceV2CheckInRecord> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2CheckInRecord.class, null,
                JpaObject.FieldsUnmodify);
    }
}
