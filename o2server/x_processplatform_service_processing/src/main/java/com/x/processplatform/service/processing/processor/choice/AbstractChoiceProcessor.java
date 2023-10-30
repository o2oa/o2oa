package com.x.processplatform.service.processing.processor.choice;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractChoiceProcessor extends AbstractProcessor {

	protected AbstractChoiceProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Choice choice = (Choice) aeiObjects.getActivity();
		return arriving(aeiObjects, choice);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Choice choice = (Choice) aeiObjects.getActivity();
		return executing(aeiObjects, choice);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Choice choice = (Choice) aeiObjects.getActivity();
		return inquiring(aeiObjects, choice).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Choice choice) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Choice choice) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Choice choice) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Choice choice = (Choice) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, choice);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Choice choice = (Choice) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, choice, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Choice choice = (Choice) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, choice);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Choice choice) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Choice choice, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Choice choice) throws Exception;
}
