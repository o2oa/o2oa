package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;

class ActionRetract extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRetract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			Activity activity = business.getActivity(work);

			if (null == activity) {
				throw new ExceptionEntityNotExist(work.getActivity(), "activity");
			}

			WorkLogTree workLogTree = new WorkLogTree(
					emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob()));

			/* 是否可以召回 */
			WorkLog workLog = null;
			if (PropertyTools.getOrElse(activity, Manual.allowRetract_FIELDNAME, Boolean.class, false)) {
				Node node = workLogTree.location(work);
				if (null != node) {
					Nodes ups = node.upTo(ActivityType.manual, ActivityType.agent, ActivityType.choice,
							ActivityType.delay, ActivityType.embed, ActivityType.invoke);
					for (Node o : ups) {
						if (business.entityManagerContainer().countEqualAndEqual(TaskCompleted.class,
								TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName(),
								TaskCompleted.activityToken_FIELDNAME, o.getWorkLog().getFromActivityToken()) > 0) {
							workLog = o.getWorkLog();
							break;
						}
					}
				}
			}

			if (null == workLog) {
				throw new ExceptionRetractNoneWorkLog(work.getId());
			}

			Req req = new Req();
			req.setWorkLog(workLog.getId());
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("work", work.getId(), "rollback"), req);
			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Req {

		private String workLog;

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

	}

	public static class Wo extends WoId {
	}

//	private WorkLog getWorkLog(Business business, TaskCompleted taskCompleted) throws Exception {
//		List<String> ids = business.workLog().listWithFromActivityToken(taskCompleted.getActivityToken());
//		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
//			if (StringUtils.equals(o.getWork(), taskCompleted.getWork())) {
//				return o;
//			}
//		}
//		return null;
//	}
}