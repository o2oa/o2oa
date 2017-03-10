package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInFilter;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;

class ActionListPrevWithFilter extends ActionBase {

	ActionResult<List<WrapOutWork>> execute(EffectivePerson effectivePerson, String id, Integer count,
			String applicationFlag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutWork>> result = new ActionResult<>();
			WrapInFilter wrapIn = this.convertToWrapIn(jsonElement, WrapInFilter.class);
			Business business = new Business(emc);
			EqualsTerms equals = new EqualsTerms();
			InTerms ins = new InTerms();
			LikeTerms likes = new LikeTerms();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationFlag);
			}
			equals.put("application", application.getId());
			equals.put("creatorPerson", effectivePerson.getName());
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
			if (ListTools.isNotEmpty(wrapIn.getWorkStatusList())) {
				ins.put("workStatus", wrapIn.getWorkStatusList());
			}
			if (StringUtils.isNotEmpty(wrapIn.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("title", key);
				}
			}

			result = this.standardListPrev(workOutCopier, id, count, "sequence", equals, null, likes, ins, null, null,
					null, true, DESC);
			/* 添加权限 */
			if (null != result.getData()) {
				for (WrapOutWork wrap : result.getData()) {
					Work o = emc.find(wrap.getId(), Work.class);
					Control control = business.getControlOfWorkList(effectivePerson, o);
					wrap.setControl(control);
				}
			}
			return result;
		}
	}
}
