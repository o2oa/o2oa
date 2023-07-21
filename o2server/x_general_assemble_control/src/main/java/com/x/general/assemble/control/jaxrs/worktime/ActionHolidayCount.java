package com.x.general.assemble.control.jaxrs.worktime;

import java.util.Date;
import java.util.List;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.tools.DateTools;

public class ActionHolidayCount extends BaseAction {
	private DateOperation dateOperation = new DateOperation();
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String startDate,String endDate) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		int holidayCount = 0;
		List<String> dateStringList = dateOperation.listDateStringBetweenDate(DateTools.parse(startDate), DateTools.parse(endDate));
		for(String dateString :dateStringList){
			Date dateObject = DateTools.parse(dateString);
			if(!Config.workTime().isWorkDay(dateObject)){
				holidayCount++;
			}
		}
		Wo wo = new Wo();
		wo.setValue(holidayCount);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapInteger {

	}
}
