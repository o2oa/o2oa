package com.x.processplatform.service.processing.processor.cancel;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractCancelProcessor extends AbstractProcessor {

	protected AbstractCancelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Cancel cancel = (Cancel) aeiObjects.getActivity();
		return arriving(aeiObjects, cancel);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Cancel cancel = (Cancel) aeiObjects.getActivity();
		return executing(aeiObjects, cancel);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Cancel cancel = (Cancel) aeiObjects.getActivity();
		return inquiring(aeiObjects, cancel).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Cancel cancel) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Cancel cancel) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Cancel cancel) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Cancel cancel = (Cancel) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, cancel);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Cancel cancel = (Cancel) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, cancel, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Cancel cancel = (Cancel) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, cancel);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Cancel cancel, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception;
}
