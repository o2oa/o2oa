package com.x.processplatform.assemble.surface.jaxrs.queryview;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryView;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.element.QueryView_;

public class ActionList extends ActionBase {

	public ActionResult<List<WrapOutQueryView>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
			List<WrapOutQueryView> wraps = new ArrayList<>();
			List<String> ids = this.list(business, effectivePerson, application);
			List<QueryView> os = business.entityManagerContainer().list(QueryView.class, ids);
			wraps = outCopier.copy(os);
			SortTools.asc(wraps, true, "name");
			result.setData(wraps);
			return result;
		}
	}

	private List<String> list(Business business, EffectivePerson effectivePerson, Application application)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.conjunction();
		/* 不是管理员或者流程管理员 */
		if (effectivePerson.isNotManager()
				&& (!business.organization().role().hasAny(effectivePerson.getName(),
						RoleDefinition.ProcessPlatformManager, RoleDefinition.Manager))
				&& effectivePerson.isNotUser(application.getControllerList())) {
			p = cb.equal(root.get(QueryView_.creatorPerson), effectivePerson.getName());
			p = cb.or(p, root.get(QueryView_.controllerList).in(effectivePerson.getName()));
			p = cb.or(p,
					cb.and(cb.isEmpty(root.get(QueryView_.availablePersonList)),
							cb.isEmpty(root.get(QueryView_.availableCompanyList)),
							cb.isEmpty(root.get(QueryView_.availableDepartmentList)),
							cb.isEmpty(root.get(QueryView_.availableIdentityList))));
			p = cb.or(p, cb.isMember(effectivePerson.getName(), root.get(QueryView_.availablePersonList)));
			p = cb.or(p, root.get(QueryView_.availableCompanyList)
					.in(this.listCompany(business, effectivePerson.getName())));
			p = cb.or(p, root.get(QueryView_.availableDepartmentList)
					.in(this.listDepartment(business, effectivePerson.getName())));
			p = cb.or(p, root.get(QueryView_.availableIdentityList)
					.in(this.listIdentity(business, effectivePerson.getName())));
		}
		cq.select(root.get(QueryView_.id)).where(p).distinct(true);
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