package com.x.attendance.assemble.control.jaxrs.v2.my;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2RestDayHelper;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionDateIsRestDay extends BaseAction {

  private static final Logger logger = LoggerFactory.getLogger(ActionDateIsRestDay.class);

  ActionResult<Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
    ActionResult<Wo> result = new ActionResult<>();
    List<String> restDateList = new ArrayList<>();
    Wo wo = new Wo();
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      Business business = new Business(emc);
      Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
      if (wi.getDateList() == null || wi.getDateList().isEmpty()) {
        logger.info("DateList 是空的！");
        wo.setRestDateList(restDateList);
        result.setData(wo);
        return result;
      }
      restDateList = AttendanceV2RestDayHelper.listRestDate(business, person.getDistinguishedName(), wi.getDateList());
      wo.setRestDateList(restDateList);
      result.setData(wo);
      if (logger.isDebugEnabled()) {
        logger.debug("restDateList: " + ListTools.toStringJoin(restDateList));
      }
      return result;
    }
  }

  public static class Wi extends GsonPropertyObject {

    private static final long serialVersionUID = 7874429260551956123L;
    
    @FieldDescribe("查询是否休息日的日期列表，yyyy-MM-dd")
    private List<String> dateList;

    public List<String> getDateList() {
      return dateList;
    }

    public void setDateList(List<String> dateList) {
      this.dateList = dateList;
    }

  }

  public static class Wo extends GsonPropertyObject {
    private static final long serialVersionUID = -6596581682844590658L;
    
    @FieldDescribe("休息日的日期列表，yyyy-MM-dd")
    private List<String> restDateList;

    public List<String> getRestDateList() {
      return restDateList;
    }

    public void setRestDateList(List<String> restDateList) {
      this.restDateList = restDateList;
    }

  }

}
