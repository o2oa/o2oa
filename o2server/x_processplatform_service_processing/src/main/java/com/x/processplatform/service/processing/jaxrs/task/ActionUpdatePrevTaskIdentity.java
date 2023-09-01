package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.ListUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskProperties.PrevTask;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUpdatePrevTaskIdentity extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdatePrevTaskIdentity.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		final Bag bag = new Bag();
		bag.wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Task> os = emc.fetchIn(Task.class, ListTools.toList(Task.job_FIELDNAME), JpaObject.id_FIELDNAME,
					bag.wi.getTaskList());
			if (os.isEmpty()) {
				Wo wo = new Wo();
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			} else {
				bag.job = os.get(0).getJob();
			}
		}

		Callable<ActionResult<Wo>> callable = () -> {
			Wo wo = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Task> os = emc.listIn(Task.class, JpaObject.id_FIELDNAME, bag.wi.getTaskList());
				emc.beginTransaction(Task.class);
				for (Task o : os) {
					o.getProperties().setPrevTaskIdentityList(
							ListTools.trim(ListUtils.sum(o.getProperties().getPrevTaskIdentityList(),
									bag.wi.getPrevTaskIdentityList()), true, true));
					bag.wi.getPrevTaskList().stream().forEach(p -> {
						PrevTask prevTask = new PrevTask();
						prevTask.setCompletedTime(p.getCompletedTime());
						prevTask.setStartTime(p.getStartTime());
						prevTask.setPerson(p.getPerson());
						prevTask.setOpinion(p.getOpinion());
						prevTask.setIdentity(p.getIdentity());
						prevTask.setUnit(p.getUnit());
						prevTask.setRouteName(p.getRouteName());
						prevTask.setActivity(p.getActivity());
						prevTask.setActivityName(p.getActivityName());
						prevTask.setActivityToken(p.getActivityToken());
						prevTask.setActivityType(p.getActivityType());

						o.getProperties().getPrevTaskList().add(prevTask);
					});
					if (null != bag.wi.getPrevTask()) {
						PrevTask prevTask = new PrevTask();
						prevTask.setCompletedTime(bag.wi.getPrevTask().getCompletedTime());
						prevTask.setStartTime(bag.wi.getPrevTask().getStartTime());
						prevTask.setPerson(bag.wi.getPrevTask().getPerson());
						prevTask.setOpinion(bag.wi.getPrevTask().getOpinion());
						prevTask.setIdentity(bag.wi.getPrevTask().getIdentity());
						prevTask.setUnit(bag.wi.getPrevTask().getUnit());
						prevTask.setRouteName(bag.wi.getPrevTask().getRouteName());
						prevTask.setActivity(bag.wi.getPrevTask().getActivity());
						prevTask.setActivityName(bag.wi.getPrevTask().getActivityName());
						prevTask.setActivityToken(bag.wi.getPrevTask().getActivityToken());
						prevTask.setActivityType(bag.wi.getPrevTask().getActivityType());
						o.getProperties().setPrevTask(prevTask);
					}
					emc.check(o, CheckPersistType.all);
					wo.getValueList().add(o.getId());
				}
				emc.commit();
			}
			ActionResult<Wo> result = new ActionResult<>();
			result.setData(wo);
			return result;
		};

		return ProcessPlatformKeyClassifyExecutorFactory.get(bag.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private static class Bag {
		private Wi wi;
		private String job;
	}

	public static class Wo extends WrapStringList {

		private static final long serialVersionUID = -449564137697660569L;
	}

	public static class Wi extends WrapUpdatePrevTaskIdentity {

		private static final long serialVersionUID = -3748933646812429331L;

	}

}