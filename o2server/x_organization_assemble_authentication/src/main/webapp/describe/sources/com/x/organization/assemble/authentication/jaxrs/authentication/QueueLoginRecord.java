package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Date;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrapin.WrapInLoginRecord;
import com.x.organization.core.entity.Person;

public class QueueLoginRecord extends AbstractQueue<WrapInLoginRecord> {

	private static Logger logger = LoggerFactory.getLogger(QueueLoginRecord.class);

	protected void execute(WrapInLoginRecord wrapIn) throws Exception {
		String name = wrapIn.getName();
		String address = wrapIn.getAddress();
		String client = wrapIn.getClient();
		Date date = wrapIn.getDate();
		logger.debug("record login:{}, {}, {}, {}.", name, date, address, client);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String id = business.person().getWithCredential(name);
			Person person = emc.find(id, Person.class);
			if (null != person) {
				emc.beginTransaction(Person.class);
				person.setLastLoginTime(date);
				person.setLastLoginAddress(address);
				person.setLastLoginClient(client);
				emc.commit();
			}
		}
	}
}
