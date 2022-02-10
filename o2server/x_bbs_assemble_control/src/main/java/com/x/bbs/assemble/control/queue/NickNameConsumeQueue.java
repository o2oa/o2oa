package com.x.bbs.assemble.control.queue;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

/**
 * @author sword
 */
public class NickNameConsumeQueue extends AbstractQueue<String> {

	private static Logger logger = LoggerFactory.getLogger(NickNameConsumeQueue.class);

	@Override
	protected void execute(String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("change nick name:{}.", person);

		}
	}
}
