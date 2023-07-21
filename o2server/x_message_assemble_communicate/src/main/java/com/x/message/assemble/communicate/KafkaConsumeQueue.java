package com.x.message.assemble.communicate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.security.scram.ScramLoginModule;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.Message.KafkaConsumer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

public class KafkaConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumeQueue.class);

	private static final Gson gson = XGsonBuilder.instance();

	protected void execute(Message message) throws Exception {
		if (null != message) {
			update(message);
		}
		List<String> ids = listOverStay();
		if (!ids.isEmpty()) {
			LOGGER.info("滞留 kafka 消息数量:{}.", ids.size());
			for (String id : ids) {
				Optional<Message> optional = find(id);
				if (optional.isPresent()) {
					message = optional.get();
					update(message);
				}
			}
		}
	}

	private Optional<Message> find(String id) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return Optional.of(emc.find(id, Message.class));
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	private void update(Message message) {
		try {
			KafkaConsumer consumer = gson.fromJson(message.getProperties().getConsumerJsonElement(),
					KafkaConsumer.class);
			producer(message, consumer);
			success(message.getId());
		} catch (InterruptedException ie) {
			LOGGER.error(ie);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			failure(message.getId(), e);
			LOGGER.error(e);
		}
	}

	private void producer(Message message, KafkaConsumer consumer) throws InterruptedException, ExecutionException {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", consumer.getBootstrapServers());
		properties.put("acks", "all");
		properties.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class.getName());
		properties.put("value.serializer", org.apache.kafka.common.serialization.StringSerializer.class.getName());
		if (StringUtils.isNotBlank(consumer.getUsername()) && StringUtils.isNotBlank(consumer.getSaslMechanism())) {
			properties.put("security.protocol", consumer.getSecurityProtocol());
			properties.put("sasl.mechanism", consumer.getSaslMechanism());
			if (StringUtils.equalsIgnoreCase(consumer.getSaslMechanism(), "PLAIN")) {
				properties.put("sasl.jaas.config", PlainLoginModule.class.getName() + " required username=\""
						+ consumer.getUsername() + "\" password=\"" + consumer.getPassword() + "\";");
			} else if (StringUtils.equalsIgnoreCase(consumer.getSaslMechanism(), "SCRAM-SHA-256")) {
				properties.put("sasl.jaas.config", ScramLoginModule.class.getName() + " required username=\""
						+ consumer.getUsername() + "\" password=\"" + consumer.getPassword() + "\";");
			}
		}
		try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
			String topic = consumer.getTopic();
			String msg = gson.toJson(message);
			producer.send(new ProducerRecord<>(topic, msg)).get();
		}
	}

	private void success(String id) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Message message = emc.find(id, Message.class);
			if (null != message) {
				emc.beginTransaction(Message.class);
				message.setConsumed(true);
				emc.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void failure(String id, Exception exception) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Message message = emc.find(id, Message.class);
			if (null != message) {
				emc.beginTransaction(Message.class);
				Integer failure = message.getProperties().getFailure();
				failure = (null == failure) ? 1 : failure + 1;
				message.getProperties().setFailure(failure);
				message.getProperties().setError(exception.getMessage());
				emc.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private List<String> listOverStay() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Message.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Message> root = cq.from(Message.class);
			Predicate p = cb.equal(root.get(Message_.consumer), MessageConnector.CONSUME_KAFKA);
			p = cb.and(p, cb.notEqual(root.get(Message_.consumed), true));
			p = cb.and(p, cb.lessThan(root.get(JpaObject_.updateTime), DateUtils.addMinutes(new Date(), -20)));
			cq.select(root.get(Message_.id)).where(p);
			return em.createQuery(cq).setMaxResults(20).getResultList();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return new ArrayList<>();
	}
}
