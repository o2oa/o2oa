package com.x.server.console.node;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EventQueueExecutor extends Thread {

	private static Logger logger = LoggerFactory.getLogger(EventQueueExecutor.class);

	private LinkedBlockingQueue<JsonElement> queue;

	public EventQueueExecutor(LinkedBlockingQueue<JsonElement> queue) {
		this.queue = queue;
		this.setName(EventQueueExecutor.class.getName());
	}

	@Override
	public void run() {
		while (true) {
			Event event = null;
			try {
				JsonElement jsonElement = queue.take();
				event = convert(jsonElement);
			} catch (Exception e) {
				logger.error(e);
			}
			if (null != event) {
				event.execute();
			}

		}
	}

	public Event convert(JsonElement jsonElement) {

		String type = Objects.toString(jsonElement.getAsJsonObject().get("type").getAsString(), "");

		switch (type) {

		case Event.TYPE_REFRESHAPPLICATIONS:
			return XGsonBuilder.instance().fromJson(jsonElement, RefreshApplicationsEvent.class);

		case Event.TYPE_REGISTAPPLICATIONS:
			return XGsonBuilder.instance().fromJson(jsonElement, RegistApplicationsEvent.class);

		case Event.TYPE_UPDATEAPPLICATIONS:
			return XGsonBuilder.instance().fromJson(jsonElement, UpdateApplicationsEvent.class);

		case Event.TYPE_VOTECENTER:
			return XGsonBuilder.instance().fromJson(jsonElement, VoteCenterEvent.class);

		default:
			return null;
		}

	}

}
