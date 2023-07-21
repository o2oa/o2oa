package com.x.attendance.assemble.control.jaxrs.v2.my;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWi;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWo;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

/**
 * Created by fancyLou on 2023/3/13.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionMyStatistic extends BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMyStatistic.class);

    ActionResult<StatisticWo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {

        StatisticWi thisWi = this.convertToWrapIn(jsonElement, StatisticWi.class);
        if (StringUtils.isEmpty(thisWi.getStartDate())) {
            throw new ExceptionEmptyParameter("开始日期");
        }
        if (StringUtils.isEmpty(thisWi.getEndDate())) {
            throw new ExceptionEmptyParameter("结束日期");
        }

        Date startDate = DateTools.parse(thisWi.getStartDate(), DateTools.format_yyyyMMdd); // 检查格式
        Date endDate = DateTools.parse(thisWi.getEndDate(), DateTools.format_yyyyMMdd); // 检查格式
        if (startDate.after(endDate)) {
            throw new ExceptionDateEndBeforeStartError();
        }
        ActionResult<StatisticWo> result = new ActionResult<>();
        thisWi.setFilter(person.getDistinguishedName());
        List<StatisticWo> res = ThisApplication.context().applications().postQuery( x_attendance_assemble_control.class, "v2/detail/statistic/filter", thisWi).getDataAsList(StatisticWo.class);
        if (res != null && !res.isEmpty()) {
            result.setData(res.get(0));
        } else {
            result.setData(new StatisticWo());
        }
        return result;
    }

}
