package com.x.message.assemble.communicate;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_calendar_assemble_control;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class CalendarConsumeQueue extends AbstractQueue<Message> {

	protected void execute(Message message) throws Exception {
		Application app = ThisApplication.context().applications()
				.randomWithWeight(x_calendar_assemble_control.class.getName());
		if (null != app) {
			WrapBoolean wrapBoolean = ThisApplication.context().applications().postQuery(false, app, "message", message)
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
