package com.x.message.assemble.communicate.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.Message;


public class TriggerMq extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Clean.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			
		    if (Config.mq().getEnable()) {
				 Message message = new Message();
				 message.setBody("");
				 message.setType("TriggerMq");
				 message.setPerson("");
				 message.setTitle("TriggerMq");
				 message.setConsumer(MessageConnector.CONSUME_MQ);
				 message.setConsumed(false);
				 message.setInstant("");
				 ThisApplication.mqConsumeQueue.send(message);
			}
			
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}