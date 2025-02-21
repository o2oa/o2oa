package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;

class V3RetractPrepare extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3RetractPrepare.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowRetract().build();
			if (BooleanUtils.isFalse(control.getAllowManage()) && BooleanUtils.isFalse(control.getAllowRetract())) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Activity activity = business.getActivity(work);
			if (null == activity) {
				throw new ExceptionEntityNotExist(work.getActivity());
			}
			WorkLogTree workLogTree = new WorkLogTree(
					business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob()));
			Node node = workLogTree.location(work);
			Nodes nodes = node.downNextManual();
			for (Node n : nodes) {
				if (BooleanUtils.isNotTrue(n.getWorkLog().getConnected())) {
					Work w = emc.find(n.getWorkLog().getWork(), Work.class);
					if (null != w) {
						WoWork woWork = new WoWork();
						woWork.setActivity(w.getActivity());
						woWork.setActivityName(w.getActivityName());
						woWork.setActivityType(w.getActivityType());
						woWork.setTitle(w.getTitle());
						List<Task> tasks = emc.listEqual(Task.class, Task.work_FIELDNAME, work.getId());
						for (Task t : tasks) {
							WoTask woTask = new WoTask();
							woTask.setId(t.getId());
							woTask.setPerson(t.getPerson());
							woWork.getTaskList().add(woTask);
						}
						wo.getWorkList().add(woWork);
					}
				}
			}
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private List<WoWork> workList = new ArrayList<>();

		private static final long serialVersionUID = -5007785846454720742L;

		public List<WoWork> getWorkList() {
			return workList;
		}

		public void setWorkList(List<WoWork> workList) {
			this.workList = workList;
		}

	}

	public static class WoWork extends GsonPropertyObject {

		private static final long serialVersionUID = -7504772027599791850L;
		
		private String id;
		private String title;
		private String activity;
		private String activityName;
		private ActivityType activityType;
		private List<WoTask> taskList = new ArrayList<>();

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

	}

	public static class WoTask extends GsonPropertyObject {

		private static final long serialVersionUID = 7081560152254505167L;
		
		private String id;
		private String person;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

}
