package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
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
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWo;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo;

class ActionRollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, flag:{}, jsonElement:{}", effectivePerson::getDistinguishedName, () -> flag,
				() -> jsonElement);

		Param param = this.init(effectivePerson, flag, jsonElement);

		String workId = this.rollback(param);

		this.processing(workId, param.series);

		Record rec = this.recordWorkProcessing(Record.TYPE_ROLLBACK, "", param.opinion, param.workCompleted.getJob(),
				param.workLog.getId(), param.identity, param.series);

		Wo wo = Wo.copier.copy(rec);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;

	}

	private Param init(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.opinion = wi.getOpinion();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.flag(flag, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(flag, WorkCompleted.class);
			}
			param.workCompleted = workCompleted;
			Application application = business.application().pick(workCompleted.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(workCompleted.getApplication(), Application.class);
			}
			Process process = business.process().pick(workCompleted.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
			}
			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}
			if (BooleanUtils.isTrue(workLog.getSplitting())) {
				throw new ExceptionSplittingNotRollback(workCompleted.getId(), workLog.getId());
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowRollback().enableAllowManage().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage())
					&& BooleanUtils.isNotTrue(control.getAllowRollback())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			param.workCompleted = workCompleted;
			param.workLog = workLog;
			param.distinguishedNameList = business.organization().distinguishedName()
					.list(wi.getDistinguishedNameList().stream().distinct().collect(Collectors.toList()));
			param.identity = business.organization().identity()
					.getMajorWithPerson(effectivePerson.getDistinguishedName());
			return param;
		}
	}

	private String rollback(Param param) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionRollbackWi req = new com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionRollbackWi();
		req.setWorkLog(param.workLog.getId());
		req.setDistinguishedNameList(param.distinguishedNameList);
		com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionRollbackWo resp = ThisApplication
				.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("workcompleted", param.workCompleted.getId(), "rollback"), req)
				.getData(
						com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionRollbackWo.class);
		return resp.getId();
	}

	private void processing(String workId, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_ROLLBACK);
		req.setSeries(series);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", workId, "processing"), req, workId)
				.getData(ActionProcessingWo.class);
	}

	public class Param {

		private WorkCompleted workCompleted;
		private WorkLog workLog;
		private List<String> distinguishedNameList;
		private String series = StringTools.uniqueToken();
		private String opinion;
		private String identity;

	}

	public static class Wi extends ActionRollbackWi {

		private static final long serialVersionUID = 1966814422721596072L;

	}

	public static class Wo extends ActionRollbackWo {

		private static final long serialVersionUID = -6048816634681644627L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}