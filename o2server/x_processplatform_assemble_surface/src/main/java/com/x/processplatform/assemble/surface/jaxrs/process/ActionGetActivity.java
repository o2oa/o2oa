package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;

class ActionGetActivity extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String activityType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Activity activity = emc.find(id, ActivityType.getClassOfActivityType(ActivityType.valueOf(activityType)));
			if (null == activity) {
				throw new ExceptionEntityNotExist(id, Activity.class);
			}
			Wo wo = new Wo();
			wo.setActivityType(activityType);
			wo.setActivity(activity);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private String activityType;

		private Activity activity;

		public String getActivityType() {
			return activityType;
		}

		public void setActivityType(String activityType) {
			this.activityType = activityType;
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}
	}
}
