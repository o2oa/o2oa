package com.x.attendance.assemble.control.jaxrs.dingding;

import java.util.Date;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;


public class ActionSyncData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionSyncData.class);

    public ActionResult<WrapBoolean> execute(EffectivePerson effectivePerson, String dateFrom, String dateTo) throws Exception {
        ActionResult<WrapBoolean> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (null == dateFrom || null == dateTo) {
                throw new SyncWayException();
            }
            Date from = DateTools.parse(dateFrom);
            Date to = DateTools.parse(dateTo);
            long gap = to.getTime() - from.getTime();
            if (gap < 0) {
                throw new SyncWayException();
            }
            if ((gap / (1000 * 60 * 60 * 24)) > 6 ) {
                throw new MoreThanSevenDayException();
            }
//            Business business = new Business(emc);
//            List<DingdingQywxSyncRecord> conflictList = business.dingdingAttendanceFactory().findConflictSyncRecord(from.getTime(), to.getTime());
//            if (conflictList != null && !conflictList.isEmpty()) {
//                throw new ConflictSyncRecordException();
//            }
            DingdingQywxSyncRecord record = new DingdingQywxSyncRecord();
            record.setDateFrom(from.getTime());
            record.setDateTo(to.getTime());
            record.setStartTime(new Date());
            record.setType(DingdingQywxSyncRecord.syncType_dingding);
            record.setStatus(DingdingQywxSyncRecord.status_loading);
            emc.beginTransaction(DingdingQywxSyncRecord.class);
            emc.persist(record);
            emc.commit();
            ThisApplication.dingdingQueue.send(record);
            result.setData(new WrapBoolean(true));
        }
        return result;
    }

}
