//package com.x.program.center;
//
//import java.util.Date;
//import java.util.List;
//
//import javax.persistence.EntityManager;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.Application;
//import com.x.base.core.project.config.ApplicationServer;
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.queue.AbstractQueue;
//import com.x.base.core.project.schedule.ReportToCenter.Report;
//import com.x.base.core.project.schedule.ScheduleLocalRequest;
//import com.x.base.core.project.schedule.ScheduleRequest;
//import com.x.base.core.project.tools.ListTools;
//import com.x.base.core.project.tools.StringTools;
//import com.x.program.center.core.entity.Schedule;
//import com.x.program.center.core.entity.ScheduleLocal;
//import com.x.program.center.core.entity.ScheduleLocal_;
//import com.x.program.center.core.entity.Schedule_;
//
//public class ReportQueue extends AbstractQueue<Report> {
//
//	private static Logger logger = LoggerFactory.getLogger(ReportQueue.class);
//
//	@Override
//	protected void execute(Report report) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			Application application = ThisApplication.context().applications().get(report.getClassName(),
//					report.getToken());
//			if (null != application) {
//				application.setReportDate(new Date());
//			} else {
//				ApplicationServer applicationServer = Config.nodes().applicationServers().get(report.getNode());
//				application = new Application();
//				application.setName(report.getName());
//				application.setNode(report.getNode());
//				application.setPort(applicationServer.getPort());
//				application.setContextPath(report.getContextPath());
//				application.setToken(report.getToken());
//				application.setWeight((null == report.getWeight()) ? 100 : report.getWeight());
//				application.setSslEnable(report.getSslEnable());
//				application.setReportDate(new Date());
//				application.setProxyPort(applicationServer.getProxyPort());
//				application.setProxyHost(applicationServer.getProxyHost());
//				ThisApplication.context().applications().add(report.getClassName(), application);
//				ThisApplication.context().applications().setToken(StringTools.uniqueToken());
//			}
//			if (ListTools.isNotEmpty(report.getScheduleLocalRequestList())) {
//				emc.beginTransaction(ScheduleLocal.class);
//				report.getScheduleLocalRequestList().stream().forEach(o -> {
//					this.update(business, o);
//				});
//				emc.commit();
//			}
//			if (ListTools.isNotEmpty(report.getScheduleRequestList())) {
//				emc.beginTransaction(Schedule.class);
//				report.getScheduleRequestList().stream().forEach(o -> {
//					this.update(business, o);
//				});
//				emc.commit();
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void update(Business business, ScheduleLocalRequest request) {
//		try {
//			EntityManager em = business.entityManagerContainer().get(ScheduleLocal.class);
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<ScheduleLocal> cq = cb.createQuery(ScheduleLocal.class);
//			Root<ScheduleLocal> root = cq.from(ScheduleLocal.class);
//			Predicate p = cb.equal(root.get(ScheduleLocal_.node), request.getNode());
//			p = cb.and(p, cb.equal(root.get(ScheduleLocal_.application), request.getApplication()));
//			p = cb.and(p, cb.equal(root.get(ScheduleLocal_.className), request.getClassName()));
//			cq.select(root).where(p).distinct(true);
//			List<ScheduleLocal> list = em.createQuery(cq).setMaxResults(1).getResultList();
//			if (!list.isEmpty()) {
//				ScheduleLocal o = list.get(0);
//				o.setClassName(request.getClassName());
//				o.setApplication(request.getApplication());
//				o.setNode(request.getNode());
//				o.setCron(request.getCron());
//				o.setDelay(request.getDelay());
//				o.setInterval(request.getInterval());
//				o.setReportTime(new Date());
//			} else {
//				ScheduleLocal o = new ScheduleLocal();
//				o.setClassName(request.getClassName());
//				o.setApplication(request.getApplication());
//				o.setNode(request.getNode());
//				o.setCron(request.getCron());
//				o.setDelay(request.getDelay());
//				o.setInterval(request.getInterval());
//				o.setReportTime(new Date());
//				business.entityManagerContainer().persist(o);
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void update(Business business, ScheduleRequest request) {
//		try {
//			EntityManager em = business.entityManagerContainer().get(Schedule.class);
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Schedule> cq = cb.createQuery(Schedule.class);
//			Root<Schedule> root = cq.from(Schedule.class);
//			Predicate p = cb.equal(root.get(Schedule_.node), request.getNode());
//			p = cb.and(p, cb.equal(root.get(Schedule_.application), request.getApplication()));
//			p = cb.and(p, cb.equal(root.get(Schedule_.className), request.getClassName()));
//			cq.select(root).where(p).distinct(true);
//			List<Schedule> list = em.createQuery(cq).setMaxResults(1).getResultList();
//			if (!list.isEmpty()) {
//				Schedule o = list.get(0);
//				o.setClassName(request.getClassName());
//				o.setApplication(request.getApplication());
//				o.setNode(request.getNode());
//				o.setCron(request.getCron());
//				o.setReportTime(new Date());
//			} else {
//				Schedule o = new Schedule();
//				o.setClassName(request.getClassName());
//				o.setApplication(request.getApplication());
//				o.setNode(request.getNode());
//				o.setCron(request.getCron());
//				o.setReportTime(new Date());
//				business.entityManagerContainer().persist(o);
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//}