package com.x.processplatform.service.processing.processor.begin;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractBeginProcessor extends AbstractProcessor {

	protected AbstractBeginProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Begin begin = (Begin) aeiObjects.getActivity();
		/** 更新data中的work和attachment */
		aeiObjects.getData().setWork(aeiObjects.getWork());
		aeiObjects.getData().setAttachmentList(aeiObjects.getAttachments());
		return arriving(aeiObjects, begin);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Begin begin = (Begin) aeiObjects.getActivity();
		return executing(aeiObjects, begin);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Begin begin = (Begin) aeiObjects.getActivity();
		return inquiring(aeiObjects, begin).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Begin begin) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Begin begin) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Begin begin) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Begin begin = (Begin) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, begin);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Begin begin = (Begin) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, begin, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Begin begin = (Begin) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, begin);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Begin begin) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Begin begin, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Begin begin) throws Exception;
}
