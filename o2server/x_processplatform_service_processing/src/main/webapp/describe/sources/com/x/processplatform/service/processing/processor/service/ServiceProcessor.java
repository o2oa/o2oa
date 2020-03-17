package com.x.processplatform.service.processing.processor.service;

import java.util.ArrayList;
import java.util.List;

import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.temporary.ServiceValue;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ServiceProcessor extends AbstractServiceProcessor {

	private static Logger logger = LoggerFactory.getLogger(ServiceProcessor.class);

	public ServiceProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Service service) throws Exception {
		/** 清空上一次调用值 */
		aeiObjects.getWork().setServiceValue("");
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Service service) throws Exception {
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Service service) throws Exception {
		List<Work> results = new ArrayList<>();
		if (StringUtils.isNotEmpty(aeiObjects.getWork().getServiceValue())) {
			boolean passThrough = false;
			if (StringUtils.isNotEmpty(service.getScript()) || StringUtils.isNotEmpty(service.getScriptText())) {
				ServiceValue serviceValue = this.entityManagerContainer().find(aeiObjects.getWork().getServiceValue(),
						ServiceValue.class);
				if (null != serviceValue) {

					ScriptContext scriptContext = aeiObjects.scriptContext();
					scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptFactory.BINDING_NAME_SERVICEVALUE,
							serviceValue);
					CompiledScript cs = aeiObjects.business().element().getCompiledScript(
							aeiObjects.getWork().getApplication(), aeiObjects.getActivity(), Business.EVENT_SERVICE);
					passThrough = ScriptFactory.asBoolean(cs.eval(scriptContext));
//					ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
//							new BindingPair("serviceValue", serviceValue));
//					passThrough = scriptHelper.evalAsBoolean(aeiObjects.getWork().getApplication(),
//							service.getScript(), service.getScriptText());
				}
			} else {
				passThrough = true;
			}
			if (passThrough) {
				results.add(aeiObjects.getWork());
			}
		}
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Service service) throws Exception {
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Service service) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Service service) throws Exception {
	}
}
