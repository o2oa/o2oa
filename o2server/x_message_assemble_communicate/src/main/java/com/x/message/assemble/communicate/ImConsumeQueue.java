package com.x.message.assemble.communicate;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.message.ImMessage;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class ImConsumeQueue extends AbstractQueue<Message> {

	protected void execute(Message message) throws Exception {
		ImMessage im = new ImMessage();
		im.setType(message.getType());
		im.setPerson(message.getPerson());
		im.setTitle(message.getTitle());
		im.setBody(XGsonBuilder.instance().fromJson(message.getBody(), JsonElement.class));
		Boolean result = false;
		for (Application app : ThisApplication.context().applications().get(x_message_assemble_communicate.class)) {
			WrapBoolean wrapBoolean = ThisApplication.context().applications()
					.postQuery(app, MessageConnector.CONSUME_IM, im).getData(WrapBoolean.class);
			result = result || wrapBoolean.getValue();
		}
		if (result) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Message messageEntityObject = emc.find(message.getId(), Message.class);
				if (null != messageEntityObject) {
					emc.beginTransaction(Message.class);
					emc.remove(messageEntityObject);
					emc.commit();
				}
			}
		}
	}
}
