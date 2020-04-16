package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.Date;


public class ActionSyncData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionSyncData.class);

    public ActionResult<WrapBoolean> execute(EffectivePerson effectivePerson, String way) throws Exception {
        ActionResult<WrapBoolean> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (null == way ||
                    (!DingdingQywxSyncRecord.syncWay_week.equals(way) && !DingdingQywxSyncRecord.syncWay_year.equals(way))) {
                throw new SyncWayException();
            }
            DingdingQywxSyncRecord record = new DingdingQywxSyncRecord();
            record.setWay(way);
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
