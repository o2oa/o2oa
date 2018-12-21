package com.x.processplatform.assemble.designer.jaxrs.application;

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
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;

class ActionListWithApplicationCategory extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationCategory) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = Wo.copier.copy(this.list(effectivePerson, business, applicationCategory));
			wos = business.application().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -7648824521711153693L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<Application> list(EffectivePerson effectivePerson, Business business, String applicationCategory)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.equal(root.get(Application_.applicationCategory), applicationCategory);
		if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.ProcessPlatformManager))) {
			p = cb.and(p,
					cb.or(cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)),
							cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName())));
		}
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

}