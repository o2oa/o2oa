package com.x.processplatform.service.processing.processor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.ReviewType;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.log.ProcessingError;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.configurator.ActivityProcessingConfigurator;

public abstract class AbstractBaseProcessor {

	private static Logger logger = LoggerFactory.getLogger(AbstractBaseProcessor.class);

	private EntityManagerContainer entityManagerContainer;

	private Business business;

	protected AbstractBaseProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		this.entityManagerContainer = entityManagerContainer;
		this.business = new Business(this.entityManagerContainer);
	}

	protected EntityManagerContainer entityManagerContainer() {
		return this.entityManagerContainer;
	}

	protected Business business() {
		return this.business;
	}

	protected static Integer MAX_ERROR_RETRY = 5;

	protected static String BAS = "beforeArriveScript";
	protected static String BAST = "beforeArriveScriptText";
	protected static String AAS = "afterArriveScript";
	protected static String AAST = "afterArriveScriptText";
	protected static String BES = "beforeExecuteScript";
	protected static String BEST = "beforeExecuteScriptText";
	protected static String AES = "afterExecuteScript";
	protected static String AEST = "afterExecuteScriptText";
	protected static String BIS = "beforeInquireScript";
	protected static String BIST = "beforeInquireScriptText";
	protected static String AIS = "afterInquireScript";
	protected static String AIST = "afterInquireScriptText";

	/* manual活动特有 */
	protected static String BAES = "beforeArrivedExecuteScript";
	protected static String BAEST = "beforeArrivedExecuteScriptText";
	protected static String AAES = "afterArrivedExecuteScript";
	protected static String AAEST = "afterArrivedExecuteScriptText";

	protected static String Binding_name_routes = "routes";
	protected static String Binding_name_route = "route";

	protected void arriveActivity(ActivityProcessingConfigurator activityConfigurator, Work work, Activity activity)
			throws Exception {
		String token = work.getActivityToken();
		if (activityConfigurator.getChangeActivityToken()) {
			token = StringTools.uniqueToken();
		}
		Date date = new Date();
		business.entityManagerContainer().beginTransaction(WorkLog.class);
		if (activityConfigurator.getStampArrivedWorkLog()) {
			if (!activity.getActivityType().equals(ActivityType.begin)) {
				this.stampArrivedWorkLog(business, work, activity, token, date);
			}
		}
		if (activityConfigurator.getCreateFromWorkLog()) {
			if ((!activity.getActivityType().equals(ActivityType.cancel))
					&& (!activity.getActivityType().equals(ActivityType.end))) {
				WorkLog o = this.createWorkLog(work, activity, token, date);
				this.entityManagerContainer.persist(o, CheckPersistType.all);
			}
		}
		work.setActivityToken(token);
		work.setActivityArrivedTime(date);
		work.setActivity(activity.getId());
		work.setActivityName(activity.getName());
		work.setActivityType(activity.getActivityType());
		work.setWorkStatus(WorkStatus.processing);
		work.setDestinationActivity(null);
		work.setDestinationActivityType(null);
		work.setDestinationRoute(null);
		work.setDestinationRouteName(null);
		work.setExecuted(false);
		work.setInquired(false);
		work.setErrorRetry(0);
		if (StringUtils.isNotEmpty(activity.getForm())) {
			/** 检查表单存在 */
			if (null != this.business().element().get(activity.getForm(), Form.class)) {
				work.setForm(activity.getForm());
			}
		}
	}

	protected WorkLog stampArrivedWorkLog(Business business, Work work, Activity activity, String token, Date date)
			throws Exception {
		WorkLog workLog = null;
		String id = business.workLog().getWithFromActivityTokenWithNotConnected(work.getActivityToken());
		if (StringUtils.isNotEmpty(id)) {
			workLog = business.entityManagerContainer().find(id, WorkLog.class);
			this.connectWorkLog(activity, workLog, work, token, date);
			workLog.setDuration(Config.workTime().betweenMinutes(workLog.getFromTime(), workLog.getArrivedTime()));
		} else {
			List<String> ids = business.workLog().listWithFromActivityToken(work.getActivityToken());
			if (!ids.isEmpty()) {
				workLog = new WorkLog();
				WorkLog template = business.entityManagerContainer().find(ids.get(0), WorkLog.class);
				template.copyTo(workLog, JpaObject.ID_DISTRIBUTEFACTOR);
				this.connectWorkLog(activity, workLog, work, token, date);
				workLog.setDuration(Config.workTime().betweenMinutes(workLog.getFromTime(), workLog.getArrivedTime()));
				business.entityManagerContainer().persist(workLog, CheckPersistType.all);
			} else {
				throw new Exception("can not get workLog form activityToken:" + work.getActivityToken() + ".");
			}
		}
		return workLog;
	}

	protected void logProcessingError(String workId, Exception e) {
		try {
			e.printStackTrace();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
					StringWriter stringWriter = new StringWriter();
					PrintWriter printWriter = new PrintWriter(stringWriter)) {
				e.printStackTrace(printWriter);
				ProcessingError error = new ProcessingError();
				error.setMessage(e.getMessage());
				error.setData(stringWriter.getBuffer().toString());
				error.setWork(workId);
				emc.beginTransaction(ProcessingError.class);
				emc.persist(error);
				emc.commit();
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Work work = emc.find(workId, Work.class, ExceptionWhen.none);
				if (null != work) {
					emc.beginTransaction(Work.class);
					work.setErrorRetry(work.getErrorRetry() + 1);
					if (work.getErrorRetry() >= MAX_ERROR_RETRY) {
						work.setWorkStatus(WorkStatus.hanging);
					}
					emc.commit();
				}
			}
		} catch (Exception en) {
			en.printStackTrace();
		}
	}

	protected Work copyWork(Work work) throws Exception {
		Work copy = new Work();
		work.copyTo(copy, "id", "distributeFactor");
		return copy;
	}

	protected Review createReview(Business business, String identity, Manual manual, Work work) throws Exception {
		Review review = new Review();
		review.setJob(work.getJob());
		review.setWork(work.getId());
		review.setCompleted(false);
		review.setTitle(work.getTitle());
		review.setStartTime(work.getStartTime());
		review.setApplication(work.getApplication());
		review.setApplicationName(work.getApplicationName());
		review.setProcess(work.getProcess());
		review.setProcessName(work.getProcessName());
		review.setIdentity(identity);
		review.setDepartment(business.organization().department().getWithIdentity(identity).getName());
		review.setCompany(business.organization().company().getWithName(identity).getName());
		review.setPerson(business.organization().person().getWithIdentity(identity).getName());
		review.setReviewType(ReviewType.review);
		review.setActivity(work.getActivity());
		review.setActivityName(work.getActivityName());
		review.setActivityToken(work.getActivityToken());
		review.setActivityType(work.getActivityType());
		return review;
	}

	protected boolean hasArriveScript(Activity activity) throws Exception {
		return this.hasBeforeArriveScript(activity) || this.hasAfterArriveScript(activity);
	}

	protected boolean hasBeforeArriveScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(BAS, String.class))
				|| StringUtils.isNotEmpty(activity.get(BAST, String.class));
	}

	protected boolean hasAfterArriveScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(AAS, String.class))
				|| StringUtils.isNotEmpty(activity.get(AAST, String.class));
	}

	protected boolean hasExecuteScript(Activity activity) throws Exception {
		return this.hasBeforeExecuteScript(activity) || this.hasAfterExecuteScript(activity);
	}

	protected boolean hasBeforeExecuteScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(BES, String.class))
				|| StringUtils.isNotEmpty(activity.get(BEST, String.class));
	}

	protected boolean hasAfterExecuteScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(AES, String.class))
				|| StringUtils.isNotEmpty(activity.get(AEST, String.class));
	}

	protected boolean hasInquireScript(Activity activity) throws Exception {
		return this.hasBeforeInquireScript(activity) || this.hasAfterInquireScript(activity);
	}

	protected boolean hasBeforeInquireScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(BIS, String.class))
				|| StringUtils.isNotEmpty(activity.get(BIST, String.class));
	}

	protected boolean hasAfterInquireScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(AIS, String.class))
				|| StringUtils.isNotEmpty(activity.get(AIST, String.class));
	}

	protected boolean hasBeforeArrivedExecuteScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(BAES, String.class))
				|| StringUtils.isNotEmpty(activity.get(BAEST, String.class));
	}

	protected boolean hasAfterArrivedExecuteScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(AAES, String.class))
				|| StringUtils.isNotEmpty(activity.get(AAEST, String.class));
	}

	protected WorkLog createWorkLog(Work work, Activity activity, String token, Date date) throws Exception {
		WorkLog workLog = new WorkLog();
		workLog.setJob(work.getJob());
		workLog.setWork(work.getId());
		workLog.setProcess(work.getProcess());
		workLog.setProcessName(work.getProcessName());
		workLog.setApplication(work.getApplication());
		workLog.setApplicationName(work.getApplicationName());
		workLog.setFromActivity(activity.getId());
		workLog.setFromActivityName(activity.getName());
		workLog.setFromActivityToken(token);
		workLog.setFromActivityType(activity.getActivityType());
		workLog.setSplitting(work.getSplitting());
		workLog.setSplitToken(work.getSplitToken());
		workLog.setSplitValue(work.getSplitValue());
		workLog.setSplitTokenList(work.getSplitTokenList());
		workLog.setFromTime(date);
		workLog.setCompleted(false);
		workLog.setConnected(false);
		return workLog;
	}

	protected void connectWorkLog(Activity activity, WorkLog workLog, Work work, String token, Date date)
			throws Exception {
		workLog.setArrivedActivity(activity.getId());
		workLog.setArrivedActivityName(activity.getName());
		workLog.setArrivedActivityToken(token);
		workLog.setArrivedActivityType(activity.getActivityType());
		workLog.setArrivedTime(date);
		workLog.setRoute(work.getDestinationRoute());
		workLog.setRouteName(work.getDestinationRouteName());
		// workLog.setProcessingType(work.getProcessingType());
		workLog.setCompleted(false);
		workLog.setConnected(true);
	}

}
