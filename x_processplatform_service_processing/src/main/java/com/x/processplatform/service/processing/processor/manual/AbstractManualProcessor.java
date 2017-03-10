package com.x.processplatform.service.processing.processor.manual;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public abstract class AbstractManualProcessor extends AbstractProcessor {

	protected AbstractManualProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		Manual manual = (Manual) activity;
		return arriving(configurator, attributes, work, data, manual);
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		Manual manual = (Manual) activity;
		return executing(configurator, attributes, work, data, manual);
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		Manual manual = (Manual) activity;
		return inquiring(configurator, attributes, work, data, manual, routes);
	}

	protected abstract Work arriving(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Manual manual) throws Exception;

	protected abstract List<Work> executing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Manual manual) throws Exception;

	protected abstract List<Route> inquiring(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Manual manual, List<Route> routes) throws Exception;
}
