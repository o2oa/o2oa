package com.x.portal.assemble.surface.jaxrs.portal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

class ActionList extends BaseAction {

	/**
	 * 1.身份在可使用列表中 2.部门在可使用部门中 3.公司在可使用公司中 4.没有限定身份,部门或者公司 5.个人在应用管理员中
	 * 6.是此Portal的创建人员 7.个人有Manage权限 8.个人拥有PortalManager
	 */
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if(effectivePerson.isAnonymous()){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = this.list(business, effectivePerson);
			for (String id : ids) {
				Portal o = business.portal().pick(id);
				if (null == o) {
					throw new ExceptionPortalNotExist(id);
				} else {
					wos.add(Wo.copier.copy(o));
				}
			}
			wos = wos.stream().sorted(Comparator.comparing(Portal::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	private List<String> list(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.or(cb.isNull(root.get(Portal_.pcClient)), cb.isTrue(root.get(Portal_.pcClient)));
		if (effectivePerson.isNotManager()
				&& (!business.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager))) {
			List<String> identities = business.organization().identity()
					.listWithPerson(effectivePerson.getDistinguishedName());
			List<String> units = business.organization().unit()
					.listWithPersonSupNested(effectivePerson.getDistinguishedName());
			Predicate who = cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName());
			who = cb.or(who, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList)));
			who = cb.or(who, cb.and(cb.isEmpty(root.get(Portal_.availableIdentityList)),
					cb.isEmpty(root.get(Portal_.availableUnitList))));
			who = cb.or(who, root.get(Portal_.availableIdentityList).in(identities));
			who = cb.or(who, root.get(Portal_.availableUnitList).in(units));
			p = cb.and(p, who);
		}
		cq.select(root.get(Portal_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public static class Wo extends Portal {

		private static final long serialVersionUID = -5240059905993945729L;
		static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class,
				JpaObject.singularAttributeField(Portal.class, true, false), JpaObject.FieldsInvisible);

	}
}
