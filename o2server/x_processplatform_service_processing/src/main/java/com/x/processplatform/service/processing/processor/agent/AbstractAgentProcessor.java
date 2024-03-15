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
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractAgentProcessor extends AbstractProcessor {

	protected AbstractAgentProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Agent agent = (Agent) aeiObjects.getActivity();
		/** 更新data中的work和attachment */
		aeiObjects.getData().setWork(aeiObjects.getWork());
		aeiObjects.getData().setAttachmentList(aeiObjects.getAttachments());
		return arriving(aeiObjects, agent);
	}

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Agent agent = (Agent) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, agent);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Agent agent = (Agent) aeiObjects.getActivity();
		List<Work> os = new ArrayList<>();
		try {
			os = executing(aeiObjects, agent);
			return os;
		} catch (Exception e) {
			if (this.hasAgentInterruptScript(agent)) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						aeiObjects.getActivity(), Business.EVENT_AGENTINTERRUPT);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			throw e;
		}
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Agent agent = (Agent) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, agent, works);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Agent agent = (Agent) aeiObjects.getActivity();
		return inquiring(aeiObjects, agent).orElse(null);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Agent agent = (Agent) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, agent);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Agent agent) throws Exception;

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Agent agent) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Agent agent) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Agent agent, List<Work> works) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Agent agent) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Agent agent) throws Exception;

	private boolean hasAgentInterruptScript(Agent agent) throws Exception {
		return StringUtils.isNotEmpty(agent.getAgentInterruptScript())
				|| StringUtils.isNotEmpty(agent.getAgentInterruptScriptText());
	}
}