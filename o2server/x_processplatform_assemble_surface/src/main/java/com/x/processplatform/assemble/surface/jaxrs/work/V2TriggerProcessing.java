package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2TriggerProcessingWo;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo;

class V2TriggerProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2TriggerProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute;{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();

		Param param = this.init(effectivePerson, id);

		ProcessingAttributes processingAttributes = new ProcessingAttributes();
		ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", param.work.getId(), "processing"), processingAttributes,
						param.work.getJob())
				.getData(ActionProcessingWo.class);

		boolean processingToNext = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.find(param.work.getId(), Work.class);
			if ((null == work)
					|| (!StringUtils.equalsIgnoreCase(param.work.getActivityToken(), work.getActivityToken()))) {
				processingToNext = true;
			}
		}
		if (processingToNext) {
			this.recordWorkProcessing(Record.TYPE_WORKTRIGGERPROCESSING, "", "", param.work.getJob(),
					param.workLog.getId(), param.identity, param.series);
		}
		Wo wo = new Wo();
		wo.setValue(processingToNext);
		result.setData(wo);
		return result;
	}

	private Param init(EffectivePerson effectivePerson, String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			param.identity = business.organization().identity()
					.getMajorWithPerson(effectivePerson.getDistinguishedName());
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			emc.beginTransaction(Work.class);
			// 标识数据被修改
			work.setDataChanged(true);
			param.work = work;
			WorkLog workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, work.getActivityToken());
			if (null == workLog) {
				// 如果没有workLog则修复这个问题,创建一个新的workLog
				Activity activity = business.getActivity(work);
				if (null == activity) {
					throw new ExceptionWorkNotExist(work.getActivity());
				}
				workLog = WorkLog.createFromWork(work, activity, work.getActivityToken(), new Date());
				emc.beginTransaction(WorkLog.class);
				emc.persist(workLog, CheckPersistType.all);
			}
			param.workLog = workLog;
			emc.commit();
		}
		return param;
	}

	private class Param {

		private Work work;
		private String series = StringTools.uniqueToken();
		private WorkLog workLog;
		private String identity;

	}

	public static class Wo extends V2TriggerProcessingWo {

		private static final long serialVersionUID = -2704637716301253584L;

	}

}