package com.x.processplatform.service.processing.processor.embed;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractEmbedProcessor extends AbstractProcessor {

	protected AbstractEmbedProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Embed embed = (Embed) aeiObjects.getActivity();
		return arriving(aeiObjects, embed);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		Embed embed = (Embed) aeiObjects.getActivity();
		return executing(aeiObjects, embed);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Embed embed = (Embed) aeiObjects.getActivity();
		return inquiring(aeiObjects, embed).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Embed embed) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Embed embed) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Embed embed) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Embed embed = (Embed) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, embed);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Embed embed = (Embed) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, embed, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Embed embed = (Embed) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, embed);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Embed embed) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Embed embed, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Embed embed) throws Exception;
}
