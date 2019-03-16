package com.x.program.center.jaxrs.schedule;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.CronExpression;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Schedule;
import com.x.program.center.core.entity.Schedule_;

class ActionFire extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFire.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Schedule schedule = emc.find(id, Schedule.class);
			if (null == schedule) {
				throw new ExceptionEntityNotExist(id, Schedule.class);
			}
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			this.fire(effectivePerson, business, schedule);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void fire(EffectivePerson effectivePerson, Business business, Schedule schedule) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Schedule.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Schedule> cq = cb.createQuery(Schedule.class);
		Root<Schedule> root = cq.from(Schedule.class);
		Predicate p = cb.equal(root.get(Schedule_.application), schedule.getApplication());
		p = cb.and(p, cb.equal(root.get(Schedule_.className), schedule.getClassName()));
		List<Schedule> list = em.createQuery(cq.select(root).where(p)).getResultList();
		emc.beginTransaction(Schedule.class);
		/* 先设定下一时间 */
		Date date = this.cron(schedule.getCron());
		list.stream().forEach(o -> {
			o.setFireTime(date);
		});
		emc.commit();
		Application app = ThisApplication.context().applications().randomWithWeight(schedule.getApplication());
		if (null != app) {
			String url = app.getUrlRoot()
					+ Applications.joinQueryUri("fireschedule", "classname", schedule.getClassName());
			CipherConnectionAction.get(effectivePerson.getDebugger(), url);
			logger.info("fire schedule node: {}, application: {}, task: {}.", app.getNode(), app.getContextPath(),
					schedule.getClassName());
		} else {
			logger.info("can not fire schedule application: {}, task: {}.", schedule.getApplication(),
					schedule.getClassName());
		}
	}

	private Date cron(String expression) throws Exception {
		CronExpression cron = new CronExpression(expression);
		return cron.getNextValidTimeAfter(new Date());
	}

	public static class Wo extends WrapBoolean {
	}

}
