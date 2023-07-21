package com.x.processplatform.assemble.surface.jaxrs.work;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.express.ProcessingAttributes;

/**
 * 
 * 
 * 废弃,不再使用,改用V2Reroute
 */
@Deprecated(forRemoval = true)
class ActionReroute extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String activityId, ActivityType activityType,
			JsonElement jsonElement) throws Exception {
		Work work;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			Activity activity = business.getActivity(work);
			Activity destinationActivity = business.getActivity(activityId, activityType);
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowReroute().build();
			if (BooleanUtils.isNotTrue(control.getAllowReroute())) {
				throw new ExceptionRerouteDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						destinationActivity.getName());
			}
			if (!StringUtils.equals(work.getProcess(), activity.getProcess())) {
				throw new ExceptionProcessNotMatch();
			}
		}
		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", work.getId(), "reroute", "activity", activityId), wi, work.getJob());
		Wo wo = new Wo();
		wo.setId(work.getId());
		result.setData(wo);
		return result;
	}

	public static class Wi extends ProcessingAttributes {

		private static final long serialVersionUID = -5095621432545025519L;

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -8410749558739884101L;
	}

}
