package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Process;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;

class ActionGetActivity extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetActivity.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String activityType) throws Exception {

		LOGGER.debug("execute:{}, id:{}, activityType:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> activityType);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Class<? extends Activity> cls = ActivityType.getClassOfActivityType(ActivityType.valueOf(activityType));
			if(cls == null){
				throw new ExceptionEntityNotExist(id);
			}
			Activity activity = emc.find(id, cls);
			if (null == activity) {
				List<? extends Activity> list = emc.listEqual(cls, Activity.unique_FIELDNAME, id);
				if(list.size() == 1){
					activity = list.get(0);
				}else {
					for (Activity o : list) {
						Process process = emc.find(o.getProcess(), Process.class);
						if (BooleanUtils.isNotFalse(process.getEditionEnable())) {
							activity = o;
							break;
						}
					}
				}
			}
			if (null == activity) {
				throw new ExceptionEntityNotExist(id);
			}
			Wo wo = new Wo();
			wo.setActivityType(activityType);
			wo.setActivity(activity);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7277488664634240645L;

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
