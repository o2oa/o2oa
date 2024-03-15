package com.x.processplatform.service.processing.processor.publish;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractPublishProcessor extends AbstractProcessor {

	protected AbstractPublishProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Publish publish = (Publish) aeiObjects.getActivity();
		return arriving(aeiObjects, publish);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Publish publish = (Publish) aeiObjects.getActivity();
		return executing(aeiObjects, publish);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Publish publish = (Publish) aeiObjects.getActivity();
		return inquiring(aeiObjects, publish).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Publish publish) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Publish publish) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Publish publish) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Publish publish = (Publish) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, publish);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Publish publish = (Publish) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, publish, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Publish publish = (Publish) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, publish);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Publish publish) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Publish publish, List<Work> works)
			throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Publish publish) throws Exception;
}
