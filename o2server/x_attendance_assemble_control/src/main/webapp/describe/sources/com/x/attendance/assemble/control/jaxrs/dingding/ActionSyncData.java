package com.x.attendance.assemble.control.jaxrs.dingding;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.Date;


public class ActionSyncData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionSyncData.class);

    public ActionResult<WrapBoolean> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<WrapBoolean> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (null == wi.getWay() ||
                    (!DingdingQywxSyncRecord.syncWay_week.equals(wi.getWay()) && !DingdingQywxSyncRecord.syncWay_year.equals(wi.getWay()))) {
                throw new SyncWayException();
            }
            DingdingQywxSyncRecord record = new DingdingQywxSyncRecord();
            record.setWay(wi.getWay());
            record.setStartTime(new Date());
            record.setType(DingdingQywxSyncRecord.syncType_dingding);
            record.setStatus(DingdingQywxSyncRecord.status_loading);
            emc.beginTransaction(DingdingQywxSyncRecord.class);
            emc.persist(record);
            emc.commit();
            ThisApplication.dingdingQueue.executing(record);
            result.setData(new WrapBoolean(true));
        }
        return result;
    }

    public static class Wi extends DingdingQywxSyncRecord {
        public static WrapCopier<Wi, DingdingQywxSyncRecord> copier = WrapCopierFactory.wi(Wi.class,
                DingdingQywxSyncRecord.class, null, JpaObject.FieldsUnmodify);
    }
}
