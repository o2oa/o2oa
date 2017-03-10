package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

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
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;

class ActionListNextWithFilter extends ActionBase {

	ActionResult<List<WrapOutWorkCompleted>> execute(EffectivePerson effectivePerson, String id, Integer count,
			String applicationFlag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInFilter wrapIn = this.convertToWrapIn(jsonElement, WrapInFilter.class);
			Business business = new Business(emc);
			ActionResult<List<WrapOutWorkCompleted>> result = new ActionResult<>();
			EqualsTerms equals = new EqualsTerms();
			InTerms ins = new InTerms();
			LikeTerms likes = new LikeTerms();
			/* 可能为空 */
			Application application = business.application().pick(applicationFlag);
			String applicationId = null == application ? applicationFlag : application.getId();
			equals.put("creatorPerson", effectivePerson.getName());
			equals.put("application", applicationId);
			// if (ListTools.isNotEmpty(wrapIn.getApplicationList())) {
			// ins.put("application", wrapIn.getApplicationList());
			// }
			if (ListTools.isNotEmpty(wrapIn.getProcessList())) {
				ins.put("process", wrapIn.getProcessList());
			}
			if (ListTools.isNotEmpty(wrapIn.getStartTimeMonthList())) {
				ins.put("startTimeMonth", wrapIn.getStartTimeMonthList());
			}
			if (ListTools.isNotEmpty(wrapIn.getCompletedTimeMonthList())) {
				ins.put("completedTimeMonth", wrapIn.getCompletedTimeMonthList());
			}
			if (StringUtils.isNotEmpty(wrapIn.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("title", key);
				}
			}
			result = this.standardListNext(workCompletedOutCopier, id, count, "sequence", equals, null, likes, ins,
					null, null, null, true, DESC);
			/* 添加权限 */
			if (null != result.getData()) {
				for (WrapOutWorkCompleted wrap : result.getData()) {
					WorkCompleted o = emc.find(wrap.getId(), WorkCompleted.class);
					Control control = business.getControlOfWorkCompleted(effectivePerson, o);
					wrap.setControl(control);
				}
			}
			return result;
		}
	}
}