package com.x.message.assemble.communicate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.assemble.communicate.mq.ActiveMQ;
import com.x.message.assemble.communicate.mq.KafkaMQ;
import com.x.message.assemble.communicate.mq.MQInterface;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;


public class MQConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(MQConsumeQueue.class);

	protected void execute(Message message) throws Exception {
		logger.info("MQConsumeQueue message.getTitle:"+ message.getTitle());
		if (Config.mq().getEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				MQInterface MQClient;
				EntityManager em = business.entityManagerContainer().get(Message.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Message> cq = cb.createQuery(Message.class);
				Root<Message> root = cq.from(Message.class);

				Order order = cb.desc(root.get(Message_.createTime));
				Predicate p = cb.notEqual(root.get(Message_.consumed), true);
				
				p = cb.and(p, cb.equal(root.get(Message_.consumer), MessageConnector.CONSUME_MQ));
				logger.info(p.toString());
				List<Message> messages = em.createQuery(cq.select(root).where(p).orderBy(order)).setMaxResults(50).getResultList();
				if(messages.size()>0) {
					   if(Config.mq().getMq().equalsIgnoreCase("kafka")) {
						       MQClient = KafkaMQ.getInstance();
					      }else {
							   MQClient = ActiveMQ.getInstance();
						 }
					    if(MQClient != null) {
					    	for(Message mes : messages) {
								 boolean res = MQClient.sendMessage(mes);
								 if (res == false) {
									  Gson gson = new Gson();
							          String msg =  gson.toJson(mes);
									  ExceptionMQMessage e = new ExceptionMQMessage(0, msg);
									  logger.error(e);
								 } else {
									Message messageEntityObject = emc.find(mes.getId(), Message.class);
									if (null != messageEntityObject) {
										emc.beginTransaction(Message.class);
										messageEntityObject.setConsumed(true);
										emc.commit();
									}
								}
							  }
					    }
					 }
			   }
		}
	}
}
