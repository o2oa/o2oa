package com.x.processplatform.assemble.surface.jaxrs.querystat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryStat_;

class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	ActionResult<List<WrapOutQueryStat>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationFlag);
			}
			ActionResult<List<WrapOutQueryStat>> result = new ActionResult<>();
			List<WrapOutQueryStat> wraps = new ArrayList<>();
			List<String> ids = this.list(business, effectivePerson, application);
			List<QueryStat> os = business.entityManagerContainer().list(QueryStat.class, ids);
			wraps = outCopier.copy(os);
			SortTools.asc(wraps, true, "name");
			result.setData(wraps);
			return result;
		}
	}

	private List<String> list(Business business, EffectivePerson effectivePerson, Application application)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(QueryStat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryStat> root = cq.from(QueryStat.class);
		Predicate p = cb.conjunction();
		/* 不是管理员或者流程管理员 */
		if (effectivePerson.isNotManager()
				&& (!business.organization().person().hasRole(effectivePerson.getDistinguishedName(),
						OrganizationDefinition.ProcessPlatformManager, OrganizationDefinition.Manager))
				&& effectivePerson.isNotPerson(application.getControllerList())) {
			p = cb.equal(root.get(QueryStat_.creatorPerson), effectivePerson.getDistinguishedName());
			p = cb.or(p, root.get(QueryStat_.controllerList).in(effectivePerson.getDistinguishedName()));
			p = cb.or(p,
					cb.and(cb.isEmpty(root.get(QueryStat_.availablePersonList)),
							cb.isEmpty(root.get(QueryStat_.availableUnitList)),
							cb.isEmpty(root.get(QueryStat_.availableIdentityList))));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(QueryStat_.availablePersonList)));
			p = cb.or(p, root.get(QueryStat_.availableUnitList)
					.in(business.organization().unit().listWithPersonSupNested(effectivePerson)));
			p = cb.or(p, root.get(QueryStat_.availableIdentityList)
					.in(business.organization().identity().listWithPerson(effectivePerson)));
		}
		p = cb.and(p, cb.equal(root.get(QueryStat_.application), application.getId()));
		cq.select(root.get(QueryStat_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

}