package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ActionListDingdingSyncRecord extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListDingdingSyncRecord.class);

    public ActionResult<List<Wo>> execute() throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<DingdingQywxSyncRecord> list = business.dingdingAttendanceFactory().findAllDingdingSyncRecord();
            if (list != null && !list.isEmpty()) {
                List<Wo> wos = list.stream().map(record -> {
                    Wo wo = new Wo();
                    try {
                        wo = Wo.copier.copy(record, wo);
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
    }
}
