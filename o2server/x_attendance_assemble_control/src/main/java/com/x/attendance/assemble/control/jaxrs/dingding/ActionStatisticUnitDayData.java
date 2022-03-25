package com.x.attendance.assemble.control.jaxrs.dingding;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.dingdingstatistic.BaseAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

/**
 * Created by fancyLou on 2020-04-05.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionStatisticUnitDayData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionStatisticUnitDayData.class);

    ActionResult<WrapBoolean> execute(String year, String month, String day) throws Exception{
        ActionResult<WrapBoolean> result = new ActionResult<WrapBoolean>();
        if (StringUtils.isEmpty(year) || StringUtils.isEmpty(month) || StringUtils.isEmpty(day)) {
            throw new ExceptionSearchArgEmpty();
        }
        logger.info("开始执行全部部门考勤信息统计 year:"+year+", month:"+month);
        Date date = DateTools.parse(year+"-"+month+"-"+day);
        ThisApplication.unitStatisticQueue.send(date);
        result.setData(new WrapBoolean(true));
        return result;
    }
}
