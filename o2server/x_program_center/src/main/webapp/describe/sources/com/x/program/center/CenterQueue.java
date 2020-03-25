package com.x.program.center;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;

public class CenterQueue extends AbstractQueue<CenterQueueBody> {

	private static Logger logger = LoggerFactory.getLogger(CenterQueue.class);

	public static final int REFRESHAPPLICATIONSINTERVAL = 45;

	protected void execute(CenterQueueBody body) throws Exception {

		switch (body.type()) {
		case CenterQueueBody.TYPE_REGISTAPPLICATIONS:
			CenterQueueRegistApplicationsBody centerQueueRegistApplicationsBody = (CenterQueueRegistApplicationsBody) body;
			registApplications(centerQueueRegistApplicationsBody);
			break;
		case CenterQueueBody.TYPE_REFRESHAPPLICATION:
			this.refresh((CenterQueueRefreshBody) body);
			break;
		default:
			break;
		}

	}

	private void registApplications(CenterQueueRegistApplicationsBody centerQueueRegistApplicationsBody)
			throws Exception {
		Applications applications = ThisApplication.context().applications();
		Date now = new Date();
		for (Application body : centerQueueRegistApplicationsBody) {
			Application application = applications.get(body.getClassName(), body.getNode());
			if (null != application) {
				application.setReportDate(now);
			} else {
				body.setReportDate(now);
				applications.add(body.getClassName(), body);
				Config.resource_node_applicationsTimestamp(now);
				applications.updateTimestamp(now);
				Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
			}
		}
//		if (null != Config.resource_node_applications()) {
//			applications = XGsonBuilder.instance().fromJson(Config.resource_node_applications(), Applications.class);
//			for (Application body : centerQueueRegistApplicationsBody) {
//				Application application = applications.get(body.getClassName(), body.getNode());
//				if (null != application) {
//					application.setReportDate(now);
//					this.updateScheduleLocalRequestAndScheduleRequest(body);
//				} else {
//					body.setReportDate(now);
//					applications.add(body.getClassName(), body);
//					this.updateScheduleLocalRequestAndScheduleRequest(body);
//					updated = true;
//				}
//			}
//		} else {
//			applications = new Applications();
//			for (Application body : centerQueueRegistApplicationsBody) {
//				body.setReportDate(now);
//				applications.add(body.getClassName(), body);
//				this.updateScheduleLocalRequestAndScheduleRequest(body);
//			}
//			updated = true;
//		}

	}

//	private void updateScheduleLocalRequestAndScheduleRequest(Application body) throws Exception {
//		if (ListTools.isNotEmpty(body.getScheduleLocalRequestList())) {
//			business.entityManagerContainer().beginTransaction(ScheduleLocal.class);
//			body.getScheduleLocalRequestList().stream().forEach(o -> {
//				this.updateScheduleLocalRequest(business, body, o);
//			});
//			business.entityManagerContainer().commit();
//		}
//		if (ListTools.isNotEmpty(body.getScheduleRequestList())) {
//			business.entityManagerContainer().beginTransaction(Schedule.class);
//			body.getScheduleRequestList().stream().forEach(o -> {
//				this.updateScheduleRequest(business, body, o);
//			});
//			business.entityManagerContainer().commit();
//		}
//	}
//
//	private void updateScheduleLocalRequest(Business business, Application body, ScheduleLocalRequest request) {
//		try {
//			List<ScheduleLocal> list = business.entityManagerContainer().listEqualAndEqualAndEqual(ScheduleLocal.class,
//					ScheduleLocal.node_FIELDNAME, body.getNode(), ScheduleLocal.application_FIELDNAME,
//					body.getClassName(), ScheduleLocal.className_FIELDNAME, request.getClassName());
//			if (!list.isEmpty()) {
//				ScheduleLocal o = list.get(0);
//				o.setClassName(request.getClassName());
//				o.setApplication(body.getClassName());
//				o.setNode(body.getNode());
//				o.setCron(request.getCron());
//				o.setDelay(request.getDelay());
//				o.setInterval(request.getInterval());
//				o.setReportTime(new Date());
//				business.entityManagerContainer().check(o, CheckPersistType.all);
//			} else {
//				ScheduleLocal o = new ScheduleLocal();
//				o.setClassName(request.getClassName());
//				o.setApplication(body.getClassName());
//				o.setNode(body.getNode());
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
//	private void updateScheduleRequest(Business business, Application body, ScheduleRequest request) {
//		try {
//			List<Schedule> list = business.entityManagerContainer().listEqualAndEqualAndEqual(Schedule.class,
//					Schedule.node_FIELDNAME, body.getNode(), Schedule.application_FIELDNAME, body.getClassName(),
//					Schedule.className_FIELDNAME, request.getClassName());
//			if (!list.isEmpty()) {
//				Schedule o = list.get(0);
//				o.setClassName(request.getClassName());
//				o.setApplication(body.getClassName());
//				o.setNode(body.getNode());
//				o.setCron(request.getCron());
//				o.setWeight(body.getScheduleWeight());
//				o.setReportTime(new Date());
//				business.entityManagerContainer().check(o, CheckPersistType.all);
//			} else {
//				Schedule o = new Schedule();
//				o.setClassName(request.getClassName());
//				o.setApplication(body.getClassName());
//				o.setNode(body.getNode());
//				o.setCron(request.getCron());
//				o.setWeight(body.getScheduleWeight());
//				o.setReportTime(new Date());
//				business.entityManagerContainer().persist(o);
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}

	private void refresh(CenterQueueRefreshBody body) throws Exception {
		Applications applications = ThisApplication.context().applications();
		Date now = new Date();
		List<String> removeEntries = new ArrayList<>();
		boolean modify = false;
		for (Entry<String, CopyOnWriteArrayList<Application>> en : applications.entrySet()) {
			List<Application> removeApplications = new ArrayList<>();
			for (Application application : en.getValue()) {
				if ((now.getTime() - application.getReportDate().getTime()) > REFRESHAPPLICATIONSINTERVAL * 3 * 1000) {
					removeApplications.add(application);
				}
			}
			modify = modify || en.getValue().removeAll(removeApplications);
			if (en.getValue().isEmpty()) {
				removeEntries.add(en.getKey());
			}
		}
		if (ListTools.isNotEmpty(removeEntries)) {
			modify = true;
			for (String str : removeEntries) {
				ThisApplication.context().applications().remove(str);
			}
		}
		if (modify) {
			applications.updateTimestamp(now);
			Config.resource_node_applicationsTimestamp(now);
			Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
		}
	}
}
