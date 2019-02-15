package com.x.program.center.schedule;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Schedule;
import com.x.program.center.core.entity.Schedule_;

public class FireSchedule implements Job {

	private static Logger logger = LoggerFactory.getLogger(FireSchedule.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<Pair> list = this.list(business);
			list.stream().forEach(p -> {
				this.fire(business, p);
			});
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void fire(Business business, Pair pair) {
		try {
			EntityManagerContainer emc = business.entityManagerContainer();
			EntityManager em = emc.get(Schedule.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Schedule> cq = cb.createQuery(Schedule.class);
			Root<Schedule> root = cq.from(Schedule.class);
			Predicate p = cb.equal(root.get(Schedule_.application), pair.getApplication());
			p = cb.and(p, cb.equal(root.get(Schedule_.className), pair.getClassName()));
			List<Schedule> list = em.createQuery(cq.select(root).where(p)).getResultList();
			if (!list.isEmpty()) {
				list = list
						.stream().sorted(Comparator
								.comparing(Schedule::getReportTime, Comparator.nullsFirst(Date::compareTo)).reversed())
						.collect(Collectors.toList());
				Schedule latest = list.get(0);
				if ((!Objects.isNull(latest.getFireTime())) && (latest.getFireTime().before(new Date()))) {
					/* 有设定值,需要触发 */
					emc.beginTransaction(Schedule.class);
					/* 先设定下一时间 */
					Date date = this.cron(latest.getCron());
					list.stream().forEach(o -> {
						o.setFireTime(date);
					});
					emc.commit();
					Application app = ThisApplication.context().applications()
							.randomWithWeight(latest.getApplication());
					if (null != app) {
						String url = app.getUrlRoot()
								+ Applications.joinQueryUri("fireschedule", "classname", latest.getClassName());
						CipherConnectionAction.get(false, url);
						logger.info("fire schedule node: {}, application: {}, task: {}.", app.getNode(),
								app.getContextPath(), latest.getClassName());
					} else {
						logger.info("can not fire schedule application: {}, task: {}.", latest.getApplication(),
								latest.getClassName());
					}
				} else {
					emc.beginTransaction(Schedule.class);
					/* 重新计算本轮时间 */
					Date date = this.cron(latest.getCron());
					list.stream().forEach(o -> {
						o.setFireTime(date);
					});
					emc.commit();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private Date cron(String expression) throws Exception {
		CronExpression cron = new CronExpression(expression);
		return cron.getNextValidTimeAfter(new Date());
	}

	private List<Pair> list(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Schedule.class);
		EntityManager em = emc.get(Schedule.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Schedule> root = cq.from(Schedule.class);
		Path<String> pathApplication = root.get(Schedule_.application);
		Path<String> pathClassName = root.get(Schedule_.className);
		List<Tuple> list = em.createQuery(cq.multiselect(pathApplication, pathClassName)).getResultList();
		List<Pair> pairs = list.stream().map(o -> {
			return new Pair(o.get(pathApplication), o.get(pathClassName));
		}).distinct().collect(Collectors.toList());
		return pairs;
	}

	class Pair {
		Pair(String application, String className) {
			this.application = application;
			this.className = className;
		}

		private String application;
		private String className;

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((application == null) ? 0 : application.hashCode());
			result = prime * result + ((className == null) ? 0 : className.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (application == null) {
				if (other.application != null)
					return false;
			} else if (!application.equals(other.application))
				return false;
			if (className == null) {
				if (other.className != null)
					return false;
			} else if (!className.equals(other.className))
				return false;
			return true;
		}

		private FireSchedule getOuterType() {
			return FireSchedule.this;
		}
	}

}