package com.x.message.assemble.communicate;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.message.PmsMessage;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class PmsConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(PmsConsumeQueue.class);

	private static final String TASK_FIRST = "first";

	protected void execute(Message message) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("send pms message:{}.", message);
			Boolean result = false;
			JsonElement jsonElement = XGsonBuilder.instance().fromJson(message.getBody(), JsonElement.class);
			/* 跳过第一条待办的提醒 */
			if (StringUtils.equalsIgnoreCase(message.getType(), MessageConnector.TYPE_TASK_CREATE)
					&& BooleanUtils.isTrue(XGsonBuilder.extractBoolean(jsonElement, TASK_FIRST))) {
				logger.debug("跳过一条pms待办提醒:{}.", message);
				result = true;
			} else {
				Business business = new Business(emc);
				Person person = business.organization().person().getObject(message.getPerson());
				if ((null != person) && StringUtils.isNotEmpty(person.getMobile())) {
					PmsMessage pms = new PmsMessage();
					pms.setAccount(person.getMobile());
					pms.setTitle(message.getTitle());
					pms.setText(message.getTitle());
					String url = Config.x_program_centerUrlRoot() + MessageConnector.CONSUME_PMS;
					WrapBoolean wrapBoolean = CipherConnectionAction.post(false, url, pms).getData(WrapBoolean.class);
					result = wrapBoolean.getValue();
				}
			}
			if (BooleanUtils.isTrue(result)) {
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
