package com.x.program.center.schedule;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.ScheduleRequest;
import com.x.base.core.project.tools.CronTools;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.ScheduleLog_;

public class FireSchedule extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(FireSchedule.class);

	private static final CopyOnWriteArrayList<String> LOCK = new CopyOnWriteArrayList<>();

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				this.fire();
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void fire() throws Exception {
		Applications applications = ThisApplication.context().applications();
		applications.entrySet().stream().forEach(entry -> {
			Date now = new Date();
			Application application = applications.randomWithWeight(entry.getKey());
			for (ScheduleRequest request : application.getScheduleRequestList()) {
				try {
					if (LOCK.contains(request.getClassName())) {
						throw new ExceptionScheduleLastNotEnd(request, application.getClassName());
					}
					Date lastStartTime = this.getLastStartTime(request);
					Date date = CronTools.next(request.getCron(), lastStartTime);
					if (date.before(now)) {
						try {
							LOCK.add(request.getClassName());
							LOGGER.info("fire schedule className: {}, cron: {}, node: {}, application: {}.",
									request.getClassName(), request.getCron(), application.getNode(),
									application.getClassName());
							String url = application.getUrlJaxrsRoot()
									+ Applications.joinQueryUri("fireschedule", "classname", request.getClassName());
							request.setLastStartTime(now);
							CipherConnectionAction.get(false, url);
						} finally {
							LOCK.remove(request.getClassName());
						}
					}
				} catch (Exception e) {
					LOGGER.error(new ExceptionScheduleFire(e, request.getClassName(), request.getCron(),
							application.getNode(), application.getClassName()));
				}
			}
		});
	}

	private Date getLastStartTime(ScheduleRequest request) throws Exception {
		Date lastStartTime = request.getLastStartTime();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(ScheduleLog.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ScheduleLog> cq = cb.createQuery(ScheduleLog.class);
			Root<ScheduleLog> root = cq.from(ScheduleLog.class);
			Predicate p = cb.equal(root.get(ScheduleLog_.className), request.getClassName());
			List<ScheduleLog> os = em
					.createQuery(cq.select(root).where(p).orderBy(cb.desc(root.get(ScheduleLog_.fireTime))))
					.setMaxResults(1).getResultList();
			if (!os.isEmpty()) {
				lastStartTime = os.get(0).getFireTime();
			}
		}
		return lastStartTime;
	}
}