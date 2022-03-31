package com.x.message.assemble.communicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class UpdateQueryTableConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateQueryTableConsumeQueue.class);

	protected void execute(Message message) throws Exception {
		if (exist(message.getId())) {

		}
	}

	private boolean update(Message message) {
		String tableName = message.getTarget();
		resp = ThisApplication.context().applications().postQuery(x_query_service_processing.class,
				Applications.joinQueryUri("table", "update", tableName), task.getJob());
		return true;
	}

	private boolean exist(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Message m = emc.find(id, Message.class);
			return null != m;
		}
	}

	private void consumed(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Message m = emc.find(id, Message.class);
			if (null != m) {
				emc.beginTransaction(Message.class);
				m.setConsumed(true);
				emc.commit();
			}
		}
	}
}
