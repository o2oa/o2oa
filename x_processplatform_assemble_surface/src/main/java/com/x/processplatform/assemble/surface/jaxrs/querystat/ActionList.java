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
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryStat_;

class ActionList extends ActionBase {

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
				&& (!business.organization().role().hasAny(effectivePerson.getName(),
						RoleDefinition.ProcessPlatformManager, RoleDefinition.Manager))
				&& effectivePerson.isNotUser(application.getControllerList())) {
			p = cb.equal(root.get(QueryStat_.creatorPerson), effectivePerson.getName());
			p = cb.or(p, root.get(QueryStat_.controllerList).in(effectivePerson.getName()));
			p = cb.or(p,
					cb.and(cb.isEmpty(root.get(QueryStat_.availablePersonList)),
							cb.isEmpty(root.get(QueryStat_.availableCompanyList)),
							cb.isEmpty(root.get(QueryStat_.availableDepartmentList)),
							cb.isEmpty(root.get(QueryStat_.availableIdentityList))));
			p = cb.or(p, cb.isMember(effectivePerson.getName(), root.get(QueryStat_.availablePersonList)));
			p = cb.or(p, root.get(QueryStat_.availableCompanyList)
					.in(this.listCompany(business, effectivePerson.getName())));
			p = cb.or(p, root.get(QueryStat_.availableDepartmentList)
					.in(this.listDepartment(business, effectivePerson.getName())));
			p = cb.or(p, root.get(QueryStat_.availableIdentityList)
					.in(this.listIdentity(business, effectivePerson.getName())));
		}
		p = cb.and(p, cb.equal(root.get(QueryStat_.application), application.getId()));
		cq.select(root.get(QueryStat_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private List<String> listIdentity(Business business, String name) throws Exception {
		List<WrapIdentity> list = business.organization().identity().listWithPerson(name);
		return ListTools.extractProperty(list, "name", String.class, true, true);
	}

	private List<String> listDepartment(Business business, String name) throws Exception {
		List<WrapDepartment> list = business.organization().department().listWithPerson(name);
		return ListTools.extractProperty(list, "name", String.class, true, true);
	}

	private List<String> listCompany(Business business, String name) throws Exception {
		List<WrapCompany> list = business.organization().company().listWithPerson(name);
		return ListTools.extractProperty(list, "name", String.class, true, true);
	}

}