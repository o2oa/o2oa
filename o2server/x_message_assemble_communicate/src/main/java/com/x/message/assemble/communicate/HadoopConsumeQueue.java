package com.x.message.assemble.communicate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.Message.HadoopConsumer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

public class HadoopConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HadoopConsumeQueue.class);

	private static final String ATTRIBUTE_FS_DEFAULTFS = "fs.defaultFS";
	private static final String SYSTEM_PROPERTY_HADOOP_USER_NAME = "HADOOP_USER_NAME";
	private static final Gson gson = XGsonBuilder.instance();

	protected void execute(Message message) throws Exception {
		LOGGER.debug("execute message:{}.", message::toString);
		if (null != message) {
			update(message);
		}
		List<String> ids = listOverStay();
		if (!ids.isEmpty()) {
			LOGGER.info("滞留 hadoop 消息数量:{}.", ids.size());
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
			HadoopConsumer consumer = gson.fromJson(message.getProperties().getConsumerJsonElement(),
					HadoopConsumer.class);
			try (FileSystem fileSystem = FileSystem.get(configuration(consumer));
					InputStream inputStream = new ByteArrayInputStream(
							message.getBody().getBytes(StandardCharsets.UTF_8))) {
				org.apache.hadoop.fs.Path path = path(message, consumer);
				if (fileSystem.exists(path)) {
					fileSystem.delete(path, false);
				}
				try (FSDataOutputStream outputStream = fileSystem.create(path)) {
					inputStream.transferTo(outputStream);
				}
			}
			success(message.getId());
		} catch (Exception e) {
			failure(message.getId(), e);
			LOGGER.error(e);
		}
	}

	private org.apache.hadoop.fs.Path path(Message message, HadoopConsumer consumer) {
		org.apache.hadoop.fs.Path path;
		if (StringUtils.isEmpty(consumer.getPath())) {
			path = new org.apache.hadoop.fs.Path(Path.SEPARATOR);
		} else if (StringUtils.startsWith(consumer.getPath(), Path.SEPARATOR)) {
			path = new org.apache.hadoop.fs.Path(consumer.getPath());
		} else {
			path = new org.apache.hadoop.fs.Path(Path.SEPARATOR + consumer.getPath());
		}
		if (StringUtils.isNotEmpty(message.getPerson())) {
			path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path(message.getPerson()));
		} else {
			path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path("default"));
		}
		path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path(message.getId()));
		return path;
	}

	private org.apache.hadoop.conf.Configuration configuration(HadoopConsumer consumer) {
		if (StringUtils.isNotEmpty(consumer.getUsername())) {
			System.setProperty(SYSTEM_PROPERTY_HADOOP_USER_NAME, consumer.getUsername());
		}
		org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
		configuration.set(ATTRIBUTE_FS_DEFAULTFS, consumer.getFsDefaultFS());
		return configuration;
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
			Predicate p = cb.equal(root.get(Message_.consumer), MessageConnector.CONSUME_HADOOP);
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