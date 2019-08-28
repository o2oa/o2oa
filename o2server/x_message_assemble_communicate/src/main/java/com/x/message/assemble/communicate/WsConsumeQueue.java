package com.x.message.assemble.communicate;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.message.WsMessage;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class WsConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(WsConsumeQueue.class);

	private static final String TASK_FIRST = "first";

	protected void execute( Message message ) throws Exception {	
		WsMessage ws = new WsMessage();
		ws.setType(message.getType());
		ws.setPerson(message.getPerson());
		ws.setTitle(message.getTitle());
		JsonElement jsonElement = XGsonBuilder.instance().fromJson(message.getBody(), JsonElement.class);
		ws.setBody( jsonElement);
		Boolean result = false;
		/* 跳过第一条待办的提醒 */
		if (StringUtils.equalsIgnoreCase(ws.getType(), MessageConnector.TYPE_TASK_CREATE)
				&& BooleanUtils.isTrue(XGsonBuilder.extractBoolean( jsonElement, TASK_FIRST))) {
			result = true;
		} else {
			for (Application app : ThisApplication.context().applications().get(x_message_assemble_communicate.class)) {
				WrapBoolean wrapBoolean = ThisApplication.context().applications()
						.postQuery(app, MessageConnector.CONSUME_WS, ws).getData(WrapBoolean.class);
				result = result || wrapBoolean.getValue();
			}
		}
		if (result) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Message messageEntityObject = emc.find(message.getId(), Message.class);
				if (null != messageEntityObject) {
					emc.beginTransaction(Message.class);
					messageEntityObject.setConsumed(true);	
					emc.commit();
				}
			}
		}
	}
}
