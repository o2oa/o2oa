package com.x.processplatform.service.processing.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ThisApplication;

public class PassExpired extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(PassExpired.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			String sequence = null;
			List<Task> targets = new ArrayList<>();
			Map<String, Route> manualToRoute = null;
			AtomicInteger count = new AtomicInteger(0);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				if (null == manualToRoute) {
					manualToRoute = this.linkPassExpiredManualToRoute(emc);
				}
			}
			if (!manualToRoute.isEmpty()) {
				do {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						targets = this.list(emc, sequence, manualToRoute);
					}
					if (!targets.isEmpty()) {
						sequence = targets.get(targets.size() - 1).getSequence();
						for (Task task : targets) {
							try {
								try {
									ThisApplication.context().applications()
											.getQuery(x_processplatform_service_processing.class,
													Applications.joinQueryUri("task", task.getId(), "pass", "expired"),
													task.getJob())
											.getData(WoId.class);
									count.incrementAndGet();
								} catch (Exception e) {
									throw new ExceptionPassExpired(e, task.getId(), task.getTitle(),
											task.getSequence());
								}
							} catch (Exception e) {
								logger.error(e);
							}
						}
					}
				} while (!targets.isEmpty());
				logger.print("完成{}个超时工作默认路由流转, 耗时:{}.", count.intValue(), stamp.consumingMilliseconds());
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<Task> list(EntityManagerContainer emc, String sequence, Map<String, Route> manualToRoute)
			throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Path<String> id_path = root.get(Task_.id);
		Path<String> job_path = root.get(Task_.job);
		Path<String> sequence_path = root.get(Task_.sequence);
		Predicate p = cb.equal(root.get(Task_.expired), true);
		p = cb.and(p, root.get(Task_.activity).in(manualToRoute.keySet()));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequence_path, sequence));
		}
		cq.multiselect(id_path, job_path, sequence_path).where(p).orderBy(cb.asc(sequence_path));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Task> list = new ArrayList<>();
		for (Tuple o : os) {
			Task task = new Task();
			task.setId(o.get(id_path));
			task.setJob(o.get(job_path));
			task.setSequence(o.get(sequence_path));
			list.add(task);
		}
		return list;
	}

	public static class ReqTaskProcessing extends GsonPropertyObject {

		@FieldDescribe("流转类型.")
		private ProcessingType processingType;

		@FieldDescribe("最后是否触发work的流转,默认流转.")
		private Boolean finallyProcessingWork;

		public ProcessingType getProcessingType() {
			return processingType;
		}

		public void setProcessingType(ProcessingType processingType) {
			this.processingType = processingType;
		}

		public Boolean getFinallyProcessingWork() {
			return finallyProcessingWork;
		}

		public void setFinallyProcessingWork(Boolean finallyProcessingWork) {
			this.finallyProcessingWork = finallyProcessingWork;
		}
	}

	private Map<String, Route> linkPassExpiredManualToRoute(EntityManagerContainer emc) throws Exception {
		List<Route> routes = emc.fetchEqual(Route.class,
				ListTools.toList(Route.id_FIELDNAME, Route.name_FIELDNAME, Route.opinion_FIELDNAME),
				Route.passExpired_FIELDNAME, true);
		List<Manual> manuals = emc.fetchIn(Manual.class, Manual.routeList_FIELDNAME,
				ListTools.extractProperty(routes, Route.id_FIELDNAME, String.class, true, true));
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