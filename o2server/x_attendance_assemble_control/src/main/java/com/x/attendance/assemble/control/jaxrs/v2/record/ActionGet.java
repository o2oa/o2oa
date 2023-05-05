package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/4/17.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionGet extends BaseAction {


    ActionResult<Wo> execute(String id) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2CheckInRecord record = emc.find(id, AttendanceV2CheckInRecord.class);
            if (record == null) {
                throw new ExceptionNotExistObject("打卡记录（" + id+"）");
            }
            result.setData( Wo.copier.copy(record));
        }
        return result;
    }

    public static class Wo extends AttendanceV2CheckInRecord {

        private static final long serialVersionUID = 4314303275189640803L;
        static WrapCopier<AttendanceV2CheckInRecord, Wo> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
