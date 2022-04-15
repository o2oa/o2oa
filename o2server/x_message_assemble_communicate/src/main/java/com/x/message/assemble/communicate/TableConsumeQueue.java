package com.x.message.assemble.communicate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.config.Message.TableConsumer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

public class TableConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TableConsumeQueue.class);

	private static final Gson gson = XGsonBuilder.instance();

	protected void execute(Message message) throws Exception {
		if (null != message) {
			update(message);
		}
		List<String> ids = listOverStay();
		if (!ids.isEmpty()) {
			LOGGER.info("滞留 table 消息数量:{}.", ids.size());
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
			TableConsumer consumer = gson.fromJson(message.getProperties().getConsumerJsonElement(),
					TableConsumer.class);
			JsonElement jsonElement = gson.fromJson(message.getBody(), JsonElement.class);
			WrapBoolean resp = ThisApplication.context().applications()
					.postQuery(x_query_service_processing.class,
							Applications.joinQueryUri("table", consumer.getTable(), "insert"), jsonElement)
					.getData(WrapBoolean.class);
			if (BooleanUtils.isNotTrue(resp.getValue())) {
				throw new ExceptionTableReturn(message.getId(), message.getTitle(), consumer.getTable());
			}
			success(message.getId());
		} catch (Exception e) {
			failure(message.getId(), e);
			LOGGER.error(e);
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
			Predicate p = cb.equal(root.get(Message_.consumer), MessageConnector.CONSUME_RESTFUL);
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