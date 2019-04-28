package com.x.message.assemble.communicate;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.message.PmsMessage;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class PmsConsumeQueue extends AbstractQueue<Message> {

	protected void execute(Message message) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Person person = business.organization().person().getObject(message.getPerson());
			if ((null != person) && StringUtils.isNotEmpty(person.getMobile())) {
				PmsMessage pms = new PmsMessage();
				pms.setAccount(person.getMobile());
				pms.setTitle(message.getTitle());
				pms.setText(message.getTitle());
				String url = Config.x_program_centerUrlRoot() + MessageConnector.CONSUME_PMS;
				CipherConnectionAction.post(false, url, pms);
			}
		}
	}
}
