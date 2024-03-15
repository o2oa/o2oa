package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.message.Event;
import com.x.processplatform.core.entity.message.Event_;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class UpdateTableQueue extends AbstractQueue<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTableQueue.class);

	private Gson gson = XGsonBuilder.instance();

	private static final int RETRYMINUTES = 20;
	private static final int THRESHOLDMINUTES = 60 * 24 * 3;

	@Override
	protected void execute(String id) throws Exception {
		if (StringUtils.isNotEmpty(id)) {
			update(id);
		}
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
		if ((null != event) && StringUtils.equals(event.getType(), Event.EVENTTYPE_UPDATETABLE)) {
			if (push(event)) {
				success(id);
			} else {
				failure(id);
				LOGGER.warn("更新到自建表失败:{}.", () -> id);
			}
		}
		return false;
	}

	private boolean push(Event event) {
		LOGGER.debug("更新到自建表:{}, bundle:{}.", event::getTarget, event::getJob);
		JsonElement jsonElement = null;
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
						event.getJob());
				if (null != workCompleted) {
					Data data = new WorkDataHelper(emc, workCompleted).get();
					Process process = emc.find(workCompleted.getProcess(), Process.class);
					if (hasTableAssignDataScript(process)) {
						AssignPublish assignPublish = new AssignPublish();
						GraalvmScriptingFactory.Bindings bindings = this.bindings(data, business,
								new Work(workCompleted));
						this.evalTableBodyFromScript(bindings, business, process, assignPublish);
						jsonElement = assignPublish.getData();
					}
					if (jsonElement == null) {
						jsonElement = XGsonBuilder.merge(gson.toJsonTree(workCompleted), gson.toJsonTree(data));
					}
				}
			}
			if (null != jsonElement) {
				WrapBoolean resp = ThisApplication.context().applications().postQuery(x_query_service_processing.class,
						Applications.joinQueryUri("table", event.getTarget(), "update", event.getJob()), jsonElement)
						.getData(WrapBoolean.class);
				return resp.getValue();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	private void evalTableBodyFromScript(GraalvmScriptingFactory.Bindings bindings, Business business, Process process,
			final AssignPublish assignPublish) throws Exception {
		WrapScriptObject assignBody = new WrapScriptObject();
		Source source = business.element().getCompiledScript(process.getApplication(),
				process.getTargetAssignDataScript(), process.getTargetAssignDataScriptText());
		bindings.putMember(GraalvmScriptingFactory.BINDING_NAME_JAXRSBODY, assignBody);
		GraalvmScriptingFactory.eval(source, bindings, assignPublish::setData);
	}

	private GraalvmScriptingFactory.Bindings bindings(Data data, Business business, Work work) throws Exception {
		AeiObjects.Resources resources = new AeiObjects.Resources();
		resources.setApplications(ThisApplication.context().applications());
		resources.setOrganization(business.organization());
		resources.setWebservicesClient(new WebservicesClient());
		resources.setContext(ThisApplication.context());
		return new GraalvmScriptingFactory.Bindings()
				.putMember(GraalvmScriptingFactory.BINDING_NAME_RESOURCES, resources)
				.putMember(GraalvmScriptingFactory.BINDING_NAME_WORKCONTEXT, new WorkContext(work, business))
				.putMember(GraalvmScriptingFactory.BINDING_NAME_DATA, data);
	}

	private boolean hasTableAssignDataScript(Process process) {
		if (process == null) {
			return false;
		}
		return StringUtils.isNotEmpty(process.getTargetAssignDataScript())
				|| StringUtils.isNotEmpty(process.getTargetAssignDataScriptText());
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
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(JpaObject_.updateTime),
					DateUtils.addMinutes(new Date(), -RETRYMINUTES)));
			list.addAll(em.createQuery(cq.select(root.get(Event_.id)).where(p)).setMaxResults(100).getResultList());
		}
		if (!list.isEmpty()) {
			LOGGER.info("查找到 {} 条处理失败的同步到自建表事件.", list::size);
		}
		return list;
	}

	private void clean() throws Exception {
		List<String> list = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Event.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Event> root = cq.from(Event.class);
			Predicate p = cb.equal(root.get(Event_.type), Event.EVENTTYPE_UPDATETABLE);
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(JpaObject_.createTime),
					DateUtils.addMinutes(new Date(), -THRESHOLDMINUTES)));
			list.addAll(em.createQuery(cq.select(root.get(Event_.id)).where(p)).setMaxResults(100).getResultList());
			if (!list.isEmpty()) {
				emc.beginTransaction(Event.class);
				for (String id : list) {
					Event event = emc.find(id, Event.class);
					if (null != event) {
						emc.remove(event);
					}
				}
				emc.commit();
			}
		}
		if (!list.isEmpty()) {
			LOGGER.info("删除 {} 条超期的同步到自建表事件.", list::size);
		}
	}

	public class AssignPublish {

		private JsonElement data;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}
	}
}
