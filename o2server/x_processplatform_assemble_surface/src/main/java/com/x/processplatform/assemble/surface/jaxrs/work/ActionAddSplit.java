package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;

class ActionAddSplit extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/* 校验work是否存在 */
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			if (ListTools.isEmpty(wi.getSplitValueList())) {
				throw new ExceptionEmptySplitValue(work.getId());
			}
			Manual manual = business.manual().pick(work.getActivity());
			if (null == manual || BooleanUtils.isFalse(manual.getAllowAddSplit())
					|| (!BooleanUtils.isTrue(work.getSplitting()))) {
				throw new ExceptionCannotAddSplit(work.getId());
			}

			List<WorkLog> workLogs = this.listWorkLog(business, work);

			WorkLogTree tree = new WorkLogTree(workLogs);

			Node currentNode = tree.location(work);

			if (null == currentNode) {
				throw new ExceptionWorkLogWithActivityTokenNotExist(work.getActivityToken());
			}

			Node splitNode = this.findSplitNode(tree, currentNode);

			if (null == splitNode) {
				throw new ExceptionNoneSplitNode(work.getId());
			}

			Req req = new Req();

			req.setWorkLog(splitNode.getWorkLog().getId());

			if (BooleanUtils.isTrue(wi.getTrimExist())) {
				List<String> splitValues = ListUtils.subtract(wi.getSplitValueList(),
						this.existSplitValues(tree, splitNode));
				if (ListTools.isEmpty(splitValues)) {
					throw new ExceptionEmptySplitValueAfterTrim(work.getId());
				}
				req.setSplitValueList(splitValues);
			} else {
				req.setSplitValueList(wi.getSplitValueList());
			}

			List<Wo> wos = ThisApplication.context().applications()
					.putQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", work.getId(), "add", "split"), req)
					.getDataAsList(Wo.class);

			result.setData(wos);
			return result;
		}
	}

	private Node findSplitNode(WorkLogTree tree, Node currentNode) {
		Nodes nodes = currentNode.upTo(ActivityType.split, ActivityType.manual);
		if (!nodes.isEmpty()) {
			return nodes.get(0);
		}
		return null;
	}

	private List<String> existSplitValues(WorkLogTree tree, Node splitNode) {
		List<String> values = new ArrayList<>();
		for (Node node : splitNode.parents()) {
			for (Node o : tree.down(node)) {
				if (StringUtils.isNotEmpty(o.getWorkLog().getSplitValue())) {
					values.add(o.getWorkLog().getSplitValue());
				}
			}
		}
		values = ListTools.trim(values, true, true);
		return values;
	}

	private List<WorkLog> listWorkLog(Business business, Work work) throws Exception {
		return business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob());
	}

	public static class Req extends GsonPropertyObject {

		@FieldDescribe("添加的拆分值.")
		private List<String> splitValueList;

		@FieldDescribe("工作日志.")
		private String workLog;

		public List<String> getSplitValueList() {
			return splitValueList;
		}

		public void setSplitValueList(List<String> splitValueList) {
			this.splitValueList = splitValueList;
		}

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("添加的拆分值.")
		private List<String> splitValueList;

		@FieldDescribe("排除已经存在的拆分值.")
		private Boolean trimExist;

		public List<String> getSplitValueList() {
			return splitValueList;
		}

		public void setSplitValueList(List<String> splitValueList) {
			this.splitValueList = splitValueList;
		}

		public Boolean getTrimExist() {
			return trimExist;
		}

		public void setTrimExist(Boolean trimExist) {
			this.trimExist = trimExist;
		}

	}

	public static class Wo extends Work {

		private static final long serialVersionUID = 8122551349295505134L;

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class,
				ListTools.toList(Work.id_FIELDNAME, Work.activityName_FIELDNAME), null);

		private List<WoTask> taskList = new ArrayList<>();

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 5196447462619948056L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class,
				ListTools.toList(Task.person_FIELDNAME, Task.unit_FIELDNAME), null);

	}

}