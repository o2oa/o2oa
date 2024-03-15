package com.x.processplatform.service.processing.processor.invoke;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractInvokeProcessor extends AbstractProcessor {

	protected AbstractInvokeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Invoke invoke = (Invoke) aeiObjects.getActivity();
		return arriving(aeiObjects, invoke);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Invoke invoke = (Invoke) aeiObjects.getActivity();
		return executing(aeiObjects, invoke);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Invoke invoke = (Invoke) aeiObjects.getActivity();
		return inquiring(aeiObjects, invoke).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Invoke invoke) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Invoke invoke) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Invoke invoke) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Invoke invoke = (Invoke) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, invoke);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Invoke invoke = (Invoke) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, invoke, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Invoke invoke = (Invoke) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, invoke);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Invoke invoke) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Invoke invoke, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Invoke invoke) throws Exception;
}
