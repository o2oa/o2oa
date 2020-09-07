package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.core.entity.log.SignalStack;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingSignalWo;
import com.x.processplatform.service.processing.ThisApplication;

class ActionProcessingSignal extends BaseAction {

	private static final List<String> EXPORTSIGNALS = ListTools.toList(Signal.TYPE_MANUALEXECUTE,
			Signal.TYPE_SPLITEXECUTE);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String series) throws Exception {

		Thread.sleep(Config.processPlatform().getProcessingSignalThreshold());

		Optional<SignalStack> optional = ThisApplication.getProcessingToProcessingSignalStack().find(id, series);

		Wo wo = new Wo();
		optional.ifPresent(
				o -> o.stream().filter(s -> EXPORTSIGNALS.contains(s.getType())).forEach(wo.getSignalStack()::add));
		optional.ifPresent(wo::setSignalStack);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	public static class Wo extends ActionProcessingSignalWo {
		private static final long serialVersionUID = -3206075665001702872L;
	}

}