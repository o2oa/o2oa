package com.x.processplatform.service.processing.processor.split;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractSplitProcessor extends AbstractProcessor {

	protected AbstractSplitProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Split split = (Split) aeiObjects.getActivity();
		return arriving(aeiObjects, split);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Split split = (Split) aeiObjects.getActivity();
		return executing(aeiObjects, split);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Split split = (Split) aeiObjects.getActivity();
		return inquiring(aeiObjects, split).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Split split) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Split split) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Split split) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Split split = (Split) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, split);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Split split = (Split) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, split, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Split split = (Split) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, split);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Split split) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Split split, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Split split) throws Exception;
}
