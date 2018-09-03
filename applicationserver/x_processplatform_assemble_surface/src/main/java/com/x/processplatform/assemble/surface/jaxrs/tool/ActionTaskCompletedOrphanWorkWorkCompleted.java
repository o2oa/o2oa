package com.x.processplatform.assemble.surface.jaxrs.tool;

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
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;

class ActionTaskCompletedOrphanWorkWorkCompleted extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> workIds = emc.ids(Work.class);
			List<Wo> wos = emc.fetch(this.listOrphanTask(business, workIds), Wo.copier);
			result.setData(wos);
			return result;
		}
	}

	private List<String> listOrphanTask(Business business, List<String> workIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.not(root.get(Task_.work).in(workIds));
		cq.select(root.get(Task_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public static class Wo extends Task {

		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Task.title_FIELDNAME, Task.identity_FIELDNAME,
						Task.process_FIELDNAME, Task.processName_FIELDNAME),
				null);

	}
}