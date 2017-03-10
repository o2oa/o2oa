package com.x.processplatform.service.processing.processor.agent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class AgentProcessor extends AbstractProcessor {

	private static Logger logger = LoggerFactory.getLogger(AgentProcessor.class);

	public AgentProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		Agent agent = (Agent) activity;
		if (StringUtils.isNotEmpty(agent.getScript()) || StringUtils.isNotEmpty(agent.getScriptText())) {
			logger.debug("work title:{}, id:{}, execute agent script id:{}, scriptText:{}.", work.getTitle(),
					work.getId(), agent.getScript(), agent.getScriptText());
			ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, agent);
			scriptHelper.eval(work.getApplication(), agent.getScript(), agent.getScriptText());
		}
		results.add(work);
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		Route o = routes.get(0);
		results.add(o);
		logger.debug("work title:{}, id:{}, inquire to route:{}.", work.getTitle(), work.getId(), o);
		return results;
	}
}