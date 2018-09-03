package com.x.processplatform.assemble.surface.jaxrs.data;

import java.util.ArrayList;
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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;

class ActionGetWithJob extends BaseAction {

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String job) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			if ((!this.manager(business, effectivePerson)) && (emc.countEqual(Review.class, Review.person_FIELDNAME,
					effectivePerson.getDistinguishedName()) == 0)
			// !this.has(business, effectivePerson, job, Review.class))
			// && (!this.has(business, effectivePerson, job, TaskCompleted.class))
			// && (!this.has(business, effectivePerson, job, ReadCompleted.class))
			// && (!this.has(business, effectivePerson, job, Task.class))
			// && (!this.has(business, effectivePerson, job, Read.class))
					&& (!this.applicationControl(business, effectivePerson, job))) {
				throw new ExceptionJobAccessDenied(effectivePerson.getName(), job);
			}
			result.setData(this.getData(business, job));
			return result;
		}
	}

	private Boolean manager(Business business, EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		return (business.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager));
	}

	private Boolean applicationControl(Business business, EffectivePerson effectivePerson, String job)
			throws Exception {
		List<String> ids = new ArrayList<>();
		ids.addAll(this.listApplicationWithWork(business, job));
		ids.addAll(this.listApplicationWithWorkCompleted(business, job));
		ids = ListTools.trim(ids, true, true);
		List<Application> os = business.application().pick(ids);
		for (Application o : os) {
			if (ListTools.contains(o.getControllerList(), effectivePerson.getDistinguishedName())) {
				return true;
			}
		}
		return false;
	}

	private <T extends JpaObject> List<String> listApplicationWithWork(Business business, String job) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get("job"), job);
		cq.select(root.get(Work_.application)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private <T extends JpaObject> List<String> listApplicationWithWorkCompleted(Business business, String job)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get("job"), job);
		cq.select(root.get(WorkCompleted_.application)).where(p);
		return em.createQuery(cq).getResultList();
	}

}