package com.x.processplatform.service.processing.processor.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.script.CompiledScript;
import javax.script.ScriptContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

import org.apache.commons.lang3.StringUtils;

public class ServiceProcessor extends AbstractServiceProcessor {

	private static Logger logger = LoggerFactory.getLogger(ServiceProcessor.class);

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
		if (StringUtils.isNotEmpty(service.getScript()) || StringUtils.isNotEmpty(service.getScriptText())) {
			ScriptContext scriptContext = aeiObjects.scriptContext();
			scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptFactory.BINDING_NAME_SERVICEVALUE,
					gson.toJson(aeiObjects.getWork().getProperties().getServiceValue()));
			CompiledScript cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					aeiObjects.getActivity(), Business.EVENT_SERVICE);
			passThrough = ScriptFactory.asBoolean(cs.eval(scriptContext));
		} else {
			passThrough = true;
		}
		if (passThrough) {
			results.add(aeiObjects.getWork());
		}

		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Service service) throws Exception {
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
