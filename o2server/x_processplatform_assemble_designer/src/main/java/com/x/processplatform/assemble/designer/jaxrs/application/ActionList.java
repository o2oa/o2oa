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

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = list(business, effectivePerson);
			/* 由于有多值字段所以需要全部取出 */
			List<Wo> wos = Wo.copier.copy(emc.list(Application.class, ids));
			wos = business.application().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -7648824521711153693L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class,
				JpaObject.singularAttributeField(Application.class, true, true), JpaObject.FieldsInvisible);

	}

	private List<String> list(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root.get(Application_.id));
		if (!effectivePerson.isSecurityManager() && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			Predicate p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName()));
			cq.where(p);
		}
		return em.createQuery(cq).getResultList();
	}

}
