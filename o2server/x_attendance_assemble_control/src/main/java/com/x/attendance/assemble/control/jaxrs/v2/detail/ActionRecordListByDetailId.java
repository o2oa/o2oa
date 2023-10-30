package com.x.attendance.assemble.control.jaxrs.v2.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionRecordListByDetailId  extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRecordListByDetailId.class);

    ActionResult<List<WoRecord>> execute(EffectivePerson effectivePerson, String detailId) throws Exception {
      ActionResult<List<WoRecord>> result = new ActionResult<>();
      try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
        AttendanceV2Detail detail = emc.find(detailId, AttendanceV2Detail.class);
        if (detail == null) {
          throw new ExceptionNotExistObject(detailId);
        }
        List<WoRecord> recordList = new ArrayList<>();
        List<String> ids = detail.getRecordIdList();
        if (ids != null && !ids.isEmpty()) {
            for (String id : ids) {
                AttendanceV2CheckInRecord record = emc.find(id, AttendanceV2CheckInRecord.class);
                if (record != null) {
                    WoRecord woRecord = WoRecord.copier.copy(record);
                    try {
                        if (StringUtils.isNotEmpty(woRecord.getLeaveDataId())) {
                            AttendanceV2LeaveData leaveData = emc.find(woRecord.getLeaveDataId(), AttendanceV2LeaveData.class);
                            if (leaveData != null) {
                                woRecord.setLeaveData(leaveData);
                            }
                        }
                    } catch (Exception ignore) {}
                    recordList.add(woRecord);
                }
            }
        }
        Collections.sort(recordList, Comparator.comparing(WoRecord::getRecordDate));
        result.setData(recordList);
        return result;
      }
    }

    public static class WoRecord extends AttendanceV2CheckInRecord {
        private static final long serialVersionUID = -4639650669016226001L;

        @FieldDescribe("外出请假记录")
        private AttendanceV2LeaveData leaveData;

        static WrapCopier<AttendanceV2CheckInRecord, WoRecord> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class, WoRecord.class, null,
                JpaObject.FieldsInvisible);

        public AttendanceV2LeaveData getLeaveData() {
            return leaveData;
        }

        public void setLeaveData(AttendanceV2LeaveData leaveData) {
            this.leaveData = leaveData;
        }
    }
  
}
