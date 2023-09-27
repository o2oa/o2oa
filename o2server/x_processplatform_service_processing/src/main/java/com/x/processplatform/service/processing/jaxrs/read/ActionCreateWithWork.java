package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

/**
 * 
 * @author zhour 对工作添加待阅,选择是否重发通知
 */
class ActionCreateWithWork extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWork.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<Wo>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(workId, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Work work = emc.find(workId, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(workId, Work.class);
					}
					List<Read> adds = new ArrayList<>();
					List<Read> updates = new ArrayList<>();
					List<Review> addReviews = new ArrayList<>();
					for (String identity : business.organization().identity()
							.list(ListTools.trim(wi.getIdentityList(), true, true))) {
						String person = business.organization().person().getWithIdentity(identity);
						String unit = business.organization().unit().getWithIdentity(identity);
						Read o = get(business, work, person);
						if (null != o) {
							Date now = new Date();
							o.setStartTime(now);
							o.setStartTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
							o.setWork(work.getId());
							o.setActivity(work.getActivity());
							o.setActivityName(work.getActivityName());
							o.setActivityToken(work.getActivityToken());
							o.setActivityType(work.getActivityType());
							o.setCreatorIdentity(work.getCreatorIdentity());
							o.setCreatorPerson(work.getCreatorPerson());
							o.setCreatorUnit(work.getCreatorUnit());
							o.setJob(work.getJob());
							o.setSerial(work.getSerial());
							o.setTitle(work.getTitle());
							o.setIdentity(identity);
							o.setPerson(person);
							o.setUnit(unit);
							updates.add(o);
							/* 同步映射字段 */
							o.copyProjectionFields(work);
						} else {
							Read read = new Read(work, identity, unit, person);
							adds.add(read);
						}
						if (count(business, work, person) < 1) {
							Review review = new Review(work, person);
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

		private static final long serialVersionUID = -3057273207744336553L;

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

		private static final long serialVersionUID = 1437089312580379170L;

	}

	public Read get(Business business, Work work, String person) throws Exception {
		List<Read> os = business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
				work.getJob(), Read.person_FIELDNAME, person);
		return os.stream().findFirst().orElse(null);
	}

	public Long count(Business business, Work work, String person) throws Exception {
		return business.entityManagerContainer().countEqualAndEqual(Review.class, Review.job_FIELDNAME, work.getJob(),
				Review.person_FIELDNAME, person);
	}

}
