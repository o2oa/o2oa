package com.x.processplatform.service.processing.processor.merge;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractMergeProcessor extends AbstractProcessor {

	protected AbstractMergeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		return arriving(aeiObjects, merge);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		return executing(aeiObjects, merge);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		return inquiring(aeiObjects, merge).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Merge merge) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Merge merge) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Merge merge) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, merge);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, merge, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Merge merge = (Merge) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, merge);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Merge merge) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Merge merge, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Merge merge) throws Exception;
}
