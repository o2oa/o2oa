package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.message.Event;
import com.x.processplatform.core.entity.message.Event_;

public class UpdateTableQueue extends AbstractQueue<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTableQueue.class);

	private Gson gson = XGsonBuilder.instance();

	protected void execute(String id) throws Exception {
		update(id);
		List<String> ids = this.checkOverstay();
		if (!ids.isEmpty()) {
			for (String s : ids) {
				update(s);
			}
			clean();
		}
	}

	private boolean update(String id) throws Exception {
		Event event = exist(id);
		if (null != event) {
			if (push(event)) {
				success(id);
			} else {
				failure(id);
			}
		}
		return false;
	}

	private boolean push(Event event) {
		String tableName = event.getTarget();
		Data data = null;
		WorkCompleted workCompleted = null;
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				workCompleted = emc.find(event.getWorkCompleted(), WorkCompleted.class);
				if (null != workCompleted) {
					data = new WorkDataHelper(emc, workCompleted).get();
				}
			}
			if (null != data && null != workCompleted) {
				JsonElement jsonElement = XGsonBuilder.merge(gson.toJsonTree(data), gson.toJsonTree(data));
				WrapBoolean resp = ThisApplication.context().applications()
						.postQuery(x_query_service_processing.class,
								Applications.joinQueryUri("table", tableName, "update", event.getTarget()), jsonElement)
						.getData(WrapBoolean.class);
				return resp.getValue();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	private Event exist(String id) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Event.class);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}

	private void success(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Event event = emc.find(id, Event.class);
			if (null != event) {
				emc.beginTransaction(Event.class);
				emc.remove(event);
				emc.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void failure(String id) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Event event = emc.find(id, Event.class);
			if (null != event) {
				emc.beginTransaction(Event.class);
				Integer failure = event.getFailure();
				failure = (failure == null) ? 1 : failure + 1;
				event.setFailure(failure);
				emc.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private List<String> checkOverstay() throws Exception {
		List<String> list = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Event.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Event> root = cq.from(Event.class);
			Predicate p = cb.equal(root.get(Event_.type), Event.EVENTTYPE_UPDATETABLE);
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(Event_.updateTime), DateUtils.addMinutes(new Date(), -20)));
			list.addAll(em.createQuery(cq.select(root.get(Event_.id)).where(p)).setMaxResults(100).getResultList());
		}
		if (!list.isEmpty()) {
			LOGGER.info("found {} overstay {} message.", () -> list.size(), () -> Event.EVENTTYPE_UPDATETABLE);
		}
		return list;
	}
}
