package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
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
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RollbackWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RollbackWo;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo;

class V2Rollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Rollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		Param param = this.init(effectivePerson, id, jsonElement);
		this.rollback(param.work, param.workLog, param.distinguishedNameList);
		this.processing(param.work, param.series);
		Record rec = this.recordWorkProcessing(Record.TYPE_ROLLBACK, "", param.opinion, param.work.getJob(),
				param.workLog.getId(), param.identity, param.series);
		Wo wo = Wo.copier.copy(rec);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;

	}

	private Param init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.opinion = wi.getOpinion();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.work = work;
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowRollback().build();

			if (BooleanUtils.isNotTrue(control.getAllowRollback())
					&& BooleanUtils.isNotTrue(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);

			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}
			param.workLog = workLog;

			Manual manual = (Manual) business.getActivity(workLog.getFromActivity(), ActivityType.manual);

			if (null == manual) {
				throw new ExceptionEntityNotExist(workLog.getFromActivity(), Manual.class);
			}

			param.distinguishedNameList = business.organization().distinguishedName()
					.list(wi.getDistinguishedNameList().stream().distinct().collect(Collectors.toList()));
			param.identity = business.organization().identity()
					.getMajorWithPerson(effectivePerson.getDistinguishedName());
		}
		return param;
	}

	private class Param {

		private String identity;
		private String opinion;
		private Work work;
		private WorkLog workLog;
		private List<String> distinguishedNameList;
		private String series = StringTools.uniqueToken();

	}

	private void rollback(Work work, WorkLog workLog, List<String> distinguishedNameList) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWi();
		req.setWorkLog(workLog.getId());
		req.setDistinguishedNameList(distinguishedNameList);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", work.getId(), "rollback"), req, work.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWo.class);
	}

	private void processing(Work work, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_ROLLBACK);
		req.setSeries(series);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", work.getId(), "processing"), req, work.getJob())
				.getData(ActionProcessingWo.class);
	}

	public static class Wi extends V2RollbackWi {

		private static final long serialVersionUID = 484540959465577790L;

	}

	public static class Wo extends V2RollbackWo {

		private static final long serialVersionUID = -8410749558739884101L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}