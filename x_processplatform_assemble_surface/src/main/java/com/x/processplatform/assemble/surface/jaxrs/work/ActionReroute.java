package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;

class ActionReroute extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, String activityId,
			ActivityType activityType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(id);
			}
			WrapOutMap complex = this.getComplex(business, effectivePerson, work);
			Control control = (Control) complex.get("control");
			if (BooleanUtils.isNotTrue(control.getAllowReroute())) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} not allow reroute.");
			}
			Activity activity = business.getActivity(work);
			Activity destinationActivity = business.getActivity(activityId, activityType);
			/* 如果是管理员那么就不判断这里的条件了 */
			if (effectivePerson.isNotManager() && (!BooleanUtils.isTrue(activity.getAllowReroute()))) {
				throw new Exception(
						"activity{name:" + activity.getName() + ", id:" + activity.getId() + "} not allow reroute.");
			}
			if (!StringUtils.equals(work.getProcess(), destinationActivity.getProcess())) {
				throw new Exception("activity{name:" + destinationActivity.getName() + ", id:"
						+ destinationActivity.getId() + "} not in same process.");
			}
			work.setForceRoute(true);
			work.setDestinationActivity(destinationActivity.getId());
			work.setDestinationActivityType(destinationActivity.getActivityType());
			work.setDestinationRoute("");
			work.setDestinationRouteName("");
			emc.beginTransaction(Work.class);
			emc.check(work, CheckPersistType.all);
			this.removeTask(business, work);
			emc.commit();
			ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
					"work/" + URLEncoder.encode(work.getId(), "UTF-8") + "/processing", null);
			WrapOutId wrap = new WrapOutId(work.getId());
			result.setData(wrap);
			return result;
		}
	}

	private void removeTask(Business business, Work work) throws Exception {
		/* 删除可能的待办 */
		List<String> ids = business.task().listWithActivityToken(work.getActivityToken());
		if (!ids.isEmpty()) {
			business.entityManagerContainer().beginTransaction(Task.class);
			for (Task o : business.entityManagerContainer().list(Task.class, ids)) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
		}
	}

}
