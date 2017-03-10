package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

class ActionListWithPerson extends ActionBase {

	/**
	 * 1.身份在可使用列表中<br/>
	 * 2.部门在可使用部门中 <br/>
	 * 3.公司在可使用公司中 <br/>
	 * 4.没有限定身份部门或者公司 <br/>
	 * 5.个人在应用管理员中 <br/>
	 * 6.应用的创建人员 <br/>
	 * 7.个人有Manage权限或者ProcessPlatformManager身份
	 */
	ActionResult<List<WrapOutApplication>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutApplication>> result = new ActionResult<>();
			List<WrapOutApplication> wraps = new ArrayList<>();
			List<String> identities = business.organization().identity().ListNameWithPerson(effectivePerson.getName());
			List<String> departments = business.organization().department()
					.ListNameWithPerson(effectivePerson.getName());
			List<String> companies = business.organization().company().ListNameWithPerson(effectivePerson.getName());
			List<String> roles = business.organization().role().listNameWithPerson(effectivePerson.getName());
			List<String> ids = this.list(business, effectivePerson, roles, identities, departments, companies);
			for (String id : ids) {
				Application o = business.application().pick(id);
				wraps.add(applicationOutCopier.copy(o));
			}
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}

	/**
	 * 从可见的application中获取一份ids<br/>
	 * 从可启动的process中获取一份ids <br/>
	 * 两份ids的交集,这样避免列示只有application没有可以启动process的应用
	 */
	private List<String> list(Business business, EffectivePerson effectivePerson, List<String> roles,
			List<String> identities, List<String> departments, List<String> companies) throws Exception {
		List<String> ids = business.application().listAvailable(effectivePerson, roles, identities, departments,
				companies);
		List<String> fromProcessIds = this.listApplicationIdFromProcess(business, effectivePerson, roles, identities,
				departments, companies);
		return ListUtils.intersection(ids, fromProcessIds);
	}

	/**
	 * 
	 * 从Process中获取可以启动的Process的application.
	 */
	private List<String> listApplicationIdFromProcess(Business business, EffectivePerson effectivePerson,
			List<String> roles, List<String> identities, List<String> departments, List<String> companies)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if (effectivePerson.isNotManager() && (!ListTools.contains(roles, RoleDefinition.ProcessPlatformManager))) {
			p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
					cb.isEmpty(root.get(Process_.startableDepartmentList)),
					cb.isEmpty(root.get(Process_.startableCompanyList)));
			p = cb.or(p, cb.isMember(effectivePerson.getName(), root.get(Process_.controllerList)));
			p = cb.or(p, cb.equal(root.get(Process_.creatorPerson), effectivePerson.getName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(departments)) {
				p = cb.or(p, root.get(Process_.startableDepartmentList).in(departments));
			}
			if (ListTools.isNotEmpty(companies)) {
				p = cb.or(p, root.get(Process_.startableCompanyList).in(companies));
			}
		}
		cq.select(root.get(Process_.application)).distinct(true).where(p);
		return em.createQuery(cq).getResultList();
	}
}