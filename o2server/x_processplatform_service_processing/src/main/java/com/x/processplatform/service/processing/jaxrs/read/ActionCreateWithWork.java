package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;

class ActionCreateWithWork extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreateWithWork.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug(effectivePerson, "receive work id:{}, jsonElement:{}.", workId, jsonElement);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			List<Read> adds = new ArrayList<>();
			/** work已经存在的read 需要重新发送通知 */
			List<Read> updates = new ArrayList<>();
			for (String identity : business.organization().identity()
					.list(ListTools.trim(wi.getIdentityList(), true, true))) {
				String person = business.organization().person().getWithIdentity(identity);
				String unit = business.organization().unit().getWithIdentity(identity);
				Read o = this.get(business, work, person);
				if (null != o) {
					Date now = new Date();
					o.setStartTime(now);
					o.setStartTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
					o.setViewed(false);
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
				} else {
					Read read = new Read(work, identity, unit, person);
					adds.add(read);
				}
			}
			List<Wo> wos = new ArrayList<Wo>();
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
				emc.commit();
				if (BooleanUtils.isNotFalse(wi.getNotify())) {
					for (Read read : adds) {
						MessageFactory.read_create(read);
					}
					for (Read read : updates) {
						MessageFactory.read_create(read);
					}
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

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
	}

	public Read get(Business business, Work work, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Read> cq = cb.createQuery(Read.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.work), work.getId());
		p = cb.and(p, cb.equal(root.get(Read_.person), person));
		cq.select(root).where(p);
		List<Read> list = em.createQuery(cq).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

}
