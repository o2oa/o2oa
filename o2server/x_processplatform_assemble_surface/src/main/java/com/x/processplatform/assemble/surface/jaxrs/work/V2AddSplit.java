package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddSplitWi;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class V2AddSplit extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2AddSplit.class);

	private EffectivePerson effectivePerson;
	private Work work;
	private WorkLog addSplitWorkLog;
	private Record record;
	private String series = StringTools.uniqueToken();
	private List<String> existTaskIds = new ArrayList<>();
	private V2AddSplitWi req = new V2AddSplitWi();
	private Wi wi;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		this.effectivePerson = effectivePerson;
		wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
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

			Node addSplitNode = this.findSplitNode(tree, currentNode);
			if (null == addSplitNode) {
				throw new ExceptionNoneSplitNode(work.getId());
			}
			addSplitWorkLog = addSplitNode.getWorkLog();

			if (BooleanUtils.isTrue(wi.getTrimExist())) {
				List<String> splitValues = ListUtils.subtract(wi.getSplitValueList(),
						this.existSplitValues(tree, addSplitNode));
				if (ListTools.isEmpty(splitValues)) {
					throw new ExceptionEmptySplitValueAfterTrim(work.getId());
				}
				req.setSplitValueList(splitValues);
			} else {
				if (ListTools.isEmpty(wi.getSplitValueList())) {
					throw new ExceptionEmptySplitValue(work.getId());
				}
				req.setSplitValueList(wi.getSplitValueList());
			}
			existTaskIds = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
		}

		List<String> ids = addSplit();
		processing(ids);
		record();
		Wo wo = Wo.copier.copy(record);
		result.setData(wo);
		return result;
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

	/**
	 * 进行回溯定位到前一次进行拆分的节点
	 */
	private Node findSplitNode(WorkLogTree tree, Node currentNode) {
		Nodes nodes = currentNode.upTo(ActivityType.split, ActivityType.manual, ActivityType.choice);
		if (!nodes.isEmpty()) {
			return nodes.get(0);
		}
		return null;
	}

	private List<String> addSplit() throws Exception {
		req.setWorkLog(addSplitWorkLog.getId());
		WrapStringList resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", work.getId(), "add", "split"), req, work.getJob())
				.getData(WrapStringList.class);
		if (ListTools.isEmpty(resp.getValueList())) {
			throw new ExceptionReroute(this.work.getId());
		}
		return resp.getValueList();

	}

	private void processing(List<String> ids) throws Exception {
		for (String id : ids) {
			ProcessingAttributes processingAttributes = new ProcessingAttributes();
			processingAttributes.setType(ProcessingAttributes.TYPE_ADDSPLIT);
			processingAttributes.setSeries(series);
			WoId processingResp = ThisApplication.context().applications()
					.putQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", id, "processing"), processingAttributes, work.getJob())
					.getData(WoId.class);
			if (StringUtils.isBlank(processingResp.getId())) {
				throw new ExceptionReroute(this.work.getId());
			}
		}
	}

	private void record() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			final List<String> nextTaskIdentities = new ArrayList<>();
			record = new Record(addSplitWorkLog);
			record.setPerson(effectivePerson.getDistinguishedName());
			record.setType(Record.TYPE_ADDSPLIT);
			record.getProperties().setElapsed(
					Config.workTime().betweenMinutes(record.getProperties().getStartTime(), record.getRecordTime()));
			/* 需要记录处理人,先查看当前用户有没有之前处理过的信息,如果没有,取默认身份 */
			TaskCompleted existTaskCompleted = emc.firstEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
					work.getJob(), TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName());
			record.setPerson(effectivePerson.getDistinguishedName());
			if (null != existTaskCompleted) {
				record.setIdentity(existTaskCompleted.getIdentity());
				record.setUnit(existTaskCompleted.getUnit());
			} else {
				record.setIdentity(
						business.organization().identity().getMajorWithPerson(effectivePerson.getDistinguishedName()));
				record.setUnit(business.organization().unit().getWithIdentity(record.getIdentity()));
			}
			List<String> ids = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
			ids = ListUtils.subtract(ids, existTaskIds);
			List<Task> list = emc.fetch(ids, Task.class,
					ListTools.toList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
							Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
							Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
			list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
					.forEach(o -> {
						Task task = o.getValue().get(0);
						NextManual nextManual = new NextManual();
						nextManual.setActivity(task.getActivity());
						nextManual.setActivityAlias(task.getActivityAlias());
						nextManual.setActivityName(task.getActivityName());
						nextManual.setActivityToken(task.getActivityToken());
						nextManual.setActivityType(task.getActivityType());
						for (Task t : o.getValue()) {
							nextManual.getTaskIdentityList().add(t.getIdentity());
							nextTaskIdentities.add(t.getIdentity());
						}
						record.getProperties().getNextManualList().add(nextManual);
					});
			/* 去重 */
			record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
		}
		WoId resp = ThisApplication.context().applications()
				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "job", work.getJob()), record, this.work.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionRecord(this.work.getId());
		}
	}

	public static class Wi extends V2AddSplitWi {

	}

	public static class Wo extends Record {

		private static final long serialVersionUID = -8410749558739884101L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoControl extends WorkControl {

	}

}