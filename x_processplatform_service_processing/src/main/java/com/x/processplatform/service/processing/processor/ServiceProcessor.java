package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.temporary.ServiceValue;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class ServiceProcessor extends AbstractProcessor {

	public ServiceProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		/* 清空上一次调用值 */
		work.setServiceValue("");
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		if (StringUtils.isNotEmpty(work.getServiceValue())) {
			boolean passThrough = false;
			Service service = (Service) activity;
			if (StringUtils.isNotEmpty(service.getScript()) || StringUtils.isNotEmpty(service.getScriptText())) {
				ServiceValue serviceValue = this.entityManagerContainer().find(work.getServiceValue(),
						ServiceValue.class);
				if (null != serviceValue) {
					ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
							service, new BindingPair("serviceValue", service));
					passThrough = scriptHelper.evalAsBoolean(work.getApplication(), service.getScript(),
							service.getScriptText());
				}
			} else {
				passThrough = true;
			}
			if (passThrough) {
				results.add(work);
			}
		}
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(routes.get(0));
		return results;
	}
}
