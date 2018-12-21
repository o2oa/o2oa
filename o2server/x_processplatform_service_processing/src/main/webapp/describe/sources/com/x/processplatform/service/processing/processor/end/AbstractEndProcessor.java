package com.x.processplatform.service.processing.processor.end;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.AeiObjects;

public abstract class AbstractEndProcessor extends AbstractProcessor {

	protected AbstractEndProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		return arriving(aeiObjects, end);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		return executing(aeiObjects, end);
	}

	@Override
	protected List<Route> inquireProcessing(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		return inquiring(aeiObjects, end);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, End end) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, End end) throws Exception;

	protected abstract List<Route> inquiring(AeiObjects aeiObjects, End end) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, end);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, end);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, end);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, End end) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, End end) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, End end) throws Exception;
}
