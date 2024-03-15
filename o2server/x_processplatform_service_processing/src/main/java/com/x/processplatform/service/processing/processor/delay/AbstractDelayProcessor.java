package com.x.processplatform.service.processing.processor.delay;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractDelayProcessor extends AbstractProcessor {

	protected AbstractDelayProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Delay delay = (Delay) aeiObjects.getActivity();
		return arriving(aeiObjects, delay);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Delay delay = (Delay) aeiObjects.getActivity();
		return executing(aeiObjects, delay);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Delay delay = (Delay) aeiObjects.getActivity();
		return inquiring(aeiObjects, delay).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Delay delay) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Delay delay) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Delay delay) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Delay delay = (Delay) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, delay);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Delay delay = (Delay) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, delay, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Delay delay = (Delay) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, delay);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Delay delay) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Delay delay, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Delay delay) throws Exception;
}
