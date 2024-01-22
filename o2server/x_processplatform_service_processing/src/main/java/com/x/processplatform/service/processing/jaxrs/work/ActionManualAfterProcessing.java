package com.x.processplatform.service.processing.jaxrs.work;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.WorkContext;
import com.x.processplatform.service.processing.processor.AeiObjects;

/**
 * 此方法不会修改数据,使用noblocking方式执行.
 */
class ActionManualAfterProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManualAfterProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		this.processing(wi.getTask(), wi.getRecord());

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;

	}

	public static class Wi extends ActionManualAfterProcessingWi {

		private static final long serialVersionUID = 94631246819740586L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -2252943558122337563L;

	}

	private void processing(Task task, Record record) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = this.getWork(business, task);
			callManualAfterProcessingScript(business, task, record, work);
		}

	}

	/**
	 * 获取work,如果没有(删除,完成)那么取同一job的work,如果还没有,那么取workCompleted转换成work
	 * 
	 * @param business
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private Work getWork(Business business, Task task) throws Exception {
		Work work = business.entityManagerContainer().find(task.getWork(), Work.class);
		if (null == work) {
			work = business.entityManagerContainer().firstEqual(Work.class, Work.job_FIELDNAME, task.getJob());
		}
		if (null == work) {
			WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
					WorkCompleted.job_FIELDNAME, task.getJob());
			if (null != workCompleted) {
				work = XGsonBuilder.convert(workCompleted, Work.class);
			}
		}
		return work;
	}

	private void callManualAfterProcessingScript(Business business, Task task, Record record, Work work)
			throws Exception {
		Manual manual = business.element().get(task.getActivity(), Manual.class);
		Process process = business.element().get(task.getProcess(), Process.class);
		if ((null != manual) && (null != process)) {
			boolean processHasManualAfterProcessingScript = processHasManualAfterProcessingScript(process);
			boolean hasManualAfterProcessingScript = hasManualAfterProcessingScript(manual);
			if (processHasManualAfterProcessingScript || hasManualAfterProcessingScript) {
				evalCallManualAfterProcessingScript(business, task, record, manual, process,
						processHasManualAfterProcessingScript, hasManualAfterProcessingScript, work);
			}
		}
	}

	private void evalCallManualAfterProcessingScript(Business business, Task task, Record record, Manual manual,
			Process process, boolean processHasManualAfterProcessingScript, boolean hasManualAfterProcessingScript,
			Work work) throws Exception {
		AeiObjects aeiObjects = new AeiObjects(business, work, manual, new ProcessingAttributes());
		GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings();
		WorkContext workContext = (WorkContext) bindings.get(GraalvmScriptingFactory.BINDING_NAME_WORKCONTEXT);
		workContext.bindTask(task);
		workContext.bindRecord(record);
		if (processHasManualAfterProcessingScript) {
			GraalvmScriptingFactory.eval(business.element().getCompiledScript(task.getApplication(), process,
					Business.EVENT_MANUALAFTERPROCESSING), bindings);
		}
		if (hasManualAfterProcessingScript) {
			GraalvmScriptingFactory.eval(business.element().getCompiledScript(task.getApplication(), manual,
					Business.EVENT_MANUALAFTERPROCESSING), bindings);
		}
	}

	private boolean hasManualAfterProcessingScript(Manual manual) {
		return ((null != manual) && (StringUtils.isNotEmpty(manual.getManualAfterProcessingScript())
				|| StringUtils.isNotEmpty(manual.getManualAfterProcessingScriptText())));
	}

	private boolean processHasManualAfterProcessingScript(Process process) {
		return ((null != process) && (StringUtils.isNotEmpty(process.getManualAfterProcessingScript())
				|| StringUtils.isNotEmpty(process.getManualAfterProcessingScriptText())));
	}
}