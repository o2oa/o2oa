package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.log.SignalStack;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingSignalWo;
import com.x.processplatform.service.processing.ThisApplication;

class ActionProcessingSignal extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String series, String activityToken)
			throws Exception {

		Wo wo = null;

		int loop = 0;

		while ((null == wo) && (loop++ < 50)) {
			Thread.sleep(200);
			wo = dataReady(activityToken, ThisApplication.getProcessingToProcessingSignalStack().find(id, series));
		}

		if (null == wo) {
			wo = new Wo();
			wo.setSignalStack(new SignalStack());
		}

		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	private Wo dataReady(String activityToken, Optional<SignalStack> optional) {
		if (!optional.isPresent()) {
			return null;
		}
		Wo wo = new Wo();
		optional.get().forEach(o -> {
			if (((null != o.getManualExecute()) || (null != o.getSplitExecute()))
					&& (!StringUtils.equals(activityToken, o.getActivityToken()))) {
				wo.getSignalStack().add(o);
			}
		});
		if (wo.getSignalStack().isEmpty()) {
			return null;
		} else {
			return wo;
		}
	}

	public static class Wo extends ActionProcessingSignalWo {
		private static final long serialVersionUID = -3206075665001702872L;
	}

}