package com.x.processplatform.assemble.surface.jaxrs.worklog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;

class ActionListAddSplitWithWork extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListAddSplitWithWork.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId) throws Exception {

		LOGGER.debug("execute:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> workId);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);

			Work work = emc.find(workId, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}

			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit().build();

			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}

			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, Work.job_FIELDNAME, work.getJob());

			WorkLogTree tree = new WorkLogTree(workLogs);

			WorkLogTree.Node current = tree.location(work);

			WorkLogTree.Nodes nodes = tree.up(current);

			List<Wo> wos = new ArrayList<>();

			List<WorkLog> os = new ArrayList<>();

			Stream<Node> splitNodes = nodes.stream()
					.filter(o -> Objects.equals(o.getWorkLog().getFromActivityType(), ActivityType.split)
							&& StringUtils.startsWith(StringUtils.join(work.getSplitTokenList(), ","),
									StringUtils.join(o.getWorkLog().getProperties().getSplitTokenList(), ",")));

			if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson,
					work.getApplication(), work.getProcess()))) {
				splitNodes.forEach(o -> o
						.upTo(ActivityType.manual, ActivityType.split, ActivityType.agent, ActivityType.choice,
								ActivityType.delay, ActivityType.embed, ActivityType.invoke, ActivityType.publish)
						.forEach(n -> {
							try {
								os.add(o.getWorkLog());
							} catch (Exception e) {
								LOGGER.error(e);
							}
						}));
			} else {
				splitNodes.forEach(o -> o
						.upTo(ActivityType.manual, ActivityType.split, ActivityType.agent, ActivityType.choice,
								ActivityType.delay, ActivityType.embed, ActivityType.invoke, ActivityType.publish)
						.forEach(n -> {
							try {
								Long count = emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
										effectivePerson.getDistinguishedName(), TaskCompleted.activityToken_FIELDNAME,
										n.getWorkLog().getFromActivityToken());
								if (count > 0) {
									os.add(o.getWorkLog());
								}
							} catch (Exception e) {
								LOGGER.error(e);
							}
						}));
			}

			wos = Wo.copier.copy(os);

			result.setData(wos);

			return result;
		}

	}

	public static class Wo extends WorkLog {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private List<WoTaskCompleted> taskCompletedList;

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = 4533878650515451989L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
	}

}
