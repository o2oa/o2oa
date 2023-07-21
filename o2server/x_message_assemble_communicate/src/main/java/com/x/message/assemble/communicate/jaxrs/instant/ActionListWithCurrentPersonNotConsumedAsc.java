package com.x.message.assemble.communicate.jaxrs.instant;

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
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Instant_;

class ActionListWithCurrentPersonNotConsumedAsc extends BaseAction {

	private static final Logger LOGGER= LoggerFactory.getLogger(ActionListWithCurrentPersonNotConsumedAsc.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer count) throws Exception {
		
		LOGGER.debug("execute:{}, count:{}.", effectivePerson::getDistinguishedName, () -> count);
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = this.list(business, NumberUtils.min(200, NumberUtils.max(1, count)), effectivePerson);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> list(Business business, Integer count, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Instant.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Instant> cq = cb.createQuery(Instant.class);
		Root<Instant> root = cq.from(Instant.class);
		Predicate p = cb.equal(root.get(Instant_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Instant_.consumed), false));
		List<Instant> os = em.createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(Instant_.createTime))))
				.setMaxResults(count).getResultList();
		return Wo.copier.copy(os);
	}

	public static class Wo extends Instant {

		private static final long serialVersionUID = 681982898431236763L;
		static WrapCopier<Instant, Wo> copier = WrapCopierFactory.wo(Instant.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}