package com.x.message.assemble.communicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.message.PmsInnerMessage;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.x_jpush_assemble_control;
import com.x.message.core.entity.Message;
import org.apache.commons.lang3.BooleanUtils;

public class PmsInnerConsumeQueue extends AbstractQueue<Message> {

	protected void execute(Message message) throws Exception {
		Application app = ThisApplication.context().applications()
				.randomWithWeight(x_jpush_assemble_control.class.getName());
		if (null != app) {
			PmsInnerMessage innerMessage = new PmsInnerMessage();
			innerMessage.setPerson(message.getPerson());
			innerMessage.setMessage(message.getTitle());
			WrapBoolean wrapBoolean = ThisApplication.context().applications().postQuery(false, app, "message/send", innerMessage)
					.getData(WrapBoolean.class);
			if (BooleanUtils.isTrue(wrapBoolean.getValue())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Message messageEntityObject = emc.find(message.getId(), Message.class);
					if (null != messageEntityObject) {
						emc.beginTransaction(Message.class);
						message.setConsumed(true);
						emc.commit();
					}
				}
			}
		}
	}
}
