package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListMyPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListMyPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Task> os = list(business, effectivePerson, this.adjustPage(page), this.adjustSize(size));
			result.setData(Wo.copier.copy(os));
			result.setCount(count(business, effectivePerson));
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionListMyPaging.Wo")
	public static class Wo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class, null, null);

	}

	private List<Task> list(Business business, EffectivePerson effectivePerson, Integer page, Integer size)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
		cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject.sequence_FIELDNAME)));
		return em.createQuery(cq).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
	}

	private Long count(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}
