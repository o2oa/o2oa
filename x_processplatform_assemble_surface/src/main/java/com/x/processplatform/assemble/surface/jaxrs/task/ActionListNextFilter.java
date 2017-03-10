package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInFilter;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;

class ActionListNextFilter extends ActionBase {

	ActionResult<List<WrapOutTask>> execute(EffectivePerson effectivePerson, String id, Integer count,
			JsonElement jsonElement) throws Exception {
		ActionResult<List<WrapOutTask>> result = new ActionResult<>();
		WrapInFilter wrapIn = this.convertToWrapIn(jsonElement, WrapInFilter.class);
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		equals.put("person", effectivePerson.getName());
		if (ListTools.isNotEmpty(wrapIn.getApplicationList())) {
			ins.put("application", wrapIn.getApplicationList());
		}
		if (ListTools.isNotEmpty(wrapIn.getProcessList())) {
			ins.put("process", wrapIn.getProcessList());
		}
		if (ListTools.isNotEmpty(wrapIn.getCreatorCompanyList())) {
			ins.put("creatorCompany", wrapIn.getCreatorCompanyList());
		}
		if (ListTools.isNotEmpty(wrapIn.getCreatorDepartmentList())) {
			ins.put("creatorDepartment", wrapIn.getCreatorDepartmentList());
		}
		if (ListTools.isNotEmpty(wrapIn.getStartTimeMonthList())) {
			ins.put("startTimeMonth", wrapIn.getStartTimeMonthList());
		}
		if (ListTools.isNotEmpty(wrapIn.getActivityNameList())) {
			ins.put("activityName", wrapIn.getActivityNameList());
		}
		if (StringUtils.isNotEmpty(wrapIn.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put("title", key);
				likes.put("opinion", key);
				likes.put("serial", key);
			}
		}
		result = this.standardListNext(taskOutCopier, id, count, "sequence", equals, null, likes, ins, null, null, null,
				true, DESC);
		return result;
	}

}
