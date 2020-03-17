package com.x.processplatform.service.processing.processor.message;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class MessageProcessor extends AbstractMessageProcessor {

	private static Logger logger = LoggerFactory.getLogger(MessageProcessor.class);

	public MessageProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Message message) throws Exception {
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Message message) throws Exception {
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Message message) throws Exception {
		MessageFactory.activity_message(aeiObjects.getWork(), null);
		List<Work> results = new ArrayList<>();
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Message message) throws Exception {
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Message message) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Message message) throws Exception {
	}
}