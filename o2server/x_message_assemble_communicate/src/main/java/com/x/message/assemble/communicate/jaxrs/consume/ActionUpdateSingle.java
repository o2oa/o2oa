package com.x.message.assemble.communicate.jaxrs.consume;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

class ActionUpdateSingle extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdateSingle.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String type) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			EntityManager em = emc.beginTransaction(Message.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Message> cq = cb.createQuery(Message.class);
			Root<Message> root = cq.from(Message.class);
			Predicate p = cb.isMember(type, root.get(Message_.consumerList));
			p = cb.and(p, cb.equal(root.get(Message_.id), id));
			List<Message> os = em.createQuery(cq.select(root).where(p)).getResultList();
			for (Message o : os) {
				o.getConsumerList().remove(type);
				if (o.getConsumerList().isEmpty()) {
					emc.remove(o, CheckRemoveType.all);
				}
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}