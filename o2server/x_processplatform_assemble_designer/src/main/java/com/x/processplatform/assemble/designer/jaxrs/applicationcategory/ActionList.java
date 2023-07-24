package com.x.processplatform.assemble.designer.jaxrs.applicationcategory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			List<String> list = this.list(business, effectivePerson);
			list.stream().collect(Collectors.groupingBy(str -> Objects.toString(str, ""), Collectors.counting()))
					.entrySet().stream()
					.sorted(Map.Entry.<String, Long>comparingByKey(Comparator.nullsLast(String::compareTo)))
					.forEach(o -> {
						Wo wo = new Wo();
						wo.setApplicationCategory(o.getKey());
						wo.setCount(o.getValue());
						wos.add(wo);
					});
			result.setData(wos);
			return result;
		}
	}

	/** 如果是isManager列示所有应用，如果不是则判断权限 */
	private List<String> list(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root.get(Application_.applicationCategory)).distinct(false);
		if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			Predicate p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName()));
			cq.where(p);
		}
		return em.createQuery(cq).getResultList();
	}

	public class Wo extends GsonPropertyObject {

		private String applicationCategory;
		private Long count;

		public String getApplicationCategory() {
			return applicationCategory;
		}

		public void setApplicationCategory(String applicationCategory) {
			this.applicationCategory = applicationCategory;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}
	}

}