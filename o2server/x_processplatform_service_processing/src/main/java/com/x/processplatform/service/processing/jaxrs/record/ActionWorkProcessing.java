package com.x.processplatform.service.processing.jaxrs.record;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkProcessingWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionWorkProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionWorkProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		Param param = init(jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.recordType = wi.getRecordType();
		param.series = wi.getSeries();
		param.opinion = wi.getOpinion();
		param.routeName = wi.getRouteName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}
			param.job = workLog.getJob();
			param.workLog = workLog;
			String identity = business.organization().identity().get(wi.getDistinguishedName());
			if (StringUtils.isNotBlank(identity)) {
				TaskCompleted existTaskCompleted = emc.firstEqualAndEqual(TaskCompleted.class,
						TaskCompleted.job_FIELDNAME, workLog.getJob(), TaskCompleted.identity_FIELDNAME, identity);
				if (null != existTaskCompleted) {
					param.identity = existTaskCompleted.getIdentity();
					param.unit = existTaskCompleted.getUnit();
					param.person = existTaskCompleted.getPerson();
				} else {
					param.identity = identity;
					param.unit = business.organization().unit().getWithIdentity(identity);
					param.person = business.organization().person().getWithIdentity(identity);
				}
			}
		}
		return param;
	}

	private class Param {

		private String job;
		private String recordType;
		private WorkLog workLog;
		private String series;
		private String identity;
		private String unit;
		private String person;
		private String routeName;
		private String opinion;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private CallableImpl(Param param) {
			this.param = param;
		}

		private Param param;

		@Override
		public ActionResult<Wo> call() throws Exception {
			Record rec = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.beginTransaction(Record.class);
				emc.beginTransaction(Task.class);
				Business business = new Business(emc);
				rec = new Record(param.workLog);
				rec.setType(param.recordType);
				// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
				checkIfWorkAlreadyCompleted(business, rec, param.workLog.getJob());
				rec.setIdentity(param.identity);
				rec.setUnit(param.unit);
				rec.setPerson(param.person);
				rec.setRouteName(param.routeName);
				rec.setOpinion(param.opinion);
				elapsed(rec);
				// 已经存在的已办理人员.
				List<String> identities = listJoinInquireTaskCompletedIdentityWithActivityToken(business,
						param.workLog.getJob(), param.workLog.getFromActivityToken());
				List<Task> tasks = listTaskWithSeriesOrActivityToken(business, param.workLog.getJob(),
						param.workLog.getFromActivityToken(), param.series);
				tasks.stream().forEach(o -> {
					o.setPrevTaskIdentityList(identities);
					o.setPrevTaskIdentity(param.identity);
				});
				List<NextManual> nextManuals = tasks.stream()
						.collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
						.map(en -> {
							Optional<Task> opt = en.getValue().stream().findFirst();
							if (opt.isPresent()) {
								NextManual nextManual = new NextManual();
								nextManual.setActivity(opt.get().getActivity());
								nextManual.setActivityAlias(opt.get().getActivityAlias());
								nextManual.setActivityName(opt.get().getActivityName());
								nextManual.setActivityToken(opt.get().getActivityToken());
								nextManual.setActivityType(opt.get().getActivityType());
								nextManual.setTaskIdentityList(
										en.getValue().stream().map(Task::getIdentity).collect(Collectors.toList()));
								return nextManual;
							}
							return null;
						}).filter(Objects::nonNull).collect(Collectors.toList());
				rec.setNextManualList(nextManuals);
				rec.setNextManualTaskIdentityList(
						tasks.stream().map(Task::getIdentity).distinct().collect(Collectors.toList()));
				emc.persist(rec, CheckPersistType.all);
				emc.commit();
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(Wo.copier.copy(rec));
				return result;
			}
		}
	}

	public static class Wi extends ActionWorkProcessingWi {

		private static final long serialVersionUID = 4179509440650818001L;

	}

	public static class Wo extends ActionWorkProcessingWo {

		private static final long serialVersionUID = -6397936830449100267L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}