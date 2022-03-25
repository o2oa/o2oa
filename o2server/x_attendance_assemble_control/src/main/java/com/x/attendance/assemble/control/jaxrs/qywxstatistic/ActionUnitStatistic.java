package com.x.attendance.assemble.control.jaxrs.qywxstatistic;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.dingdingstatistic.BaseAction;
import com.x.attendance.assemble.control.jaxrs.dingdingstatistic.EmptyArgsException;
import com.x.attendance.entity.StatisticQywxUnitForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;

/**
 * Created by fancyLou on 2020-04-07.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionUnitStatistic extends BaseAction {


    ActionResult<Wo> execute(String unit, String year, String month) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(unit) || StringUtils.isEmpty(year) || StringUtils.isEmpty(month)) {
            throw new EmptyArgsException();
        }
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<StatisticQywxUnitForMonth> list = business.dingdingAttendanceFactory().findQywxUnitStatistic(unit, year, month);
            if (list!=null && !list.isEmpty()) {
                result.setData(Wo.copier.copy(list.get(0)));
            }else {
                result.setData(new Wo());
            }
        }
        return result;
    }



    public static class Wo extends StatisticQywxUnitForMonth {
        static final WrapCopier<StatisticQywxUnitForMonth, Wo> copier = WrapCopierFactory.wo(StatisticQywxUnitForMonth.class, Wo.class,
                null, JpaObject.FieldsInvisible);

    }
}
