package com.x.message.assemble.communicate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.MessageRestful;
import com.x.base.core.project.connection.HttpConnectionResponse;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

public class RestfulConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestfulConsumeQueue.class);

	private static final Pattern pattern = Pattern.compile("\\{\\$(.+?)\\}");

	private static final Gson gson = XGsonBuilder.instance();

	private static WebservicesClient client = new WebservicesClient();

	protected void execute(Message message) throws Exception {
		if (null != message && StringUtils.isNotEmpty(message.getItem())) {
			update(message);
		}
		List<String> ids = listOverStay();
		if (!ids.isEmpty()) {
			LOGGER.info("滞留 restful 消息数量:{}.", ids.size());
			for (String id : ids) {
				Optional<Message> optional = find(id);
				if (optional.isPresent()) {
					message = optional.get();
					if (StringUtils.isNotEmpty(message.getItem())) {
						update(message);
					}
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
			MessageRestful.Item item = Config.messageRestful().get(message.getItem());
			if (null != item) {
				String url = url(message, item);
				HttpConnectionResponse response = client.restful(item.getMethod(), url, null, message.getBody(), 5000,
						5000);
				if (null == response) {
					throw new ExceptionRestful(message.getTitle(), message.getPerson(), url);
				}
				success(message.getId());
			} else {
				throw new ExceptionMessageRestfulItem(message.getItem());
			}
		} catch (Exception e) {
			failure(message.getId(), e);
			LOGGER.error(e);
		}
	}

	private String url(Message message, MessageRestful.Item item) {
		String url = item.getUrl();
		JsonElement jsonElement = gson.toJsonTree(message.getBody());
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (null != jsonObject) {
				Matcher matcher = pattern.matcher(url);
				while (matcher.find()) {
					String key = matcher.group(1);
					if (jsonObject.has(key)) {
						String value = jsonObject.get(key).getAsString();
						if (null != value) {
							url = StringUtils.replace(url, matcher.group(), value);
						}
					}
				}
			}
		}
		return url;
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
