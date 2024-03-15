package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionCreateWithWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWorkCompleted.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workCompletedId, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(workCompletedId, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(workCompletedId, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}
		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class);
					if (null == workCompleted) {
						throw new ExceptionEntityNotExist(workCompletedId, WorkCompleted.class);
					}
					/* 取workLog补充WorkCompleted不足字段 */
					List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME,
							workCompleted.getJob());
					workLogs = workLogs.stream()
							.sorted(Comparator.comparing(WorkLog::getFromTime, Comparator.nullsLast(Date::compareTo))
									.thenComparing(WorkLog::getArrivedTime, Comparator.nullsLast(Date::compareTo)))
							.collect(Collectors.toList());
					WorkLog workLog = new WorkLog();
					workLog.setArrivedActivityToken(UUID.randomUUID().toString());
					if (!workLogs.isEmpty()) {
						workLog = workLogs.get(workLogs.size() - 1);
					}
					List<Read> adds = new ArrayList<>();
					List<Read> updates = new ArrayList<>();
					List<Review> addReviews = new ArrayList<>();
					for (String identity : business.organization().identity()
							.list(ListTools.trim(wi.getIdentityList(), true, true))) {
						String unit = business.organization().unit().getWithIdentity(identity);
						String person = business.organization().person().getWithIdentity(identity);
						Read o = get(business, workCompleted, person);
						if (null != o) {
							Date now = new Date();
							o.setStartTime(now);
							o.setStartTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
							o.setWorkCompleted(workCompleted.getId());
							o.setCreatorIdentity(workCompleted.getCreatorIdentity());
							o.setCreatorPerson(workCompleted.getCreatorPerson());
							o.setCreatorUnit(workCompleted.getCreatorUnit());
							o.setJob(workCompleted.getJob());
							o.setSerial(workCompleted.getSerial());
							o.setTitle(workCompleted.getTitle());
							o.setIdentity(identity);
							o.setPerson(person);
							o.setUnit(unit);
							o.copyProjectionFields(workCompleted);
							updates.add(o);
						} else {
							Read read = new Read(workCompleted, identity, unit, person);
							read.setActivity(workLog.getArrivedActivity());
							read.setActivityName(workLog.getArrivedActivityName());
							read.setActivityType(workLog.getArrivedActivityType());
							read.setActivityAlias(workLog.getArrivedActivityAlias());
							read.setActivityToken(workLog.getArrivedActivityToken());
							adds.add(read);
						}
						if (count(business, workCompleted, person) < 1) {
							Review review = new Review(workCompleted, person);
							addReviews.add(review);
						}
					}

					if (!adds.isEmpty() || (!updates.isEmpty())) {
						emc.beginTransaction(Read.class);
						for (Read o : adds) {
							emc.persist(o, CheckPersistType.all);
							Wo wo = new Wo();
							wo.setId(o.getId());
							wos.add(wo);
						}
						for (Read o : updates) {
							emc.check(o, CheckPersistType.all);
							Wo wo = new Wo();
							wo.setId(o.getId());
							wos.add(wo);
						}
						if (!addReviews.isEmpty()) {
							emc.beginTransaction(Review.class);
							for (Review o : addReviews) {
								emc.persist(o, CheckPersistType.all);
							}
						}
						emc.commit();
						if (BooleanUtils.isTrue(wi.getNotify())) {
							for (Read read : adds) {
								MessageFactory.read_create(read);
							}
							for (Read read : updates) {
								MessageFactory.read_create(read);
							}
						}
					}
				}
				return "";
			}
		};

		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wos);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -2275510505339993205L;

		@FieldDescribe("待阅标识")
		private List<String> identityList = new ArrayList<>();

		@FieldDescribe("发送待阅通知")
		private Boolean notify = false;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public Boolean getNotify() {
			return notify;
		}

		public void setNotify(Boolean notify) {
			this.notify = notify;
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -1692814680748900168L;
	}

	public Read get(Business business, WorkCompleted workCompleted, String person) throws Exception {
		List<Read> os = business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
				workCompleted.getJob(), Read.person_FIELDNAME, person);
		return os.stream().findFirst().orElse(null);
	}

	public Long count(Business business, WorkCompleted workCompleted, String person) throws Exception {
		return business.entityManagerContainer().countEqualAndEqual(Review.class, Review.job_FIELDNAME,
				workCompleted.getJob(), Review.person_FIELDNAME, person);
	}

}
