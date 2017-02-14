package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInFilter;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTaskCompleted;

class ActionListPrevFilter extends ActionBase {

	ActionResult<List<WrapOutTaskCompleted>> execute(EffectivePerson effectivePerson, String id, Integer count,
			WrapInFilter wrapIn) throws Exception {
		ActionResult<List<WrapOutTaskCompleted>> result = new ActionResult<>();
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
		if (ListTools.isNotEmpty(wrapIn.getCompletedTimeMonthList())) {
			ins.put("completedTimeMonth", wrapIn.getCompletedTimeMonthList());
		}
		if (ListTools.isNotEmpty(wrapIn.getActivityNameList())) {
			ins.put("activityName", wrapIn.getActivityNameList());
		}
		if (StringUtils.isNotEmpty(wrapIn.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put("title", key);
				likes.put("opinion", key);
			}
		}
		result = this.standardListPrev(taskCompletedOutCopier, id, count, "sequence", equals, null, likes, ins, null,
				null, null, true, DESC);
		return result;
	}

}
