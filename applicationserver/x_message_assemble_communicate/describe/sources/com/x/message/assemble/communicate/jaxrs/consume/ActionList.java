package com.x.message.assemble.communicate.jaxrs.consume;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.ImMessage;
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String consume, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = this.list(business, consume, NumberUtils.min(200, NumberUtils.max(1, count)));
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> list(Business business, String consume, Integer count) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Message.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> cq = cb.createQuery(Message.class);
		Root<Message> root = cq.from(Message.class);
		Predicate p = cb.isMember(consume, root.get(Message_.consumerList));
		List<Message> os = em.createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(Message_.createTime))))
				.setMaxResults(count).getResultList();
		return Wo.copier.copy(os);
	}

	public static class Wi extends ImMessage {
	}

	public static class Wo extends Message {

		private static final long serialVersionUID = 681982898431236763L;
		static WrapCopier<Message, Wo> copier = WrapCopierFactory.wo(Message.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Message.consumerList_FIELDNAME));
	}

}