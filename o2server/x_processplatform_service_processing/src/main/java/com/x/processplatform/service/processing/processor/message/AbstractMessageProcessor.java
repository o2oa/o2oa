package com.x.processplatform.service.processing.processor.message;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

public abstract class AbstractMessageProcessor extends AbstractProcessor {

	protected AbstractMessageProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Message message = (Message) aeiObjects.getActivity();
		return arriving(aeiObjects, message);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Message message = (Message) aeiObjects.getActivity();
		return executing(aeiObjects, message);
	}

	@Override
	protected List<Route> inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Message message = (Message) aeiObjects.getActivity();
		return inquiring(aeiObjects, message);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Message message) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Message message) throws Exception;

	protected abstract List<Route> inquiring(AeiObjects aeiObjects, Message message) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Message message = (Message) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, message);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects) throws Exception {
		Message message = (Message) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, message);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Message message = (Message) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, message);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Message message) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Message message) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Message message) throws Exception;
}
