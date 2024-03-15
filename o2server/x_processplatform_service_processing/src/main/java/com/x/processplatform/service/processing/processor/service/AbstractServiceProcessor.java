package com.x.processplatform.service.processing.processor.service;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractServiceProcessor extends AbstractProcessor {

	protected AbstractServiceProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Service service = (Service) aeiObjects.getActivity();
		return arriving(aeiObjects, service);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Service service = (Service) aeiObjects.getActivity();
		return executing(aeiObjects, service);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Service service = (Service) aeiObjects.getActivity();
	return  inquiring(aeiObjects, service).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Service service) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Service service) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Service service) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Service service = (Service) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, service);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Service service = (Service) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, service, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Service service = (Service) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, service);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Service service) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Service service, List<Work> works)
			throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Service service) throws Exception;
}
