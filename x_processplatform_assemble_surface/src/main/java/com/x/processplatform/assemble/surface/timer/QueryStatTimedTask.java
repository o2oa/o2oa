package com.x.processplatform.assemble.surface.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.application.Application;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryStatTimed;
import com.x.processplatform.core.entity.element.QueryStatTimed_;
import com.x.processplatform.core.entity.element.QueryStat_;

public class QueryStatTimedTask implements Runnable {

	public void run() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.removeUnavailable(emc);
			for (String id : this.listAvailableQueryStat(emc)) {
				if (!this.alreadyTimed(emc, id)) {
					this.concreteQueryStatTimed(emc, id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void concreteQueryStatTimed(EntityManagerContainer emc, String queryStatId) throws Exception {
		QueryStat o = emc.find(queryStatId, QueryStat.class);
		if (null != o) {
			QueryStatTimed timed = new QueryStatTimed();
			timed.setQueryStat(o.getId());
			Application application = ThisApplication.applications
					.randomWithWeight(x_processplatform_assemble_surface.class);
			if (null != application) {
				timed.setProject(application.getToken());
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, o.getTimingInterval());
				timed.setScheduleTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, o.getTimingInterval());
				timed.setExpiredTime(calendar.getTime());
				emc.beginTransaction(QueryStatTimed.class);
				emc.persist(timed, CheckPersistType.all);
				emc.commit();
			}
		}
	}

	private void removeUnavailable(EntityManagerContainer emc) throws Exception {
		List<String> ids = this.listUnavailable(emc);
		if (ListTools.isNotEmpty(ids)) {
			emc.beginTransaction(QueryStatTimed.class);
			emc.delete(QueryStatTimed.class, ids);
			emc.commit();
		}
	}

	private Boolean alreadyTimed(EntityManagerContainer emc, String queryStatId) throws Exception {
		EntityManager em = emc.get(QueryStatTimed.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<QueryStatTimed> root = cq.from(QueryStatTimed.class);
		Predicate p = cb.lessThan(root.get(QueryStatTimed_.queryStat), queryStatId);
		cq.select(cb.count(root));
		long count = em.createQuery(cq.where(p)).getSingleResult();
		return (count > 0);
	}

	private List<String> listUnavailable(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(QueryStatTimed.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryStatTimed> root = cq.from(QueryStatTimed.class);
		Predicate p = cb.lessThan(root.get(QueryStatTimed_.expiredTime), new Date());
		p = cb.or(cb.isNull(root.get(QueryStatTimed_.expiredTime)));
		p = cb.or(cb.isNull(root.get(QueryStatTimed_.scheduleTime)));
		p = cb.or(p, cb.not(root.get(QueryStatTimed_.project).in(listToken())));
		p = cb.or(p, cb.not(root.get(QueryStatTimed_.queryStat).in(listAvailableQueryStat(emc))));
		cq.select(root.get(QueryStatTimed_.id));
		List<String> list = em.createQuery(cq.where(p)).getResultList();
		return list;
	}

	private List<String> listToken() throws Exception {
		List<Application> applications = ThisApplication.applications.get(x_processplatform_assemble_surface.class);
		List<String> tokens = ListTools.extractProperty(applications, "token", String.class, true, true);
		return tokens;
	}

	private List<String> listAvailableQueryStat(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(QueryStat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryStat> root = cq.from(QueryStat.class);
		Predicate p = cb.equal(root.get(QueryStat_.timingEnable), true);
		p = cb.and(p, cb.greaterThan(root.get(QueryStat_.timingInterval), 0));
		cq.select(root.get(QueryStat_.id));
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

}