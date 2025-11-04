package com.x.processplatform.assemble.surface.jaxrs.task;

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
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;

import io.swagger.v3.oas.annotations.media.Schema;

class V2ListPaging extends V2Base {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}. page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Task> os = list(business, this.adjustPage(page), this.adjustSize(size), p);
			result.setData(Wo.copier.copy(os));
			result.setCount(count(business, p));
			return result;
		}
	}

	private List<Task> list(Business business, Integer page, Integer size, Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		cq.select(root).where(predicate).orderBy(cb.asc(root.get(Task.orderNumber_FIELDNAME)),
				cb.desc(root.get(JpaObject.sequence_FIELDNAME)));
		return em.createQuery(cq).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
	}

	private Long count(Business business, Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		cq.select(cb.count(root)).where(predicate);
		return em.createQuery(cq).getSingleResult();
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.V2ListPaging$Wi")
	public static class Wi extends RelateFilterWi {

		private static final long serialVersionUID = -3322648572903820279L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.V2ListPaging$Wo")
	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = -4773789253221941109L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, false), JpaObject.FieldsInvisible);

	}
}
