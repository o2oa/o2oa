package com.x.query.assemble.surface.jaxrs.query;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Query_;

class ActionListWithPersonLike extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithPersonLike.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String key) throws Exception {
		logger.debug(effectivePerson, "{}.", effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			/** 去除部门以及上级部门,如果设置了一级部门可用,那么一级部门下属的二级部门也可用 */
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			List<String> ids = this.list(business, effectivePerson, identities, units, key);
			List<Query> os = business.query().pick(ids);
			List<Wo> wos = Wo.copier.copy(os);
			wos = business.query().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Query {

		private static final long serialVersionUID = -4862564047240738097L;

		static WrapCopier<Query, Wo> copier = WrapCopierFactory.wo(Query.class, Wo.class,
				JpaObject.singularAttributeField(Query.class, true, false), null);

	}

	private List<String> list(Business business, EffectivePerson effectivePerson, List<String> identities,
			List<String> units, String key) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Query.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Query> root = cq.from(Query.class);
		Predicate p = cb.conjunction();
		if (StringUtils.isNotEmpty(key)) {
			p = cb.and(p, cb.or(cb.like(root.get(Query_.name), "%" + key + "%"),
					cb.like(root.get(Query_.alias), "%" + key + "%")));
		}
		if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			p = cb.and(cb.isEmpty(root.get(Query_.availableIdentityList)),
					cb.isEmpty(root.get(Query_.availableUnitList)));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Query_.controllerList)));
			p = cb.or(p, cb.equal(root.get(Query_.creatorPerson), effectivePerson.getDistinguishedName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Query_.availableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Query_.availableUnitList).in(units));
			}
		}
		cq.select(root.get(Query_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}
}