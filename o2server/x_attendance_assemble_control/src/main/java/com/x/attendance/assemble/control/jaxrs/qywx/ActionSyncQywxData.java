package com.x.attendance.assemble.control.jaxrs.qywx;

import java.util.Date;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.dingding.MoreThanSevenDayException;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;


public class ActionSyncQywxData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionSyncQywxData.class);

    public ActionResult<WrapBoolean> execute(EffectivePerson effectivePerson, String dateFrom, String dateTo) throws Exception {
        ActionResult<WrapBoolean> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (business.isManager(effectivePerson)) {
                if (null == dateFrom || null == dateTo) {
                    throw new SyncDateException();
                }
                Date from = DateTools.parse(dateFrom);
                Date to = DateTools.parse(dateTo);
                long gap = to.getTime() - from.getTime();
                if (gap < 0) {
                    throw new SyncDateException();
                }
                if ((gap / (1000 * 60 * 60 * 24)) > 6) {
                    throw new MoreThanSevenDayException();
                }
                Date wxTo = DateTools.parse(dateTo + " 23:59:59");//企业微信和钉钉查询时间不一样
                DingdingQywxSyncRecord record = new DingdingQywxSyncRecord();
                record.setDateFrom(from.getTime());
                record.setDateTo(wxTo.getTime());
                record.setStartTime(new Date());
                record.setType(DingdingQywxSyncRecord.syncType_qywx);
                record.setStatus(DingdingQywxSyncRecord.status_loading);
                emc.beginTransaction(DingdingQywxSyncRecord.class);
                emc.persist(record);
                emc.commit();
                //企业微信的处理队列
                ThisApplication.qywxQueue.send(record);
                result.setData(new WrapBoolean(true));
            } else {
                throw new ExceptionNotManager();
            }
        }
        return result;
    }

}
