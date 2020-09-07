package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Optional;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.log.SignalStack;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingSignalWo;
import com.x.processplatform.service.processing.ThisApplication;

class ActionProcessingSignal extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String series) throws Exception {

		Thread.sleep(Config.processPlatform().getProcessingSignalThreshold());

		Optional<SignalStack> optional = ThisApplication.getProcessingToProcessingSignalStack().find(id, series);

		Wo wo = new Wo();

		if (optional.isPresent()) {
			optional.get().forEach(o -> {
				if ((null != o.getManualExecute()) || (null != o.getSplitExecute())) {
					wo.getSignalStack().add(o);
				}
			});
		} else {
			wo.setSignalStack(new SignalStack());
		}

		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	public static class Wo extends ActionProcessingSignalWo {
		private static final long serialVersionUID = -3206075665001702872L;
	}

}