package com.x.processplatform.service.processing.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ProcessPlatform;
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
import com.x.processplatform.core.entity.element.Manual_;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Route_;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.neural.NeuralAnalyzer;

public class Expire extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Expire.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TimeStamp stamp = new TimeStamp();
			Business business = new Business(emc);
			/* 将所有已经过期的进行标记 */
			this.expired(business);
			/* 将有passExpire的处理掉 */
			this.passExpired(business);
			/* 前面对象读取过,冲掉 */
			emc.flush();
			/* 有过期的待办需要处理 */
			Long count = emc.countEqual(Task.class, Task.expired_FIELDNAME, true);
			if (StringUtils.equals(ProcessPlatform.Expire.AUTO_SINGLE, Config.processPlatform().getExpire().getAuto())
					|| StringUtils.equals(ProcessPlatform.Expire.AUTO_NEURAL,
							Config.processPlatform().getExpire().getAuto())) {
				if (count > 0) {
					switch (Config.processPlatform().getExpire().getAuto()) {
					case ProcessPlatform.Expire.AUTO_SINGLE:
						this.single(business);
						break;
					case ProcessPlatform.Expire.AUTO_NEURAL:
						this.neural(business);
						break;
					default:
						break;
					}
					logger.print("检查到 {} 个超期的待办, 自动处理方式:{}, 耗时:{}.", count,
							Config.processPlatform().getExpire().getAuto(), stamp.consumingMilliseconds());
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void single(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Task> tasks = emc.fetchEqual(Task.class, ListTools.toList(Task.activity_FIELDNAME, JpaObject.id_FIELDNAME),
				Task.expired_FIELDNAME, true);
		/* 触发的待办 */
		List<String> trigger_ids = new ArrayList<>();
		tasks.stream().collect(Collectors.groupingBy(Task::getActivity)).entrySet().stream().forEach(o -> {
			List<Route> routes;
			try {
				routes = business.element().listRouteWithManual(o.getKey());
				/* 查找活动的路由数量,如果数量为一直接流转 */
				if (routes.size() == 1) {
					Route route = routes.get(0);
					HashSet<String> workIds = new HashSet<>();
					o.getValue().forEach(p -> {
						Task task;
						try {
							task = emc.find(p.getId(), Task.class);
							if (null != task) {
								emc.beginTransaction(Task.class);
								task.setRouteName(route.getName());
								if (StringUtils.isEmpty(task.getOpinion())) {
									task.setOpinion(route.getOpinion());
								}
								emc.commit();
								ReqTaskProcessing _req = new ReqTaskProcessing();
								_req.setProcessingType(ProcessingType.expire);
								_req.setFinallyProcessingWork(false);
								ThisApplication.context().applications()
										.putQuery(x_processplatform_service_processing.class,
												Applications.joinQueryUri("task", task.getId(), "processing"), _req)
										.getData(WoId.class);
								logger.print("超时待办单一路由处理, 标题:{}, 路由:{}.", task.getTitle(), task.getRouteName());
								trigger_ids.add(task.getId());
								workIds.add(task.getWork());
							}
						} catch (Exception e) {
							logger.error(e);
						}
					});
					/* task全部处理完成后再进行work的处理 */
					workIds.stream().forEach(w -> {
						try {
							ThisApplication.context().applications()
									.putQuery(x_processplatform_service_processing.class,
											Applications.joinQueryUri("work", w, "processing"),
											new ProcessingAttributes())
									.getData(WoId.class);
							logger.print("超时待办单一路由处理, 触发工作继续流转, work:{}.", w);
						} catch (Exception e) {
							logger.error(e);
						}
					});
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
		logger.debug("超时待办单一路由处理, 总数:{}, 处理数量:{}, 耗时:{}.", tasks.size(), trigger_ids.size(),
				stamp.consumingMilliseconds());
	}

	private void neural(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Task> tasks = emc.fetchEqual(Task.class, ListTools.toList(Task.activity_FIELDNAME, JpaObject.id_FIELDNAME),
				Task.expired_FIELDNAME, true);
		NeuralAnalyzer analyzer = new NeuralAnalyzer();
		/* 触发的待办 */
		List<String> trigger_ids = new ArrayList<>();
		tasks.stream().collect(Collectors.groupingBy(Task::getActivity)).entrySet().stream().forEach(o -> {
			try {
				HashSet<String> workIds = new HashSet<>();
				o.getValue().forEach(p -> {
					Task task;
					try {
						task = emc.find(p.getId(), Task.class);
						if (null != task) {
							Route route = analyzer.analysis(business, task);
							if (null != route) {
								emc.beginTransaction(Task.class);
								task.setRouteName(route.getName());
								if (StringUtils.isEmpty(task.getOpinion())) {
									task.setOpinion(route.getOpinion());
								}
								emc.commit();
								logger.debug("超时待办人工神经网络处理, 标题:{}, 路由:{}.", task.getTitle(), task.getRouteName());
								ReqTaskProcessing _req = new ReqTaskProcessing();
								_req.setProcessingType(ProcessingType.expire);
								_req.setFinallyProcessingWork(false);
								ThisApplication.context().applications()
										.putQuery(x_processplatform_service_processing.class,
												Applications.joinQueryUri("task", task.getId(), "processing"), _req)
										.getData(WoId.class);
								workIds.add(task.getWork());
								trigger_ids.add(task.getId());
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
				});
				/* task全部处理完成后再进行work的处理 */
				workIds.stream().forEach(w -> {
					try {
						ThisApplication.context().applications()
								.putQuery(x_processplatform_service_processing.class,
										Applications.joinQueryUri("work", w, "processing"), new ProcessingAttributes())
								.getData(WoId.class);
						logger.debug("超时待办人工神经网络处理, 触发工作继续流转, work:{}.", w);
					} catch (Exception e) {
						logger.error(e);
					}
				});
			} catch (Exception e) {
				logger.error(e);
			}
		});
		logger.print("超时待办人工神经网络处理, 总数:{}, 处理数量:{}, 耗时:{}.", tasks.size(), trigger_ids.size(),
				stamp.consumingMilliseconds());
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

	private void passExpired(Business business) throws Exception {
		TimeStamp stamp = new TimeStamp();
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Task> tasks = this.passExpired_task_list(business);
		/* 触发的待办 */
		List<String> trigger_ids = new ArrayList<>();
		tasks.stream().collect(Collectors.groupingBy(Task::getActivity)).entrySet().forEach(o -> {
			try {
				List<Route> routes = business.element().listRouteWithManual(o.getKey());
				Route passExpiredRoute = routes.stream().filter(r -> {
					return BooleanUtils.isTrue(r.getPassExpired());
				}).findFirst().orElse(null);
				if (null != passExpiredRoute) {
					HashSet<String> workIds = new HashSet<>();
					o.getValue().forEach(p -> {
						Task task;
						try {
							task = emc.find(p.getId(), Task.class);
							if (null != task) {
								emc.beginTransaction(Task.class);
								task.setRouteName(passExpiredRoute.getName());
								if (StringUtils.isEmpty(task.getOpinion())) {
									task.setOpinion(passExpiredRoute.getOpinion());
								}
								emc.commit();
								ReqTaskProcessing _req = new ReqTaskProcessing();
								_req.setProcessingType(ProcessingType.expire);
								_req.setFinallyProcessingWork(false);
								/*
								 * 如果直接触发work的流转,会导致task的多线程读写,报乐观锁错误 Caused by:
								 * <openjpa-3.0.0-SNAPSHOT-r422266:1819982 nonfatal store error>
								 * org.apache.openjpa.persistence.OptimisticLockException: Optimistic locking
								 * errors were detected when flushing to the data store. The following objects
								 * may have been concurrently modified in another transaction:
								 * [com.x.processplatform.core.entity.content.Task-6cae129c-a753-4169-b213-
								 * a521057ae03d]
								 */
								ThisApplication.context().applications()
										.putQuery(x_processplatform_service_processing.class,
												Applications.joinQueryUri("task", task.getId(), "processing"), _req)
										.getData(WoId.class);
								logger.debug("超时待办默认路由处理, 标题: {}, 路由: {}.", task.getTitle(), task.getRouteName());
								trigger_ids.add(task.getId());
								workIds.add(task.getWork());
							}
						} catch (Exception e) {
							logger.error(e);
						}
					});
					/* task全部处理完成后再进行work的处理 */
					workIds.stream().forEach(w -> {
						try {
							ThisApplication.context().applications()
									.putQuery(x_processplatform_service_processing.class,
											Applications.joinQueryUri("work", w, "processing"),
											new ProcessingAttributes())
									.getData(WoId.class);
							logger.debug("超时待办默认路由处理, 触发工作继续流转, 标题: {}.", w);
						} catch (Exception e) {
							logger.error(e);
						}
					});
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
		logger.print("超时待办默认路由处理, 总数: {}, 处理数量: {}, 耗时: {}.", tasks.size(), trigger_ids.size(),
				stamp.consumingMilliseconds());
	}

	private List<Task> passExpired_task_list(Business business) throws Exception {
		List<String> manualIds = this.passExpired_manual_list(business);
		List<Task> os = business.entityManagerContainer().fetchIn(Task.class,
				ListTools.toList(Task.activity_FIELDNAME, Task.identity_FIELDNAME), Task.activity_FIELDNAME, manualIds);
		return os;
	}

	private List<String> passExpired_manual_list(Business business) throws Exception {
		List<String> routeIds = this.passExpired_route_list(business);
		EntityManager em = business.entityManagerContainer().get(Manual.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Manual> root = cq.from(Manual.class);
		Predicate p = root.get(Manual_.routeList).in(routeIds);
		cq.select(root.get(Manual_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	private List<String> passExpired_route_list(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Route.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Route> root = cq.from(Route.class);
		Predicate p = cb.equal(root.get(Route_.passExpired), true);
		cq.select(root.get(Route_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	/* 将所有过期任务标识为过期,并进行通知 */
	private void expired(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.or(cb.isNull(root.get(Task_.expired)), cb.equal(root.get(Task_.expired), false));
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Task_.expireTime), new Date()));
		cq.select(root.get(Task_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		if (!os.isEmpty()) {
			for (String id : os) {
				Task task = business.entityManagerContainer().find(id, Task.class);
				if (null != task) {
					business.entityManagerContainer().beginTransaction(Task.class);
					task.setExpired(true);
					business.entityManagerContainer().commit();
					MessageFactory.task_expire(task);
				}
			}
		}
	}

}