package com.x.processplatform.service.processing.processor;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.service.processing.Business;

/***
 * 基础的Processor类,属性以及WorkLog处理
 */
public abstract class AbstractBaseProcessor {

	private static Logger logger = LoggerFactory.getLogger(AbstractBaseProcessor.class);

	private EntityManagerContainer entityManagerContainer;

	private Business business;

	protected Gson gson = XGsonBuilder.instance();

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

	protected void arriveActivity(AeiObjects aeiObjects) throws Exception {
		String token = aeiObjects.getWork().getActivityToken();
		if (aeiObjects.getActivityProcessingConfigurator().getChangeActivityToken()) {
			token = StringTools.uniqueToken();
		}
		Date date = new Date();
		if (aeiObjects.getActivityProcessingConfigurator().getStampArrivedWorkLog()) {
			/* 需要创建到达的工作日志 */
			if (ListTools.isNotEmpty(aeiObjects.getWorkLogs())) {
				/* 如果为空的话那么就是新建的工作,不需要连接到达日志 */
				this.stampArriveWorkLog(aeiObjects, token, date);
			}
		}
		if (aeiObjects.getActivityProcessingConfigurator().getCreateFromWorkLog()) {
			this.createFromWorkLog(aeiObjects, token, date);
		}
		aeiObjects.getWork().setActivityToken(token);
		aeiObjects.getWork().setActivityArrivedTime(date);
		aeiObjects.getWork().setActivity(aeiObjects.getActivity().getId());
		aeiObjects.getWork().setActivityName(aeiObjects.getActivity().getName());
		aeiObjects.getWork().setActivityAlias(aeiObjects.getActivity().getAlias());
		aeiObjects.getWork().setActivityDescription(aeiObjects.getActivity().getDescription());
		aeiObjects.getWork().setActivityType(aeiObjects.getActivity().getActivityType());
		aeiObjects.getWork().setWorkStatus(WorkStatus.processing);
		aeiObjects.getWork().setDestinationActivity(null);
		aeiObjects.getWork().setDestinationActivityType(null);
		aeiObjects.getWork().setDestinationRoute(null);
		aeiObjects.getWork().setDestinationRouteName(null);
		aeiObjects.getWork().setErrorRetry(0);
		if (StringUtils.isNotEmpty(aeiObjects.getActivity().getForm())) {
			/** 检查表单存在 */
			Form _form = this.business().element().get(aeiObjects.getActivity().getForm(), Form.class);
			if (null != _form) {
				aeiObjects.getWork().setForm(_form.getId());
			}
		}
	}

	protected WorkLog stampArriveWorkLog(AeiObjects aeiObjects, String token, Date date) throws Exception {
		WorkLog workLog = aeiObjects.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(aeiObjects.getWork().getId(), o.getWork())
						&& StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getFromActivityToken())
						&& BooleanUtils.isNotTrue(o.getConnected()))
				.findFirst().orElse(null);
		if (null != workLog) {
			/* 已经有workLog 进行连接 */
			workLog.setArrivedActivity(aeiObjects.getActivity().getId());
			workLog.setArrivedActivityName(aeiObjects.getActivity().getName());
			workLog.setArrivedActivityToken(token);
			workLog.setArrivedActivityType(aeiObjects.getActivity().getActivityType());
			workLog.setArrivedTime(date);
			workLog.setArrivedGroup(aeiObjects.getActivity().getGroup());
			workLog.setArrivedOpinionGroup(aeiObjects.getActivity().getOpinionGroup());
			workLog.setRoute(aeiObjects.getWork().getDestinationRoute());
			workLog.setRouteName(aeiObjects.getWork().getDestinationRouteName());
			workLog.setCompleted(false);
			workLog.setConnected(true);
			workLog.setDuration(Config.workTime().betweenMinutes(workLog.getFromTime(), workLog.getArrivedTime()));
			aeiObjects.getUpdateWorkLogs().add(workLog);
		} else {
			/* 拆分情况下是有可能这样的情况的,多份Work其中一份已经连接了formActivityToken的WorkLog */
			WorkLog oldest = aeiObjects.getWorkLogs().stream()
					.filter(o -> StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getFromActivityToken()))
					.sorted(Comparator.comparing(WorkLog::getCreateTime, Comparator.nullsLast(Date::compareTo)))
					.findFirst().orElse(null);
			if (null != oldest) {
				// @Todo
				workLog = new WorkLog();
				oldest.copyTo(workLog, JpaObject.ID_DISTRIBUTEFACTOR);
				workLog.setArrivedActivity(aeiObjects.getActivity().getId());
				workLog.setArrivedActivityName(aeiObjects.getActivity().getName());
				workLog.setArrivedActivityToken(token);
				workLog.setArrivedActivityType(aeiObjects.getActivity().getActivityType());
				workLog.setArrivedGroup(aeiObjects.getActivity().getGroup());
				workLog.setArrivedOpinionGroup(aeiObjects.getActivity().getOpinionGroup());
				workLog.setArrivedTime(date);
				workLog.setRoute(aeiObjects.getWork().getDestinationRoute());
				workLog.setRouteName(aeiObjects.getWork().getDestinationRouteName());
				workLog.setCompleted(false);
				workLog.setConnected(true);
				workLog.setDuration(Config.workTime().betweenMinutes(workLog.getFromTime(), workLog.getArrivedTime()));
				aeiObjects.getCreateWorkLogs().add(workLog);
			} else {
				/* 这样的情况应该是不可能的 */
				throw new Exception("不能发生的情况,没有找到任何WorkLog");
			}
		}
		return workLog;
	}

	protected Work copyWork(Work work) throws Exception {
		Work copy = new Work();
		work.copyTo(copy, JpaObject.ID_DISTRIBUTEFACTOR);
		return copy;
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

	protected void createFromWorkLog(AeiObjects aeiObjects, String token, Date date) throws Exception {
		WorkLog workLog = WorkLog.createFromWork(aeiObjects.getWork(), aeiObjects.getActivity(), token, date);
		aeiObjects.getCreateWorkLogs().add(workLog);
	}

}
