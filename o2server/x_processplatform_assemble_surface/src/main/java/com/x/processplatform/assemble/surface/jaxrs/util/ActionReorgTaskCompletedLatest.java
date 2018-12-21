package com.x.processplatform.assemble.surface.jaxrs.util;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

public class ActionReorgTaskCompletedLatest extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = new Wo();
			wo.setValue(0);
			this.listJobs(business).stream().forEach(o -> {
				try {
					emc.beginTransaction(TaskCompleted.class);
					emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, o).stream()
							.collect(Collectors.groupingBy(TaskCompleted::getPerson)).values().stream().forEach(l -> {
								List<TaskCompleted> os = l.stream()
										.sorted(Comparator.comparing(TaskCompleted::getCompletedTime,
												Comparator.nullsFirst(Date::compareTo)).reversed())
										.collect(Collectors.toList());
								os.get(0).setLatest(true);
								for (int i = 1; i < os.size(); i++) {
									os.get(i).setLatest(false);
								}
								wo.setValue(wo.getValue() + os.size());
							});
					emc.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			result.setData(wo);
			return result;
		}
	}

	private List<String> listJobs(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		cq.select(root.get(TaskCompleted_.job)).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public static class WoControl extends WorkControl {
	}

	public static class Wo extends WrapInteger {

	}

}
