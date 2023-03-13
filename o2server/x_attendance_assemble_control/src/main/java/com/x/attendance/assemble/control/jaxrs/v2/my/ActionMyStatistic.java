package com.x.attendance.assemble.control.jaxrs.v2.my;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ActionStatisticWithFilter;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.x_attendance_assemble_control;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/3/13.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionMyStatistic extends BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMyStatistic.class);

    ActionResult<ActionStatisticWithFilter.Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {

        Wi thisWi = this.convertToWrapIn(jsonElement, Wi.class);
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
        ActionResult<ActionStatisticWithFilter.Wo> result = new ActionResult<>();
        ActionStatisticWithFilter.Wi wi = new ActionStatisticWithFilter.Wi();
        wi.setStartDate(thisWi.getStartDate());
        wi.setEndDate(thisWi.getEndDate());
        wi.setFilter(person.getDistinguishedName());
        List<ActionStatisticWithFilter.Wo> res = ThisApplication.context().applications().postQuery( x_attendance_assemble_control.class, "v2/detail/statistic/filter", wi).getDataAsList(ActionStatisticWithFilter.Wo.class);
        if (res != null && !res.isEmpty()) {
            result.setData(res.get(0));
        } else {
            result.setData(new ActionStatisticWithFilter.Wo());
        }
        return result;
    }

    public static class Wi extends GsonPropertyObject {


        @FieldDescribe("开始日期，包含")
        private String startDate;
        @FieldDescribe("结束日期， 包含")
        private String endDate;


        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}
