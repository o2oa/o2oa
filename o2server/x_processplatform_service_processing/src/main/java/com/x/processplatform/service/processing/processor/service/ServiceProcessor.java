package com.x.processplatform.service.processing.processor.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ServiceProcessor extends AbstractServiceProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProcessor.class);

	public ServiceProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Service service) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.serviceArrive(aeiObjects.getWork().getActivityToken(), service));
		// 清空上一次调用值
		aeiObjects.getWork().getProperties().setServiceValue(new LinkedHashMap<>());
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Service service) throws Exception {
		// Do nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Service service) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.parallelExecute(aeiObjects.getWork().getActivityToken(), service));
		List<Work> results = new ArrayList<>();
		boolean passThrough = false;
		// 判断是否已经在getServiceValue中有值了,否则会在到达后直接运行.
		if (!aeiObjects.getWork().getProperties().getServiceValue().isEmpty()) {
			LOGGER.debug("work:{}, serviceValue:{}.", () -> aeiObjects.getWork().getId(),
					() -> this.gson.toJson(aeiObjects.getWork().getProperties().getServiceValue()));
			if (StringUtils.isNotEmpty(service.getScript()) || StringUtils.isNotEmpty(service.getScriptText())) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptingFactory.BINDING_NAME_REQUESTTEXT,
						gson.toJson(aeiObjects.getWork().getProperties().getServiceValue()));
				CompiledScript cs = aeiObjects.business().element().getCompiledScript(
						aeiObjects.getWork().getApplication(), aeiObjects.getActivity(), Business.EVENT_SERVICE);
				passThrough = JsonScriptingExecutor.evalBoolean(cs, scriptContext, Boolean.TRUE);
			} else {
				passThrough = true;
			}
		}
		if (passThrough) {
			results.add(aeiObjects.getWork());
		}
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Service service, List<Work> works) throws Exception {
		// Do nothing
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Service service) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.parallelInquire(aeiObjects.getWork().getActivityToken(), service));
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Service service) throws Exception {
		// Do nothing
	}
}
