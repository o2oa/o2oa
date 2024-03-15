package com.x.processplatform.service.processing.processor.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class AgentProcessor extends AbstractAgentProcessor {

	public AgentProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Agent agent) {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.agentArrive(aeiObjects.getWork().getActivityToken(), agent));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Agent agent) throws Exception {
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Agent agent) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.agentExecute(aeiObjects.getWork().getActivityToken(), agent));
		List<Work> results = new ArrayList<>();
		if (StringUtils.isNotEmpty(agent.getScript()) || StringUtils.isNotEmpty(agent.getScriptText())) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					aeiObjects.getActivity(), Business.EVENT_AGENT);
			GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
		}
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Agent agent, List<Work> works) throws Exception {

	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Agent agent) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.agentInquire(aeiObjects.getWork().getActivityToken(), agent));
		return aeiObjects.getRoutes().stream().findFirst();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Agent agent) throws Exception {

	}
}