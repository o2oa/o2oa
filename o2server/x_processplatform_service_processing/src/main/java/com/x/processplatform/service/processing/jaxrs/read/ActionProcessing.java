package com.x.processplatform.service.processing.jaxrs.read;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;

class ActionProcessing extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug(effectivePerson, "read id:{} processing.", id);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionReadNotExist(id);
			}
			Date now = new Date();
			Long duration = Config.workTime().betweenMinutes(read.getStartTime(), now);
			ReadCompleted readCompleted = new ReadCompleted(read, now, duration);
			List<ReadCompleted> exists = this.listExist(business, read);
			if (exists.isEmpty()) {
				emc.persist(readCompleted, CheckPersistType.all);
				MessageFactory.readCompleted_create(readCompleted);
			} else {
				for (ReadCompleted o : exists) {
					readCompleted.copyTo(o, JpaObject.FieldsUnmodify);
				}
			}
			emc.remove(read, CheckRemoveType.all);
			emc.commit();
			MessageFactory.read_to_readCompleted(readCompleted);
			// MessageFactory.read_delete(read);
			Wo wo = new Wo();
			wo.setId(read.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
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

}
