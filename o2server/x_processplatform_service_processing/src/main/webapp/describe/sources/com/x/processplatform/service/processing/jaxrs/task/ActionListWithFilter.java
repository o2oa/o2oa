package com.x.processplatform.service.processing.jaxrs.task;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.service.processing.Business;

class ActionListWithFilter extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer count, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(Task.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Task> cq = cb.createQuery(Task.class);
			Root<Task> root = cq.from(Task.class);
			if (wi.isEmpty()) {
				throw new ExceptionEmptyFilter();
			}
			if (count == null || count < 1) {
				throw new ExceptionInvalidCount(count);
			}
			Predicate p = cb.conjunction();
			if (ListTools.isNotEmpty(wi.getPersonList())) {
				p = cb.and(p, root.get(Task_.person).in(wi.getPersonList()));
			}
			if (ListTools.isNotEmpty(wi.getApplicationList())) {
				p = cb.and(p, root.get(Task_.application).in(wi.getApplicationList()));
			}
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				p = cb.and(p, root.get(Task_.process).in(wi.getProcessList()));
			}
			if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
				p = cb.and(p, root.get(Task_.creatorUnit).in(wi.getCreatorUnitList()));
			}
			List<Task> os = em.createQuery(cq.select(root).where(p)).setMaxResults(count).getResultList();
			List<Wo> wos = Wo.copier.copy(os);
			wos = wos.stream().sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

		public boolean isEmpty() {
			if (ListTools.isEmpty(this.getPersonList()) && ListTools.isEmpty(this.getApplicationList())
					&& ListTools.isEmpty(this.getProcessList()) && ListTools.isEmpty(this.getCreatorUnitList())) {
				return true;
			}
			return false;
		}

		private List<String> personList;

		private List<String> applicationList;

		private List<String> processList;

		private List<String> creatorUnitList;

		public List<String> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<String> applicationList) {
			this.applicationList = applicationList;
		}

		public List<String> getProcessList() {
			return processList;
		}

		public void setProcessList(List<String> processList) {
			this.processList = processList;
		}

		public List<String> getCreatorUnitList() {
			return creatorUnitList;
		}

		public void setCreatorUnitList(List<String> creatorUnitList) {
			this.creatorUnitList = creatorUnitList;
		}

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	public static class Wo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
