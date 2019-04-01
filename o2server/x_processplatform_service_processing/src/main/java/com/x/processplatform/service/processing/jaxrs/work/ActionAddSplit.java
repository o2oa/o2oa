package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;

class ActionAddSplit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);

			ActionResult<Wo> result = new ActionResult<>();

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/* 校验work是否存在 */

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			if (!work.getSplitting()) {
				throw new ExceptionNotSplit(work.getId());
			}

			if (StringUtils.isEmpty(wi.getSplitValue())) {
				throw new ExceptionEmptySplitValue(work.getId());
			}

			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob());

			WorkLogTree tree = new WorkLogTree(workLogs);

			WorkLog arrived = workLogs.stream().filter(o -> {
				return StringUtils.equals(o.getId(), wi.getWorkLog());
			}).findFirst().orElse(null);

			WorkLog from = tree.children(arrived).stream().findFirst().orElse(null);

			if (null == arrived) {
				throw new ExceptionInvalidArrivedWorkLog(wi.getWorkLog());
			}

			if (null == from) {
				throw new ExceptionInvalidFromWorkLog(wi.getWorkLog());
			}

			Activity activity = business.element().getActivity(from.getFromActivity());

			emc.beginTransaction(Work.class);
			emc.beginTransaction(WorkLog.class);

			Work workCopy = new Work(work);
			workCopy.setActivity(activity.getId());
			workCopy.setActivityAlias(activity.getAlias());
			workCopy.setActivityArrivedTime(new Date());
			workCopy.setActivityDescription(activity.getDescription());
			workCopy.setActivityName(activity.getName());
			workCopy.setActivityToken(StringTools.uniqueToken());
			workCopy.setActivityType(activity.getActivityType());
			workCopy.setSplitTokenList(arrived.getSplitTokenList());
			workCopy.setSplitToken(arrived.getSplitToken());
			workCopy.setSplitting(from.getSplitting());
			workCopy.getManualTaskIdentityList().clear();
			workCopy.setSplitValue(wi.getSplitValue());
			workCopy.getManualTaskIdentityList().clear();
			workCopy.setBeforeExecuted(false);
			workCopy.setDestinationActivity(null);
			workCopy.setDestinationActivityType(null);
			workCopy.setDestinationRoute(null);
			workCopy.setDestinationRouteName(null);

			WorkLog arrivedCopy = new WorkLog(arrived);
			arrivedCopy.setArrivedActivity(activity.getId());
			arrivedCopy.setArrivedActivityAlias(activity.getAlias());
			arrivedCopy.setArrivedActivityName(activity.getName());
			arrivedCopy.setArrivedActivityToken(workCopy.getActivityToken());
			arrivedCopy.setArrivedActivityType(activity.getActivityType());
			arrivedCopy.setWork(workCopy.getId());
			arrivedCopy.setArrivedTime(workCopy.getActivityArrivedTime());
			arrivedCopy.setSplitValue(workCopy.getSplitValue());

			WorkLog fromCopy = new WorkLog(from);
			fromCopy.setConnected(false);
			fromCopy.setFromActivity(activity.getId());
			fromCopy.setFromActivityAlias(activity.getAlias());
			fromCopy.setFromActivityName(activity.getName());
			fromCopy.setFromActivityType(activity.getActivityType());
			fromCopy.setFromActivityToken(workCopy.getActivityToken());
			fromCopy.setFromTime(workCopy.getActivityArrivedTime());
			fromCopy.setWork(workCopy.getId());
			arrivedCopy.setSplitValue(workCopy.getSplitValue());
			fromCopy.setArrivedActivity("");
			fromCopy.setArrivedActivityAlias("");
			fromCopy.setArrivedActivityName("");
			fromCopy.setArrivedActivityToken("");
			fromCopy.setArrivedActivityType(null);
			fromCopy.setArrivedTime(null);

			emc.persist(workCopy, CheckPersistType.all);
			emc.persist(arrivedCopy, CheckPersistType.all);
			emc.persist(fromCopy, CheckPersistType.all);
			emc.commit();
			Processing processing = new Processing(wi);
			processing.processing(workCopy.getId());
			Wo wo = new Wo();
			wo.setId(workCopy.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends ProcessingAttributes {

		@FieldDescribe("增加的拆分值")
		private String splitValue;

		@FieldDescribe("拆分到的日志")
		private String workLog;

		public String getSplitValue() {
			return splitValue;
		}

		public void setSplitValue(String splitValue) {
			this.splitValue = splitValue;
		}

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

	}

	public static class Wo extends WoId {
	}

}