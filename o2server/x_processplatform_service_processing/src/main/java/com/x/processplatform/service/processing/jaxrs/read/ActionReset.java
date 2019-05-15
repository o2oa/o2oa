package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;

class ActionReset extends BaseAction {

	ActionResult<Wo> execute(String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionReadNotExist(id);
			}
			WorkCompleted workCompleted = null;
			Work work = null;
			if (read.getCompleted()) {
				workCompleted = emc.find(read.getWorkCompleted(), WorkCompleted.class);
				if (null == workCompleted) {
					throw new ExceptionWorkCompletedNotExist(read.getWorkCompleted());
				}
			} else {
				work = emc.find(read.getWork(), Work.class);
				if (null == work) {
					throw new ExceptionWorkNotExist(read.getWork());
				}
			}
			List<String> identities = ListTools.trim(business.organization().identity().list(wi.getIdentityList()),
					true, true);
			if (ListTools.isEmpty(identities)) {
				throw new ExceptionResetEmptyIdentity();
			}
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			List<ReadCompleted> exists = this.listExist(business, read);
			Date now = new Date();
			Long duration = Config.workTime().betweenMinutes(read.getStartTime(), now);
			ReadCompleted readCompleted = new ReadCompleted(read, now, duration);
			if (exists.isEmpty()) {
				emc.persist(readCompleted, CheckPersistType.all);
				MessageFactory.readCompleted_create(readCompleted);
			} else {
				for (ReadCompleted o : exists) {
					readCompleted.copyTo(o, JpaObject.FieldsUnmodify);
				}
			}
			List<Read> list = new ArrayList<>();
			for (String str : identities) {
				Read o = null;
				String person = business.organization().person().getWithIdentity(str);
				String unit = business.organization().person().getWithIdentity(str);
				if (read.getCompleted()) {
					o = new Read(workCompleted, str, unit, person);
				} else {
					o = new Read(work, str, unit, person);
				}
				List<Read> os = this.listExist(business, read.getJob(), person);
				if (ListTools.isEmpty(os)) {
					emc.persist(o, CheckPersistType.all);
					list.add(o);
				} else {
					for (Read r : os) {
						o.copyTo(r, JpaObject.FieldsUnmodify);
						list.add(r);
					}
				}
				emc.remove(read);
				emc.commit();
				MessageFactory.read_delete(read);
				for (Read obj : list) {
					MessageFactory.read_create(obj);
				}
				Wo wo = new Wo();
				wo.setId(read.getId());
				result.setData(wo);
			}
			return result;
		}
	}

	private List<ReadCompleted> listExist(Business business, Read read) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ReadCompleted> cq = cb.createQuery(ReadCompleted.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.job), read.getJob());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.person), read.getPerson()));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<Read> listExist(Business business, String job, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Read> cq = cb.createQuery(Read.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.job), job);
		p = cb.and(p, cb.equal(root.get(Read_.person), person));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

}
