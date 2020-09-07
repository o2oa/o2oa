package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.log.SignalStack;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingSignalWo;
import com.x.processplatform.service.processing.ThisApplication;

class ActionProcessingSignal extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String series, String activityToken)
			throws Exception {

		Wo wo = new Wo();

		Thread.sleep(Config.processPlatform().getProcessingSignalThreshold());

		Optional<SignalStack> optional = ThisApplication.getProcessingToProcessingSignalStack().find(id, series);

		int loop = 0;

		while ((!optional.isPresent()) && (loop++ < 20)) {
			Thread.sleep(200);
			optional = ThisApplication.getProcessingToProcessingSignalStack().find(id, series);
		}

		if (optional.isPresent()) {
			optional.get().forEach(o -> {
				if (((null != o.getManualExecute()) || (null != o.getSplitExecute()))
						&& (!StringUtils.equals(activityToken, o.getActivityToken()))) {
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