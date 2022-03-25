package com.x.attendance.assemble.control.jaxrs.dingding;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListDingdingSyncRecord extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListDingdingSyncRecord.class);

    public ActionResult<List<Wo>> execute() throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<DingdingQywxSyncRecord> list = business.dingdingAttendanceFactory().findAllSyncRecordWithType(DingdingQywxSyncRecord.syncType_dingding);
            if (list != null && !list.isEmpty()) {
                List<Wo> wos = list.stream().map(record -> {
                    Wo wo = new Wo();
                    try {
                        wo = Wo.copier.copy(record, wo);
                        wo.formatDate();
                    }catch (Exception e) {
                        logger.error(e);
                    }
                    return wo;
                }).collect(Collectors.toList());
                result.setData(wos);
            }
        }
        return result;
    }


    public static class Wo extends DingdingQywxSyncRecord {
        static final WrapCopier<DingdingQywxSyncRecord, Wo> copier =
                WrapCopierFactory.wo(DingdingQywxSyncRecord.class, Wo.class, null, JpaObject.FieldsInvisible);

        @FieldDescribe("同步打卡记录的开始时间")
        private Date dateFromFormat;
        @FieldDescribe("同步打卡记录的结束时间")
        private Date dateToFormat;

        public void formatDate() {
            Date date = new Date();
            date.setTime(getDateFrom());
            setDateFromFormat(date);
            Date dateto = new Date();
            dateto.setTime(getDateTo());
            setDateToFormat(dateto);
        }


        public Date getDateFromFormat() {
            return dateFromFormat;
        }

        public void setDateFromFormat(Date dateFromFormat) {
            this.dateFromFormat = dateFromFormat;
        }

        public Date getDateToFormat() {
            return dateToFormat;
        }

        public void setDateToFormat(Date dateToFormat) {
            this.dateToFormat = dateToFormat;
        }
    }
}
