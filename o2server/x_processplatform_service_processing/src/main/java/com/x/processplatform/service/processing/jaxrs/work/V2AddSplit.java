package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddSplitWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddSplitWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class V2AddSplit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2AddSplit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id, jsonElement);

		Callable<ActionResult<Wo>> callable = new CallableImpl(id, param.splitValueList, param.workLog);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.splitValueList = wi.getSplitValueList();
		param.workLog = wi.getWorkLog();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.job = work.getJob();
		}
		return param;
	}

	private class Param {

		private String job;
		private List<String> splitValueList;
		private String workLog;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;
		private List<String> splitValueList;
		private Boolean trimExist;
		private String workLog;

		private CallableImpl(String id, List<String> splitValueList, String workLog) {

			this.id = id;
			this.splitValueList = splitValueList;
			this.workLog = workLog;

		}

		@Override
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

				if (BooleanUtils.isTrue(trimExist)) {
					splitValueList = ListUtils.subtract(splitValueList, work.getSplitTokenList());
				}

				if (ListTools.isEmpty(splitValueList)) {
					throw new ExceptionEmptySplitValue(work.getId());
				}

				List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob());

				WorkLogTree tree = new WorkLogTree(workLogs);

				WorkLog arrived = workLogs.stream().filter(o -> StringUtils.equals(o.getId(), workLog)).findFirst()
						.orElse(null);

				WorkLog from = tree.children(arrived).stream().findFirst().orElse(null);

				if (null == arrived) {
					throw new ExceptionInvalidArrivedWorkLog(workLog);
				}

				if (null == from) {
					throw new ExceptionInvalidFromWorkLog(workLog);
				}

				Activity activity = business.element().getActivity(from.getFromActivity());

				for (String splitValue : splitValueList) {

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

					workCopy.setSplitValueList(adjustSplitValueList(arrived.getSplitValueList(), splitValue));
					workCopy.setSplitToken(arrived.getSplitToken());

					workCopy.setSplitTokenList(arrived.getSplitTokenList());
					workCopy.setSplitValue(splitValue);
					workCopy.setSplitting(arrived.getSplitting());
					Map<String, String> splitValueMap = workCopy.getSplitTokenValueMap();
					splitValueMap.put(workCopy.getSplitToken(), workCopy.getSplitValue());
					workCopy.setSplitTokenValueMap(splitValueMap);
					workCopy.setTickets(null);
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
					arrivedCopy.setSplitValueList(workCopy.getSplitValueList());
					arrivedCopy.setSplitTokenList(workCopy.getSplitTokenList());
					arrivedCopy.setSplitToken(workCopy.getSplitToken());
					arrivedCopy.setSplitValue(workCopy.getSplitValue());
					arrivedCopy.setSplitting(workCopy.getSplitting());

					WorkLog fromCopy = new WorkLog(from);
					fromCopy.setConnected(false);
					fromCopy.setFromActivity(activity.getId());
					fromCopy.setFromActivityAlias(activity.getAlias());
					fromCopy.setFromActivityName(activity.getName());
					fromCopy.setFromActivityType(activity.getActivityType());
					fromCopy.setFromActivityToken(workCopy.getActivityToken());
					fromCopy.setFromTime(workCopy.getActivityArrivedTime());
					fromCopy.setWork(workCopy.getId());
					fromCopy.setSplitValueList(workCopy.getSplitValueList());
					fromCopy.setSplitTokenList(workCopy.getSplitTokenList());
					fromCopy.setSplitToken(workCopy.getSplitToken());
					fromCopy.setSplitValue(workCopy.getSplitValue());
					fromCopy.setSplitting(workCopy.getSplitting());
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

		/**
		 * 替换掉最后一个值
		 * 
		 * @param list
		 * @param value
		 * @return
		 */
		private List<String> adjustSplitValueList(List<String> list, String value) {
			List<String> values = new ArrayList<>();
			if (ListTools.isNotEmpty(list)) {
				list.stream().limit(list.size() - 1L).forEach(values::add);
			}
			values.add(value);
			return values;
		}
	}

	public static class Wi extends V2AddSplitWi {

		private static final long serialVersionUID = 6460190818209523936L;

	}

	public static class Wo extends V2AddSplitWo {

		private static final long serialVersionUID = -5717489826043523199L;

	}

}