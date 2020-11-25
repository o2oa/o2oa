package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddSplitWi;
import com.x.processplatform.service.processing.Business;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class V2AddSplit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		final String job;
		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			job = work.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {

			public ActionResult<Wo> call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

					ActionResult<Wo> result = new ActionResult<>();
					Wo wo = new Wo();

					Business business = new Business(emc);

					/* 校验work是否存在 */
					Work work = emc.find(id, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(id, Work.class);
					}

					if (BooleanUtils.isNotTrue(work.getSplitting())) {
						throw new ExceptionNotSplit(work.getId());
					}
					if (ListTools.isEmpty(wi.getSplitValueList())) {
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

					for (String splitValue : wi.getSplitValueList()) {

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
						workCopy.setSplitTokenList(arrived.getProperties().getSplitTokenList());
						workCopy.setSplitToken(arrived.getSplitToken());
						workCopy.setSplitting(from.getSplitting());
						workCopy.setSplitValue(splitValue);
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
						wo.addValue(workCopy.getId(), true);
					}
					result.setData(wo);
					return result;
				}
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wi extends V2AddSplitWi {

	}

	public static class Wo extends WrapStringList {

	}

}