package com.x.processplatform.service.processing.jaxrs.work;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.ProcessingSignal;
import com.x.processplatform.service.processing.Processing;

class ActionProcessing extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		String job;

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			wi.setDebugger(effectivePerson.getDebugger());

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			job = work.getJob();

		}

		ActionResult<Wo> result = new ActionResult<>();

		result.setData(new Wo());

		ProcessPlatformExecutorFactory.get(job).submit(new CallableExecute(wi, id, result));

		String value = wi.signal().read();

		if (StringUtils.isNotBlank(value)) {
			result.getData().setId(id);
			result.getData().setOccurProcessingSignal(true);
			result.getData().setProcessingSignal(gson.fromJson(value, ProcessingSignal.class));
			return result;
		} else {
			return result;
		}

	}

	private class CallableExecute implements Callable<ActionResult<Wo>> {
		private Wi wi;
		private String id;
		private ActionResult<Wo> result;

		private CallableExecute(Wi wi, String id, ActionResult<Wo> result) {
			this.wi = wi;
			this.id = id;
			this.result = result;
		}

		public ActionResult<Wo> call() throws Exception {
			Processing processing = new Processing(wi);
			processing.processing(id);
			result.getData().setId(id);
			return result;
		}
	}

	public static class Wi extends ProcessingAttributes {

	}

	public static class Wo extends WoId {

		@FieldDescribe("是否发生流程信号.")
		private Boolean occurProcessingSignal = false;

		@FieldDescribe("流程信号.")
		private ProcessingSignal processingSignal;

		public Boolean getOccurProcessingSignal() {
			return occurProcessingSignal;
		}

		public void setOccurProcessingSignal(Boolean occurProcessingSignal) {
			this.occurProcessingSignal = occurProcessingSignal;
		}

		public ProcessingSignal getProcessingSignal() {
			return processingSignal;
		}

		public void setProcessingSignal(ProcessingSignal processingSignal) {
			this.processingSignal = processingSignal;
		}

	}

}