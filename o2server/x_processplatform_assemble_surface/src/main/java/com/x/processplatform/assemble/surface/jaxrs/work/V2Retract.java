package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RetractWo;

class V2Retract extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Retract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();

		Param param = init(effectivePerson, id);

		this.retract(param);

		this.processing(param);

		Record rec = this.recordWorkProcessing(Record.TYPE_RETRACT, "", "", param.work.getJob(), param.workLog.getId(),
				param.taskCompleted.getIdentity(), param.series);

		result.setData(Wo.copier.copy(rec));

		return result;

	}

	private Param init(EffectivePerson effectivePerson, String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.work = work;
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowRetract().build();
			if (BooleanUtils.isFalse(control.getAllowManage()) && BooleanUtils.isFalse(control.getAllowRetract())) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Activity activity = business.getActivity(work);
			if (null == activity) {
				throw new ExceptionEntityNotExist(work.getActivity());
			}
			WorkLog workLog = findWorkLog(effectivePerson, business, work);
			if (null == workLog) {
				throw new ExceptionRetractNoWorkLog(work.getId());
			}
			param.workLog = workLog;
			if (emc.countEqualAndEqualAndNotEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob(),
					TaskCompleted.activityToken_FIELDNAME, work.getActivityToken(), TaskCompleted.joinInquire_FIELDNAME,
					false) > 0) {
				throw new ExceptionRetractNoneTaskCompleted(work.getTitle(), work.getId());
			}
			TaskCompleted taskCompleted = findLastTaskCompleted(effectivePerson, business, workLog);
			if (null == taskCompleted) {
				throw new ExceptionNoTaskCompletedToRetract(param.workLog.getId(),
						effectivePerson.getDistinguishedName());
			}
			param.taskCompleted = taskCompleted;
			Activity destinationActivity = business.getActivity(taskCompleted.getActivity(),
					taskCompleted.getActivityType());
			if (null == destinationActivity) {
				throw new ExceptionEntityNotExist(taskCompleted.getActivity());
			}

		}
		return param;
	}

	private class Param {
		private WorkLog workLog;
		private TaskCompleted taskCompleted;
		private Work work;
		private String series = StringTools.uniqueToken();
	}

	private WorkLog findWorkLog(EffectivePerson effectivePerson, Business business, Work work) throws Exception {
		WorkLogTree workLogTree = new WorkLogTree(
				business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob()));
		// 是否可以召回
		WorkLog workLog = null;
		Node node = workLogTree.location(work);
		if (null != node) {
			Nodes ups = node.upTo(ActivityType.manual, ActivityType.agent, ActivityType.choice, ActivityType.delay,
					ActivityType.embed, ActivityType.invoke, ActivityType.parallel, ActivityType.split,
					ActivityType.publish);
			for (Node o : ups) {
				if (business.entityManagerContainer().countEqualAndEqual(TaskCompleted.class,
						TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName(),
						TaskCompleted.activityToken_FIELDNAME, o.getWorkLog().getFromActivityToken()) > 0) {
					workLog = o.getWorkLog();
					break;
				}
			}
		}
		return workLog;
	}

	private TaskCompleted findLastTaskCompleted(EffectivePerson effectivePerson, Business business, WorkLog workLog)
			throws Exception {
		List<TaskCompleted> list = business.entityManagerContainer().listEqualAndEqualAndEqual(TaskCompleted.class,
				TaskCompleted.job_FIELDNAME, workLog.getJob(), TaskCompleted.activityToken_FIELDNAME,
				workLog.getFromActivityToken(), TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName());
		return list.stream().sorted(Comparator.comparing(TaskCompleted::getStartTime).reversed()).findFirst()
				.orElse(null);
	}

	private void retract(Param param) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWi();
		req.setTaskCompleted(param.taskCompleted.getId());
		req.setWorkLog(param.workLog.getId());
		com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWo resp = ThisApplication.context()
				.applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", param.work.getId(), "retract"), req,
						param.work.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWo.class);
		if (BooleanUtils.isNotTrue(resp.getValue())) {
			throw new ExceptionRetract(param.work.getId());
		}
	}

	private void processing(Param param) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_RETRACT);
		req.setSeries(param.series);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", param.work.getId(), "processing"), req, param.work.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo.class);
	}

	public static class Wo extends V2RetractWo {

		private static final long serialVersionUID = -5007785846454720742L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
