package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
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
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2AddManualTaskIdentityMatrixWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;

class V2AddManualTaskIdentityMatrix extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2AddManualTaskIdentityMatrix.class);

	// 当前提交的串号
	private final String series = StringTools.uniqueToken();
	private Wi wi;
	// 当前执行用户
	private EffectivePerson effectivePerson;
	// 根据输入得到的待办
	private Work work = null;
	// 指定的身份
	private String identity = null;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		}
		this.init(effectivePerson, id, jsonElement);
		this.add(wi.getOptionList(), wi.getRemove());
		this.processingWork(work);
		return result();
	}

	private void init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.effectivePerson = effectivePerson;
			this.wi = this.convertToWrapIn(jsonElement, Wi.class);
			this.work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			this.identity = business.organization().identity().get(wi.getIdentity());
		}
	}

	private void add(List<V2AddManualTaskIdentityMatrixWi.Option> options, Boolean remove) throws Exception {
		V2AddManualTaskIdentityMatrixWi req = new V2AddManualTaskIdentityMatrixWi();
		req.setIdentity(identity);
		req.setOptionList(options);
		req.setRemove(remove);
		WrapBoolean resp = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class, Applications.joinQueryUri("work", "v2",
						work.getId(), "add", "manual", "task", "identity", "matrix"), req, work.getJob())
				.getData(WrapBoolean.class);
		if (BooleanUtils.isNotTrue(resp.getValue())) {
			throw new ExceptionAddManualTaskIdentityMatrix(work.getId());
		}
	}

	private void processingWork(Work work) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_TASKADD);
		req.setSeries(this.series);
		WoId resp = ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", work.getId(), "processing"), req, work.getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionProcessingWork(work.getId());
		}
	}

	private ActionResult<Wo> result() {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends V2AddManualTaskIdentityMatrixWi {

		private static final long serialVersionUID = -6251874269093504136L;

	}

	public static class WoControl extends WorkControl {

		private static final long serialVersionUID = -8675239528577375846L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 8155067200427920853L;

	}

}