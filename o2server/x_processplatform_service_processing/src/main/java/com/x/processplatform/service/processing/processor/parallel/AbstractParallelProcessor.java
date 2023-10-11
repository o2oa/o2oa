package com.x.processplatform.service.processing.processor.parallel;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractParallelProcessor extends AbstractProcessor {

	protected AbstractParallelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Parallel parallel = (Parallel) aeiObjects.getActivity();
		return arriving(aeiObjects, parallel);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Parallel parallel = (Parallel) aeiObjects.getActivity();
		return executing(aeiObjects, parallel);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Parallel parallel = (Parallel) aeiObjects.getActivity();
		return inquiring(aeiObjects, parallel).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Parallel parallel) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Parallel parallel) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Parallel parallel) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Parallel parallel = (Parallel) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, parallel);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Parallel parallel = (Parallel) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, parallel, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Parallel parallel = (Parallel) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, parallel);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Parallel parallel) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Parallel parallel, List<Work> works)
			throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Parallel parallel) throws Exception;
}
