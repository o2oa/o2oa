package com.x.processplatform.assemble.designer.jaxrs.workcompleted;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionMergeDataItem extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String processFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = emc.find(processFlag, Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(processFlag, Process.class);
			}
			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(processFlag, Application.class);
			}

			List<String> ids = this.list(business, process);
			do {

				ids = this.list(business, process);
			} while (ListTools.isNotEmpty(ids));

			return result;
		}
	}

	private void update(Business business, List<String> ids) throws Exception {
		business.entityManagerContainer().beginTransaction(WorkCompleted.class);
		WorkCompleted workCompleted = null;
		for (String id : ids) {
			workCompleted = business.entityManagerContainer().find(id, WorkCompleted.class);
			if (null != workCompleted) {

			}
		}
		business.entityManagerContainer().commit();
	}

	private List<String> list(Business business, Process process) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.process), process.getId());
		p = cb.and(p, cb.or(cb.isNull(root.get(WorkCompleted_.data)), cb.equal(root.get(WorkCompleted_.data), "")));
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).setMaxResults(100).getResultList();
	}

	public static class Wo extends WrapBoolean {


	}

}