package com.x.processplatform.assemble.surface.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.V2TriggerProcessingWo;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionPassExpiredWo;

public class PassExpired extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassExpired.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			String sequence = null;
			List<Task> targets = new ArrayList<>();
			Map<String, Route> manualToRoute = null;
			AtomicInteger count = new AtomicInteger(0);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				manualToRoute = this.linkPassExpiredManualToRoute(emc);
			}
			if (!manualToRoute.isEmpty()) {
				do {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						targets = this.list(emc, sequence, manualToRoute);
					}
					if (!targets.isEmpty()) {
						sequence = targets.get(targets.size() - 1).getSequence();
						for (Task task : targets) {
							LOGGER.print("执行超时工作默认路由流转:{}, id:{}.", task.getTitle(), task.getId());
							this.execute(task);
							count.incrementAndGet();
						}
					}
				} while (!targets.isEmpty());
				LOGGER.print("完成{}个超时工作默认路由流转, 耗时:{}.", count.intValue(), stamp.consumingMilliseconds());
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void execute(Task task) throws Exception {
		try {
			this.passExpired(task.getId(), task.getJob());
			this.taskTriggerProcessing(task.getId(), task.getJob());
		} catch (Exception e) {
			LOGGER.error(new ExceptionPassExpired(e, task.getId(), task.getTitle()));
		}
	}

	private void passExpired(String id, String job) throws Exception {
		ThisApplication.context().applications()
				.getQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", id, "pass", "expired"), job)
				.getData(ActionPassExpiredWo.class);
	}

	private void taskTriggerProcessing(String id, String job) throws Exception {
		ThisApplication.context().applications()
				.getQuery(x_processplatform_assemble_surface.class,
						Applications.joinQueryUri("task", "v2", id, "trigger", "processing"), job)
				.getData(V2TriggerProcessingWo.class);
	}

	private List<Task> list(EntityManagerContainer emc, String sequence, Map<String, Route> manualToRoute)
			throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.expired), true);
		p = cb.and(p, root.get(Task_.activity).in(manualToRoute.keySet()));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(root.get(JpaObject_.sequence), sequence));
		}
		return em.createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject_.sequence))))
				.setMaxResults(200).getResultList();
	}

	private Map<String, Route> linkPassExpiredManualToRoute(EntityManagerContainer emc) throws Exception {
		List<Route> routes = emc.fetchEqual(Route.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Route.name_FIELDNAME, Route.opinion_FIELDNAME),
				Route.passExpired_FIELDNAME, true);
		List<Manual> manuals = emc.fetchIn(Manual.class, Manual.routeList_FIELDNAME,
				ListTools.extractProperty(routes, JpaObject.id_FIELDNAME, String.class, true, true));
		List<String> ids = new ArrayList<>();
		for (Manual m : manuals) {
			ids.add(m.getId());
		}
		ids = ListTools.trim(ids, true, true);
		Map<String, Route> map = new HashMap<>();
		ids.stream().forEach(m -> {
			for (Route r : routes) {
				map.put(m, r);
			}
		});
		return map;
	}

}