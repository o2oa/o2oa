package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import java.util.ArrayList;
import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {



    /**
     * 先删除老的记录
     * @param personDn
     * @param date yyyy-MM-dd
     * @param emc
     * @param business
     * @throws Exception
     */
    protected void deleteOldRecords(String personDn, String date, EntityManagerContainer emc, Business business) throws Exception {
        // 查询是否有存在的数据
        List<AttendanceV2CheckInRecord> oldRecordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(personDn, date);
        List<String> deleteIds = new ArrayList<>();
        for (AttendanceV2CheckInRecord record : oldRecordList) {
            deleteIds.add(record.getId());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.delete(AttendanceV2CheckInRecord.class, deleteIds);
        emc.commit();
    }
}
